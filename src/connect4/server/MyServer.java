package connect4.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import net.sf.lipermi.net.IServerListener;

import connect4.client.interfaces.GameListener;
import connect4.server.enums.Status;
import connect4.server.interfaces.IModel;
import connect4.server.interfaces.IMyServer;
import connect4.server.objects.User.UserType;
import net.sf.lipermi.exception.LipeRMIException;
import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Server;

public class MyServer extends Server implements IMyServer
{
	static private int serverPort = 12345;
	
	private IModel model;
	private CallHandler callHandler = new CallHandler();

	private Map<String, String> users = Collections.synchronizedMap(new TreeMap<String, String>());
	private String newClientID;
	
	MyServer()
	{
		model = new Model();
		initializeGame();
		startDatabase();
	}

	MyServer(int columns, int rows, int connectToWin)
	{
		model = new Model(columns, rows, connectToWin);
		initializeGame();
		model.connectToDatabase();
		startDatabase();
	}
	
	private void startDatabase()
	{
		if(!model.connectToDatabase())
		{
			System.out.println("Unable to connect to database...\n" +
					"Falling back on mock");
			model.fallBackOnMock();
		}
	}
	
	@Override
	public String validateUsername(String username)
	{
		username = model.validateUsername(username);
		// to handle unexpected disconnects
		if(!newClientID.isEmpty())
		{
			users.put(newClientID, username);
			System.out.println("New client: " + newClientID + " " + username);
		}
		return username;
	}
	
	@Override
	public synchronized Status register(String name, GameListener client, UserType userType)
	{	
		System.out.println(name);
		String validatedName = model.validateUsername(name);
		if(!validatedName.equals(name))
		{
			name = validatedName;
			return Status.NAME_NOT_VALIDATED;
		}
		
		if(userType == UserType.PLAYER)
		{
			if(!userLoggedIn(name))
			{
				return Status.NOT_LOGGED_IN;
			}
			else if(!model.playerAvailable())
			{
				return Status.GAME_FULL;
			}
			// last player starts
			model.setCurrentPlayer(name);
		}
		
		model.addClient(name, client);
		model.initializeClientBoard(client);
		model.updateClientsCurrentPlayer();
		return Status.OPERATION_DONE;
	}
	
	private synchronized void unregisterUser(String username) 
	{
		if(model.isPlaying(username))
		{
			model.notifyOfEndOfTheGame(true);
			model.removeClient(username);
		}
		model.removeClient(username);
	}
	
	@Override
	public synchronized Status makeMove(int column, int row, String player)
	{
		if(!model.isPlaying(player))
		{
			return Status.USER_NOT_PLAYING;
		}
		else if(model.playerAvailable())
		{
			return Status.WAITING_FOR_SECOND_PLAYER;
		}
		else if(!model.getCurrentPlayer().equals(player))
		{
			return Status.NOT_YOUR_TURN;
		}
		else if(model.floorFull())
		{
			model.notifyOfEndOfTheGame(true);
			return Status.ITS_A_NULL;
		}
		
		int mostLowRow = model.getColumnLowestFreeRow(column);
		if(mostLowRow == -1)
		{
			return Status.NO_AVAILABLE_POSITION;
		}
		else if(!model.makeMove(column, mostLowRow, player))
		{
			return Status.UNKNOWN;
		}
		
		model.updateClientsBoardCase(column, mostLowRow, player);
		
		if(model.positionMakeWinning(column, mostLowRow))
		{
			model.notifyOfEndOfTheGame(false);
			return Status.YOU_WON;
		}
		
		model.makeNextPlayerCurrent();
		model.updateClientsCurrentPlayer();
		
		return Status.OPERATION_DONE;
	}

	
	private void initializeGame()
	{
		if(!startServer())
		{
			System.out.println("Server failed to start");
		}
		System.out.println("Server ready");
	}
	
	private boolean startServer()
	{
		try
		{
			callHandler.registerGlobal(IMyServer.class, this);
			this.bind(serverPort, callHandler);
			// will be auto-called by RMI on new client
			this.addServerListener(new ServListener());
			return true;
		} 
		catch (LipeRMIException error) 
		{
			error.printStackTrace();
		}
		catch (IOException error) 
		{
			error.printStackTrace();
		}
		return false;
	}
	
	private boolean userLoggedIn(String user)
	{
		for(String username : users.values())
		{
			if(username.equals(user))
			{
				return true;
			}
		}
		return false;
	}
	
	private class ServListener implements IServerListener
	{
		@Override
		public synchronized void clientConnected(Socket socket) 
		{
			MyServer.this.newClientID = socket.getInetAddress() + ":" + socket.getPort();
		}
		
		@Override
		public synchronized void clientDisconnected(Socket socket) 
		{
			String client = socket.getInetAddress() + ":" + socket.getPort();
			if(users.containsKey(client))
			{
				MyServer.this.unregisterUser(users.get(client));
				MyServer.this.users.remove(client);
			}
			System.out.println("Client disconnected: " + client);
		}
	}
}