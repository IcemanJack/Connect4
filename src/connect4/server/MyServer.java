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

public class MyServer extends Server implements IMyServer
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
	public synchronized Status registerListener(String username, IModelListener client)
	{
		if(!model.playerAvailable())
		{
			return Status.GAME_FULL;
		}
		// will return incremented one if already in use
		username = model.addClient(username, client);
		model.initializeClientBoard(client);
		model.updateClientUsername(username, client);
		
		// last player starts
		model.setCurrentPlayer(username);
		model.updateClientsCurrentPlayer();
		
		return Status.OPERATION_DONE;
	}
	@Override
	public synchronized void unregisterListener(String username) 
	{
		model.removeClient(username);
	}
	
	@Override
	public synchronized Status makeMove(int column, int row, String player)
	{
		if(!model.playerIsLoggedIn(player))
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
		
		model.updateClientBoardCase(column, mostLowRow, player);
		
		if(model.positionMakeWinning(column, mostLowRow))
		{
			// will make a new board
			model.notifyOfEndOfTheGame();
			model.makeNewBoard();
			// TODO who's turn?
			// first spectator
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