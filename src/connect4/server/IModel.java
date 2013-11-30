package connect4.server;

import connect4.client.IModelListener;

// used by server
public interface IModel 
{
	String addModelListener(String player, IModelListener client);
	void removeModelListener(String player);
	
	void initializeListenerBoard(IModelListener client);
	void updateListenersCurrentPlayer();
	void updateListenersBoardCase(int column, int row, String player);
	void updateUsername(String username, IModelListener client);
	
	void makeNewBoard();
	boolean makeMove(int column, int row, String player);
	void movePlayerToPosition(int column, int row, String player);
	
	void makeNextPlayerCurrent();
	void setCurrentPlayer(String player);
	boolean playerIsLoggedIn(String player);
	
	String getCurrentPlayer();
	String getNextPlayer();
	int getColumnLowestFreeRow(int column);
	
	boolean floorFull();
	boolean playerAvailable();
	boolean positionAvailable(int column, int row);
	boolean positionMakeWinning(int column, int row);
}
