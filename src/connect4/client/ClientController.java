package connect4.client;

import java.io.IOException;
import connect4.server.IMyServer;
import net.sf.lipermi.exception.LipeRMIException;
import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Client;

// TODO change to extends Client
public class ClientController
{
	Client client;
	IMyServer myRemoteObject;
	CallHandler callHandler;
	
	String serverIP;
	int serverPort;
	IModelListener view;
	
	String uid;
	
	public ClientController()
	{
		serverIP = "127.0.0.1";
		serverPort = 12345;
		startClient();
		makeCustomView();
	}
	
	public ClientController(String serverIP, int serverPort)
	{
		this.serverIP = serverIP;
		this.serverPort = serverPort;
		startClient();
		makeCustomView();
	}
	
	public void play(final int row, final int column)
	{
		System.out.println("Starting action from client");
		myRemoteObject.play();
		System.out.println("finishing action from client");
	}
	
	public void quitTheGame()
	{
		disconnectClient();
		System.exit(0);
	}
	
	private void makeCustomView()
	{
		view = new View(this);
		try 
		{
			callHandler.registerGlobal(IModelListener.class, view);
		}
		catch (LipeRMIException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		uid = myRemoteObject.registerListener(view);
		System.out.println("Client connected with UID: "+uid.toString());
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
		System.out.println("Client started");
	}
	
	private void disconnectClient()
	{
		myRemoteObject.unregisterListener(uid);
		try 
		{
			client.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		System.out.println("Client disconnected");
	}
}
