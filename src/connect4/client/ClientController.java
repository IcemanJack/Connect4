package connect4.client;

import java.io.IOException;

import connect4.client.interfaces.GameListener;
import connect4.client.interfaces.GenericUI;
import connect4.client.views.GameView;
import connect4.client.views.LoginView;
import connect4.server.enums.Status;
import connect4.server.interfaces.IMyServer;
import connect4.server.objects.User.UserType;
import net.sf.lipermi.exception.LipeRMIException;
import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Client;

import connect4.client.views.ScoresView;

public class ClientController
{
	private Client client;
	private IMyServer server;
	private CallHandler callHandler;
	
	private String serverIP;
	private int serverPort;
	String username;

	private GameListener gameListener;
	private GenericUI gameInterface;
	private GenericUI loginInterface;
	
	public ClientController()
	{
		serverIP = "127.0.0.1";
		serverPort = 12345;
		
		makeLoginView();
		startClient();
		
		// TESTS
		
//		makeGameView();
//		gameListener = (GameListener) gameInterface;
//		gameListener.initializeView(7, 6);
//		gameInterface.updateUsername("cheval");
//		gameListener.updateCurrentPlayer("playing");
	}
	
	public ClientController(String serverIP, int serverPort, String username)
	{
		this.serverIP = serverIP;
		this.serverPort = serverPort;
		this.username = username;
		
		makeLoginView();
		startClient();
	}
	
	public void makeMove(final int row, final int column)
	{
		if(gameInterface != null)
		{
			handleServerResponse(server.makeMove(column, row, username), gameInterface);
		}
	}
	
	public void loggedIn(String username)
	{
		this.username = username;
		register(UserType.PLAYER);
	}
	
	public void quitTheGame()
	{
		gameInterface.close();
		System.exit(0);
	}
	
	public void register(UserType type)
	{
		Status response = null;
		if(type == UserType.PLAYER)
		{
			makeGameView();
			startGameListener();
			username = server.validateUsername(username);
		}
		else if(type == UserType.SPECTATOR)
		{
			username = server.validateUsername("Spectator");
		}
		
		if(username == null)
		{
			System.err.println("The username is null.");
			return;
		}
		
		response = server.register(username, gameListener, type);
		
		if(loginInterface != null)
		{
			handleServerResponse(response, loginInterface);
		}
		else
		{
			handleServerResponse(response, gameInterface);
		}
	}
	
	public void showScoresTable()
	{
		new ScoresView(server.getScoreTable());
	}
	
	private void makeLoginView()
	{
		loginInterface = new LoginView(this);
	}
	
	private void makeGameView()
	{
		if(loginInterface != null)
		{
			loginInterface.close();
			loginInterface = null;
		}
		gameInterface = new GameView(this);
	}
	
	private void startGameListener()
	{
		gameListener = (GameListener) gameInterface;
		try 
		{
			callHandler.registerGlobal(GameListener.class, gameListener);
		}
		catch (LipeRMIException e) 
		{
			System.out.println(e.getMessage());
		}
	}
	
	private void startClient()
	{
		callHandler = new CallHandler();
		try 
		{
			client  = new Client(serverIP, serverPort, callHandler);
			server = (IMyServer) client.getGlobal(IMyServer.class);
		} 
		catch (IOException e) 
		{
			loginInterface.alertMessage("Server is not responding.");
			System.exit(0);
		}
	}
	
	private void handleServerResponse(Status response, GenericUI userInterface)
	{
		if(response == Status.OPERATION_DONE)
		{
			return;
		}
		else if(response == Status.NAME_NOT_VALIDATED)
		{
			this.username = server.validateUsername(this.username);
			register(UserType.PLAYER);
		}
		else if(response == Status.GAME_FULL)
		{
			if(userInterface.choiceDialog("Game full",
					"Would you like to become a spectator?") == 0)
			{
				register(UserType.SPECTATOR);
			}
			else
			{
				System.exit(0);
			}
		}
		else
		{
			userInterface.alertMessage(response.getDescription());
		}
	}
}
