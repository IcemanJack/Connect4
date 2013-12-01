package connect4.server;

import connect4.client.IModelListener;

// used by server
public interface IModel 
{
	String addClient(String player, IModelListener client);
	void removeClient(String player);
	
	// too many similar private classes?
	void initializeClientBoard(IModelListener client);
	void updateClientsCurrentPlayer();
	void updateClientBoardCase(int column, int row, String player);
	void updateClientUsername(String username, IModelListener client);
	void notifyOfEndOfTheGame();
	void initializeClientsBoard();
	
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
