package connect4.client;

import java.io.IOException;
import connect4.server.IMyServer;
import connect4.server.Status;
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
	private IMyServer myRemoteObject;
	private CallHandler callHandler;
	
	private String serverIP;
	private int serverPort;
	// used to give to server
	private IModelListener modelListener;
	// used to talk to the view
	private IU userInterface;
	
	String username;
	
	public ClientController()
	{
		serverIP = "127.0.0.1";
		serverPort = 12345;
		username = "MadJack";
		
		makeCustomView();
		startClient();
		
		// Testing
//		modelListener.initializeView(7, 6);
//		modelListener.updateEndOfTheGame("fddf");
//		modelListener.updateUsername("Username");
//		modelListener.updateCurrentPlayer("Current");
		//modelListener.updateEndOfTheGame("Cheval");
		//modelListener.initializeView(7, 6);
//		System.out.println(((View) userInterface).endGameChoiceDialog("","sdsds\nsdsd"));

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
		System.out.println("Starting move from client");
		// TODO ask tiger weird error: do many returns.
		handleServerResponse(myRemoteObject.makeMove(column, row, username));
	}
	
	public void updateUsername(String username)
	{
		this.username = username;
	}
	
	/* TODO 
	 * Must be called if
	 * 	Client closes windows
	 * 	Client stops the application
	 * 	Client kills the process
	 */
	public void quitTheGame()
	{
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
		modelListener = (IModelListener) userInterface;
	}
	
	private void startClient()
	{
		callHandler = new CallHandler();
		try 
		{
			client  = new Client(serverIP, serverPort, callHandler);
			myRemoteObject = (IMyServer) client.getGlobal(IMyServer.class);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		registerAtServer();
	}
	
	private void registerAtServer()
	{
		try 
		{
			callHandler.registerGlobal(IModelListener.class, modelListener);
		}
		catch (LipeRMIException e) 
		{
			e.printStackTrace();
		}
		handleServerResponse(myRemoteObject.registerListener(username, modelListener));
	}
	
	private void handleServerResponse(Status serverResponse)
	{
		if(!serverResponse.equals(Status.OPERATION_DONE))
		{
			userInterface.alertMessage(serverResponse.getDescription());
		}
	}
	
	private void disconnectClient()
	{
		myRemoteObject.unregisterListener(username);
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
