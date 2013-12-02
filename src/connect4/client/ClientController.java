package connect4.client;

import java.io.IOException;
import connect4.server.IMyServer;
import connect4.server.Status;
import connect4.server.database.User.UserType;
import net.sf.lipermi.exception.LipeRMIException;
import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Client;

/* TODO
 * Make view with console log, so we print server responses directly.
 * Add extends client?
 */
public class ClientController
{
	private Client client;
	private IMyServer server;
	private CallHandler callHandler;
	
	private String serverIP;
	private int serverPort;
	private IModelListener myListener;
	private IU userInterface;
	
	String username;
	
	public ClientController()
	{
		serverIP = "127.0.0.1";
		serverPort = 12345;
		username = "MadJack";
		
		makeCustomView();
		startClient();
	}
	
	public ClientController(String serverIP, int serverPort, String username)
	{
		this.serverIP = serverIP;
		this.serverPort = serverPort;
		this.username = username;
		
		makeCustomView();
		startClient();
	}
	
	public void makeMove(final int row, final int column)
	{
		handleServerResponse(server.makeMove(column, row, username));
	}
	
	public void updateUsername(String username)
	{
		this.username = username;
	}
	
	/* TODO 
	 * Must be called if:
	 * 	Client stops the application
	 * 	Client kills the process
	 */
	public void quitTheGame()
	{
		// TODO ask Tiger:
		// If its a null will freeze the window.
		disconnectClient();
		System.exit(0);
	}
	
	public String getUsername()
	{
		return username;
	}
	
	private void makeCustomView()
	{
		userInterface = new View(this);
		myListener = (IModelListener) userInterface;
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
			userInterface.alertMessage("Server is not responding.");
			System.exit(0);
		}
		try 
		{
			callHandler.registerGlobal(IModelListener.class, myListener);
		}
		catch (LipeRMIException e) 
		{
			System.out.println(e.getMessage());
		}
		register(UserType.PLAYER);
	}
	
	private void register(UserType type)
	{
		Status response = null;
		if(type == UserType.PLAYER)
		{
			response = server.registerAsPlayer(username, myListener);
		}
		else if(type == UserType.SPECTATOR)
		{
			response = server.registerAsSpectator(myListener);
		}
		handleServerResponse(response);
	}
	
	private void handleServerResponse(Status serverResponse)
	{
		if(serverResponse == Status.OPERATION_DONE)
		{
			return;
		}
		else if(serverResponse == Status.GAME_FULL)
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
			userInterface.alertMessage(serverResponse.getDescription());
		}
	}
	
	private void disconnectClient()
	{
		server.unregisterUser(username);
		try 
		{
			client.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
