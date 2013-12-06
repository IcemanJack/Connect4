package connect4.server;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import connect4.client.IModelListener;
import connect4.server.CaseType;
import connect4.server.database.Database;
import connect4.server.database.IDatabase;
import connect4.server.database.MockDatabase;
import connect4.server.database.User;
import connect4.server.database.User.UserType;

/* TODO
 * change all return null
 * throw custom exceptions, in server catch them
 * call super to get data from private classes
 */

public class Model implements IModel
{
	private IDatabase database;
	private CaseType[][] board;
	// Name, User Object
	// changed to tree temp tests
	private Map<String, User> users = Collections.synchronizedMap(new TreeMap<String, User>());
	
	private AtomicInteger columns = new AtomicInteger();
	private AtomicInteger rows = new AtomicInteger();
	private AtomicInteger connectToWin = new AtomicInteger();
	
	private String player1 = "";
	private String player2 = "";
	private String currentPlayer = "";
	
	public Model()
	{
		this.columns.set(7);
		this.rows.set(6);
		this.connectToWin.set(4);
		makeNewBoard();
	}
	public Model(int columns, int rows, int connectToWin)
	{
		this.columns.set(columns);
		this.rows.set(rows);
		this.connectToWin.set(connectToWin);
		makeNewBoard();
	}
	
	public boolean connectToDatabase()
	{
		try
		{
			database = new Database();
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return false;
		}
		catch(UnsupportedClassVersionError e)
		{
			System.err.println(e.getMessage());
			return false;
		}
		catch(ClassNotFoundException e)
		{
			System.err.println(e.getMessage());
			return false;
		}
		return true;
	}
	
	public void fallBackOnMock()
	{
		database = new MockDatabase();
	}
	
	@Override
	public String addClient(String username, IModelListener client)
	{
		if(containsUser(username))
		{
			username = getNextAvailableUsername(username, username.concat("0"), 0);
		}
		
		UserType type =  UserType.SPECTATOR;
		if(playerAvailable())
		{
			addNewPlayer(username);
			type = UserType.PLAYER;
		}
		
		User user = new User(username, client, type);
		users.put(username, user);
		
		printListOfUsernamesInList("New player added. New u ");
		return username;
	}
	
	@Override
	public void removeClient(String username)
	{
		removePlayer(username);
		users.remove(username);
		printListOfUsernamesInList("Player removed. New list ");
	}
	
	
	@Override
	public void initializeClientBoard(IModelListener client) 
	{
		ExecutorService executor = Executors.newFixedThreadPool(1);
		Runnable task = new InitializeListenerBoard(client, columns.get(), rows.get());
		executor.execute(task);
		executor.shutdown();
		System.out.println("initializeClientBoard done");
	}
	
	@Override
	public void updateClientUsername(String name, IModelListener client)
	{
		Runnable task;
		ExecutorService executor = Executors.newFixedThreadPool(1);
		task = new UpdateListenerUsername(client, name);
		System.out.println("executing");
		executor.execute(task);
		System.out.println("shuting down");
		executor.shutdown();
		System.out.println(name + " updated ");
	}
	
	@Override
	public void updateClientsCurrentPlayer() 
	{
		IModelListener[] listeners = getClientsListeners();
		System.out.println("dfffffffff"+listeners.length);
		ExecutorService executor = Executors.newFixedThreadPool(listeners.length);
		Runnable task;
		for(IModelListener listener: listeners)
		{
			if(listener == null)
			{
				System.out.println("cheval");
			}
			task = new UpdateListenerCurrentPlayer(listener, currentPlayer);
			executor.execute(task);
		}
		executor.shutdown();
	}
	
	@Override
	public void updateClientsBoardCase(int column, int row, String player)
	{
		CaseType caseType = getPlayerCaseType(player);
		IModelListener[] listeners = getClientsListeners();
		ExecutorService executor = Executors.newFixedThreadPool(listeners.length);
		Runnable task;
		for(IModelListener listener: listeners)
		{
			task = new UpdateListenerBoardCase(listener, column, row, caseType);
			executor.execute(task);
		}
		executor.shutdown();
	}
	
	@Override
	public void notifyOfEndOfTheGame(boolean isNull)
	{
		String winner = "";
		if(!isNull)
		{
			winner = currentPlayer;
		}
		IModelListener[] listeners = getClientsListeners();
		ExecutorService executor = Executors.newFixedThreadPool(listeners.length);
		Runnable task;
		for(IModelListener listener: listeners)
		{
			task = new UpdateEndOfTheGame(listener, winner);
			executor.execute(task);
		}
		executor.shutdown();
	}
	
	@Override
	public boolean makeMove(int column, int row, String player) 
	{
		if(board[column][row] != CaseType.NONE)
		{
			return false;
		}
		board[column][row] = getPlayerCaseType(player);
		return true;
	}
	
	@Override
	public boolean isPlaying(String player)
	{
		if((player.equals(player1)) || (player.equals(player2)))
		{
			return true;
		}
		return false;
	}
	
	@Override
	public void makeNextPlayerCurrent() 
	{
		if(currentPlayer.equals(player1))
		{
			currentPlayer = player2;
		}
		else if(currentPlayer.equals(player2))
		{
			currentPlayer = player1;
		}
	}
	
	@Override
	public void setCurrentPlayer(String player)
	{
		currentPlayer = player;
	}
	
	@Override
	public String getCurrentPlayer() 
	{
		return currentPlayer;
	}
	
	@Override
	public int getColumnLowestFreeRow(int column)
	{
		for(int currentRow = rows.get() - 1; currentRow >= 0; currentRow--)
		{
				if(positionAvailable(column, currentRow))
				{
					return currentRow;
				}
		}
		return -1;
	}
	
	@Override
	public boolean floorFull()
	{
		for(int column = 0; column < columns.get(); column++)
		{
			if(getColumnLowestFreeRow(column) != -1)
			{
				return false;
			}
		}
		return true;
	}
	@Override
	public boolean playerAvailable()
	{
		if(player1.isEmpty())
		{
			return true;
		}
		else if(player2.isEmpty())
		{
			return true;
		}
		return false;
	}
	@Override
	public boolean positionAvailable(int column, int row)
	{
		if(board[column][row] == CaseType.NONE)
		{
			return true;
		}
		return false;
	}
	
	@Override
	public boolean positionMakeWinning(int column, int row)
	{
		// WinningAlignTokenNumber from current position to both ways & current position
		int caseSize = connectToWin.get() * 2 + 1;
		
		// Horizontal
		CaseType[] case1 = new CaseType[caseSize];
		// Vertical
		CaseType[] case2 = new CaseType[caseSize];
		// Diagonal West-North & South-East
		CaseType[] case3 = new CaseType[caseSize];
		// Diagonal East-North & South-West
		CaseType[] case4 = new CaseType[caseSize];

		// The middle index is the actual player, were he just played.
		case1[connectToWin.get()] = getPlayerCaseType(currentPlayer);
		case2[connectToWin.get()] = getPlayerCaseType(currentPlayer);
		case3[connectToWin.get()] = getPlayerCaseType(currentPlayer);
		case4[connectToWin.get()] = getPlayerCaseType(currentPlayer);
		
		// For all cases go both ways from the actual player index.
		for(int i = 1; i <= connectToWin.get(); i++)
		{
			case1[i + connectToWin.get()] = getPositionCaseType(column + i, row);
			case1[connectToWin.get() - i] = getPositionCaseType(column - i, row);	

			case2[i + connectToWin.get()] = getPositionCaseType(column, row + i);
			case2[connectToWin.get() - i] = getPositionCaseType(column, row - i);

			case3[i + connectToWin.get()] = getPositionCaseType(column + i, row + i);
			case3[connectToWin.get() - i] = getPositionCaseType(column - i, row - i);

			case4[i + connectToWin.get()] = getPositionCaseType(column - i, row + i);
			case4[connectToWin.get() - i] = getPositionCaseType(column + i, row - i);
		}
		
		CaseType[][] winningCases = new CaseType[4][caseSize];
		winningCases[0] = case1;
		winningCases[1] = case2;
		winningCases[2] = case3;
		winningCases[3] = case4;
		
		return checkIfWinningCases(winningCases);
	}
	
	private IModelListener[] getClientsListeners()
	{
		IModelListener[] listeners =  new IModelListener[users.size()];
		if(users.size() > 0)
		{
			int index = 0;
			for(String usr: users.keySet())
			{
				listeners[index] = users.get(usr).getListener();
				index++;
			}
		}
		return listeners;
	}
	
	private boolean containsUser(String username)
	{
		for(String current: users.keySet())
		{
			if(current.equals(username))
			{
				return true;
			}
		}
		return false;
	}
	
	private void printListOfUsernamesInList(String header)
	{
		String output = "[";
		for(String user: users.keySet())
		{
			output += user + ",";
		}
		if (output.endsWith(","))
		{
			output = output.substring(0, output.length() - 1) + "]";
		}
		else if (output.endsWith("["))
		{
			output += "]";
		}
		System.out.println(header + output);
	} 
	
	private void addNewPlayer(String player)
	{
		if(player1.isEmpty())
		{
			player1 = player;
		}
		else if(player2.isEmpty())
		{
			player2 = player;
		}
	}
	
	private void removePlayer(String player)
	{
		if(player1.equals(player))
		{
			player1 = "";
		}
		else if(player2.equals(player))
		{
			player2 = "";
		}
	}
	
	private CaseType getPlayerCaseType(String player)
	{
		if(player.equals(player1))
		{
			return CaseType.PLAYER1;
		}
		else if(player.equals(player2))
		{
			return CaseType.PLAYER2;
		}
		return null;
	}
	
	private boolean checkIfWinningCases(CaseType[][] winningCases)
	{
		for(int caseIndex = 0; caseIndex < 4; caseIndex++)
		{
			CaseType[] currentCase = winningCases[caseIndex];
			int consecutivePlayerMatches = 0;
			for(int index = 0; index < currentCase.length; index++)
			{
				if(currentCase[index] == getPlayerCaseType(currentPlayer))
				{
					consecutivePlayerMatches++;
				}
				else
				{
					consecutivePlayerMatches = 0;
				}
				if(consecutivePlayerMatches >= connectToWin.get())
				{
					return true;
				}
			}
		}
		return false;
	}
	
	private CaseType getPositionCaseType(int column, int row)
	{
		CaseType caseType = null;
		try
		{
			caseType = board[column][row];
		}
		catch(ArrayIndexOutOfBoundsException e){}
		return caseType;
	}
	
	private String getNextAvailableUsername(String username, String newUsername, int counter)
	{
		if(containsUser(newUsername))
		{
			counter++;
			newUsername = username.concat(String.valueOf(counter));
			getNextAvailableUsername(username, newUsername, counter);
		}
		return newUsername;
	}
	
	private void makeNewBoard()
	{
		board = new CaseType[columns.get()][rows.get()];
		for(int column = 0; column < columns.get(); column++)
		{
			for(int row = 0; row < rows.get(); row++)
			{
				board[column][row] = CaseType.NONE;
			}
		}
		player1 = "";
		player2 = "";
	}
	
	private class InitializeListenerBoard implements Runnable 
	{
		  private IModelListener listener;
		  private int columns;
		  private int rows;
		  
		  // TODO call super for columns n rows
		  public InitializeListenerBoard(IModelListener listener, int columns, int rows) 
		  {
			  this.listener = listener;
			  this.columns = columns;
			  this.rows = rows;
		  }

		  @Override
		  public void run() 
		  {
			  try 
			  {
				Thread.sleep(100);
			  } 
			  catch (InterruptedException e)
			  {
				e.printStackTrace();
			  }
			  listener.initializeView(columns, rows);
		  }
	}
	
	private class UpdateListenerUsername implements Runnable 
	{
		  private IModelListener listener;
		  private String username;
		  
		  UpdateListenerUsername(IModelListener listener, String username) 
		  {
			  this.listener = listener;
			  this.username = username;
		  }

		  @Override
		  public void run() 
		  {
			  try 
			  {
				Thread.sleep(250);
			  } 
			  catch (InterruptedException e)
			  {
				e.printStackTrace();
			  }
			  listener.updateUsername(username);
		  }
	}
	
	private class UpdateListenerCurrentPlayer implements Runnable 
	{
		  private IModelListener listener;
		  private String player;
		  
		  UpdateListenerCurrentPlayer(IModelListener listener, String player) 
		  {
			  this.listener = listener;
			  this.player = player;
		  }

		  @Override
		  public void run() 
		  {
			  try 
			  {
				Thread.sleep(200);
			  } 
			  catch (InterruptedException e)
			  {
				e.printStackTrace();
			  }
			  listener.updateCurrentPlayer(player);
		  }
	}
	
	private class UpdateListenerBoardCase implements Runnable 
	{
		  private IModelListener listener;
		  private int column;
		  private int row;
		  private CaseType caseType;
		  
		  public UpdateListenerBoardCase(IModelListener listener, 
				  int columns, int rows, CaseType caseType) 
		  {
			  this.listener = listener;
			  this.column = columns;
			  this.row = rows;
			  this.caseType = caseType;
		  }

		  @Override
		  public void run() 
		  {
			  try 
			  {
				Thread.sleep(300);
			  } 
			  catch (InterruptedException e)
			  {
				e.printStackTrace();
			  }
			  listener.updateCase(column, row, caseType);
		  }
	}
	
	private class UpdateEndOfTheGame implements Runnable 
	{
		  private IModelListener listener;
		  private String winner;
		  
		  public UpdateEndOfTheGame(IModelListener listener, String winner) 
		  {
			  this.listener = listener;
			  this.winner = winner;
		  }

		  @Override
		  public void run() 
		  {
			  try 
			  {
				Thread.sleep(100);
			  } 
			  catch (InterruptedException e)
			  {
				e.printStackTrace();
			  }
			  try
			  {
				  listener.updateEndOfTheGame(winner);
			  }
			  catch(Exception e)
			  {
				  /* TODO Arrange
				   * Cause: this thread is updating after
				   * the client was remove from the users
				   */
			  }
		  }
	}
}
