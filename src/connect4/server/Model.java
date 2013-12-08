package connect4.server;

import java.lang.reflect.UndeclaredThrowableException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import connect4.client.interfaces.GameListener;
import connect4.server.database.Database;
import connect4.server.database.MockDatabase;
import connect4.server.database.MockDatabase.NoUsers;
import connect4.server.database.MockDatabase.UserAlreadyExists;
import connect4.server.database.MockDatabase.UserIsNotFound;
import connect4.server.enums.CaseType;
import connect4.server.enums.GameResult;
import connect4.server.interfaces.IDatabase;
import connect4.server.interfaces.IModel;
import connect4.server.objects.User;
import connect4.server.objects.User.UserType;

/* TODO
 * change all return null
 * throw custom exceptions, in server catch them
 * call super to get data from private classes
 */

public class Model implements IModel
{
	private IDatabase database;
	private CaseType[][] board;
	// <User name, User Object>
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
	
	@Override
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
	
	@Override
	public void fallBackOnMock()
	{
		database = new MockDatabase();
	}
	
	@Override
	public User[] getScoreTableFromDatabase()
	{
		User[] usrs = null;
		try
		{
			usrs = database.getScoreTable();
		}
		catch (SQLException e)
		{
			System.err.println(e.getMessage());
		}
		catch (NoUsers e)
		{
			System.err.println(e.getMessage());
		}
		return usrs;
	}
	
	@Override
	public void updatePlayersScoresInDatabase(String username, GameResult result)
	{
		User current = new User(username);		
		User opponent = new User(getOpponent(username));
		
		if(!containsPlayerInDatabase(current.getName()))
		{
			addPlayerToDatabase(current.getName());
		}
		
		if(!containsPlayerInDatabase(opponent.getName()))
		{
			addPlayerToDatabase(opponent.getName());
		}
		
		try
		{
			database.updateUserScore(current, result.getScore());
			
			if(result == GameResult.WIN)
			{
				database.updateUserScore(opponent, GameResult.LOSE.getScore());
			}
			else if(result == GameResult.LOSE)
			{
				database.updateUserScore(opponent, GameResult.WIN.getScore());
			}
			else
			{
				database.updateUserScore(opponent, GameResult.NULL.getScore());
			}
		}
		catch (SQLException e)
		{
			System.err.println(e.getMessage());
		}
		catch (UserIsNotFound e)
		{
			System.err.println(e.getMessage());
		}
	}
	
	@Override
	public String validateUsername(String username)
	{
		if(containsUser(username))
		{
			username = getNextAvailableUsername(username, username.concat("0"), 0);
		}
		return username;
	}
	
	@Override
	public void addClient(String username, GameListener client)
	{
		UserType type =  UserType.SPECTATOR;
		if(playerAvailable())
		{
			addNewPlayer(username);
			type = UserType.PLAYER;
			
			if(!containsPlayerInDatabase(username))
			{
				addPlayerToDatabase(username);
			}
		}
		
		User user = new User(username, client, type);
		users.put(username, user);
		
		printListOfUsernamesInList("New player added. New users ");
	}
	
	@Override
	public void removeClient(String username)
	{
		removePlayer(username);
		users.remove(username);
		printListOfUsernamesInList("Player removed. New list ");
	}
	
	@Override
	public void initializeClientBoard(GameListener client) 
	{
		ExecutorService executor = Executors.newFixedThreadPool(1);
		Runnable task = new InitializeListenerBoard(client, columns.get(), rows.get());
		executor.execute(task);
		executor.shutdown();
	}
		
	@Override
	public void updateClientsCurrentPlayer() 
	{
		GameListener[] listeners = getClientsListeners();
		ExecutorService executor = Executors.newFixedThreadPool(listeners.length);
		Runnable task;
		for(GameListener listener: listeners)
		{
			task = new UpdateListenerCurrentPlayer(listener, currentPlayer);
			executor.execute(task);
		}
		executor.shutdown();
	}
	
	@Override
	public void updateClientsBoardCase(int column, int row, String player)
	{
		CaseType caseType = getPlayerCaseType(player);
		GameListener[] listeners = getClientsListeners();
		ExecutorService executor = Executors.newFixedThreadPool(listeners.length);
		Runnable task;
		for(GameListener listener: listeners)
		{
			task = new UpdateListenerBoardCase(listener, column, row, caseType);
			executor.execute(task);
		}
		executor.shutdown();
	}
	
	@Override
	public void notifyOfEndOfTheGame(GameResult result)
	{
		String winner = "";
		if(result == GameResult.WIN)
		{
			winner = this.currentPlayer;
		}
		if(result == GameResult.LOSE)
		{
			winner = this.getOpponent(this.currentPlayer);
		}
		else
		{
			winner = "No one";
		}
		
		GameListener[] listeners = getClientsListeners();
		ExecutorService executor = Executors.newFixedThreadPool(listeners.length);
		Runnable task;
		for(GameListener listener: listeners)
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
	
	private boolean containsPlayerInDatabase(String username)
	{
		try
		{
			if(database.containsUser(username))
			{
				return true;
			}
		}
		catch (SQLException e)
		{
			System.err.println(e.getMessage());
		}
		return false;
	}
	
	private void addPlayerToDatabase(String username)
	{
		try
		{
			database.addUser(new User(username));
		}
		catch (SQLException e)
		{
			System.err.println(e.getMessage());
		}
		catch (UserAlreadyExists e)
		{
			System.err.println(e.getMessage());
		}
	}
	
	private GameListener[] getClientsListeners()
	{
		GameListener[] listeners =  new GameListener[users.size()];
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
	
	private String getOpponent(String player)
	{
		if(player1.equals(player))
		{
			return player2;
		}
		else if(player2.equals(player))
		{
			return player1;
		}
		return null;
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
		  private GameListener listener;
		  private int columns;
		  private int rows;
		  
		  // TODO call super for columns n rows
		  public InitializeListenerBoard(GameListener listener, int columns, int rows) 
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
			  try
			  {
				  listener.initializeView(columns, rows);
			  }
			  catch (NullPointerException e)
			  {
				  System.err.println("Can't initializeView of a null client.");
			  }
		  }
	}
	
	private class UpdateListenerCurrentPlayer implements Runnable 
	{
		  private GameListener listener;
		  private String player;
		  
		  UpdateListenerCurrentPlayer(GameListener listener, String player) 
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
			  try
			  {
				  listener.updateCurrentPlayer(player);
			  }
			  catch (NullPointerException e)
			  {
				  System.err.println("Can't updateCurrentPlayer of a null client.");
			  }
		  }
	}
	
	private class UpdateListenerBoardCase implements Runnable 
	{
		  private GameListener listener;
		  private int column;
		  private int row;
		  private CaseType caseType;
		  
		  public UpdateListenerBoardCase(GameListener listener, 
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
			  try
			  {
				  listener.updateCase(column, row, caseType);
			  }
			  catch (NullPointerException e)
			  {
				  System.err.println("Can't updateCase of a null client.");
			  }
		  }
	}
	
	private class UpdateEndOfTheGame implements Runnable 
	{
		  private GameListener listener;
		  private String winner;
		  
		  public UpdateEndOfTheGame(GameListener listener, String winner) 
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
			  catch (UndeclaredThrowableException e)
			  {
				  System.err.println("Can't updateEndOfTheGame of a null client.");
			  }
			  catch (NullPointerException e)
			  {
				  System.err.println("Can't updateEndOfTheGame of a null client.");
			  }
		  }
	}
}
