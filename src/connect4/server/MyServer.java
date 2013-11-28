package connect4.server;

import java.io.IOException;
import java.net.Socket;

import net.sf.lipermi.net.IServerListener;

import connect4.client.IModelListener;
import net.sf.lipermi.exception.LipeRMIException;
import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Server;

/* TODO
 * Add support for more than 2 player
 * 
 */

public class MyServer extends Server implements IMyServer, UserMessages
{
	static private int serverPort = 12345;
	
	private IModel model;
	private CallHandler callHandler = new CallHandler();
	
	MyServer()
	{
		model = new Model();
		initializeGame();
	}

	MyServer(int columns, int rows, int connectToWin)
	{
		model = new Model(columns, rows, connectToWin);
		initializeGame();
	}

	@Override
	public synchronized String registerListener(String username, IModelListener client)
	{
		if(model.playerAvailable())
		{
			// if username not available will return a new one
			username = model.addModelListener(username, client);
			model.initializeListenerBoard(client);
			// last player starts.
			model.setCurrentPlayer(username);
			model.updateListenersCurrentPlayer();
			return MyServer.WELCOME;
		}
		return MyServer.GAME_FULL;
	}
	@Override
	public synchronized void unregisterListener(String username) 
	{
		model.removeModelListener(username);
	}
	
	@Override
	public synchronized String makeMove(int column, int row, String player)
	{
		if(!model.playerIsLoggedIn(player))
		{
			return MyServer.USER_NOT_PLAYING;
		}
		
		if(model.playerAvailable())
		{
			return MyServer.WAITING_FOR_SECOND_PLAYER;
		}
		
		if(model.floorFull())
		{
			return NOONE_WON;
		}
		
		int mostLowRow = model.getColumnLowestFreeRow(column);
		if (mostLowRow != -1)
		{
			if(model.makeMove(column, mostLowRow, player))
			{
				model.makeNextPlayerCurrent();
				model.updateListenersCurrentPlayer();
				
				return MyServer.MOVE_DONE;
			}
			if(model.positionMakeWinning(column, mostLowRow))
			{
				return MyServer.YOU_WON;
			}
		}
		return MyServer.NO_AVAILABLE_POSITION;
	}

	private void initializeGame()
	{
		if(!startServer())
		{
			System.out.println("Server failed to start");
		}
		System.out.println("Server ready");
		model.makeNewBoard();
	}
	
	private boolean startServer()
	{
		try
		{
			callHandler.registerGlobal(IMyServer.class, this);
			this.bind(serverPort, callHandler);
			// will be auto-called by RMI
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
	
	private class ServListener implements IServerListener
	{
		@Override
		public void clientConnected(Socket socket) 
		{
			System.out.println("Client connected: " + socket.getInetAddress());
		}
		
		@Override
		public void clientDisconnected(Socket socket) 
		{
			System.out.println("Client disconnected: " + socket.getInetAddress());
		}
	}
}