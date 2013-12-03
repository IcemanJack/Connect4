package connect4.server;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import connect4.client.IModelListener;
import connect4.server.CaseType;
import connect4.server.database.Database;
import connect4.server.database.IDatabase;
import connect4.server.database.MockDatabase;
import connect4.server.database.MockDatabase.NoUsers;
import connect4.server.database.MockDatabase.UserAlreadyExists;
import connect4.server.database.MockDatabase.UserNotFound;
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
	
	private AtomicInteger columns = new AtomicInteger();
	private AtomicInteger rows = new AtomicInteger();
	private AtomicInteger connectToWin = new AtomicInteger();
	
	private String player1;
	private String player2;
	private String currentPlayer;
	
	public Model()
	{
		this.columns.set(7);
		this.rows.set(6);
		this.connectToWin.set(4);
	}
	public Model(int columns, int rows, int connectToWin)
	{
		this.columns.set(columns);
		this.rows.set(rows);
		this.connectToWin.set(connectToWin);
	}
	
	public void fallBackOnMock()
	{
		database = new MockDatabase();
	}
	
	public boolean connectToDatabase() throws SQLException, ClassNotFoundException
	{
		try
		{
			database = new Database();
		}
		catch(SQLException e)
		{
			return false;
		}
		return true;
	}
	
	@Override
	public String addClient(String username, IModelListener client)
	{
		if(database.containsUser(username))
		{
			username = getNextAvailableUsername(username, username.concat("0"), 0);
		}
		UserType type = null;
		if(playerAvailable())
		{
			addNewPlayer(username);
			type = UserType.PLAYER;
		}
		else
		{
			type = UserType.SPECTATOR;
		}
		System.out.println("In addClient:\nplayer "+ username +"\nplayer1: "+ player1 +"\nplayer2: "+player2);
		try
		{
			database.addUser(new User(username, client, type));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		catch (UserAlreadyExists e) 
		{
			e.printStackTrace();
		}
		printListOfUsernamesInList("New player added. New list ");
		
		return username;
	}
	
	@Override
	public void removeClient(String username) 
	{
		try
		{
			database.removeUser(username);
		}
		catch (UserNotFound e)
		{
			e.printStackTrace();
			System.out.println("Can't remove not existing player: "+username);
			return;
		}
		removePlayer(username);
		printListOfUsernamesInList("Player removed. New list ");
	}
	
	@Override
	public void initializeClientBoard(IModelListener client) 
	{
		ExecutorService executor = Executors.newFixedThreadPool(1);
		Runnable task = new InitializeListenerBoard(client, columns.get(), rows.get());
		executor.execute(task);
		executor.shutdown();
	}
	
	@Override
	public void initializeClientsBoard()
	{
		IModelListener[] listeners = getClientListeners();
		for(IModelListener listener: listeners)
		{
			initializeClientBoard(listener);
		} 
	}
	
	@Override
	public void updateClientUsername(String username, IModelListener client)
	{
		Runnable task;
		ExecutorService executor = Executors.newFixedThreadPool(1);
		task = new UpdateListenerUsername(client, username);
		executor.execute(task);
		executor.shutdown();
	}
	
	@Override
	public void updateClientsCurrentPlayer() 
	{
		IModelListener[] listeners = getClientListeners();
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
		IModelListener[] listeners = getClientListeners();
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
		System.out.println("NOTIFYYINH " + currentPlayer + " "+ isNull);
		String winner = "";
		if(!isNull)
		{
			winner = currentPlayer;
		}
		IModelListener[] listeners = getClientListeners();
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
	public void makeNewBoard()
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
	public void movePlayerToPosition(int column, int row, String player)
	{
		try
		{
			board[column][row] = getPlayerCaseType(player);
		}
		catch(ArrayIndexOutOfBoundsException e){}
	}
	
	@Override
	public boolean isPlaying(String player)
	{
		System.out.println("In isPlaying:\nplayer "+ player +"\nplayer1: "+ player1 +"\nplayer2: "+player2);
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
	public String getNextPlayer()
	{
		if(currentPlayer.equals(player1))
		{
			return player1;
		}
		else if(currentPlayer.equals(player1))
		{
			return player1;
		}
		else
		{
			return null;
		}
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
	
	private IModelListener[] getClientListeners()
	{
		try
		{
			return database.getClientsListeners();
		}
		catch (NoUsers e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	private void printListOfUsernamesInList(String header)
	{
		System.out.println(header + database.getListOfUsers());
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
		if(database.containsUser(newUsername))
		{
			counter++;
			newUsername = username.concat(String.valueOf(counter));
			getNextAvailableUsername(username, newUsername, counter);
		}
		return newUsername;
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
