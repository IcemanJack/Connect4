package connect4.server.interfaces;

import connect4.client.interfaces.GameListener;
import connect4.server.objects.User;

// used by server
public interface IModel 
{
	public boolean connectToDatabase();
	public void fallBackOnMock();
	public User[] getScoreTable();
	//public void containsUserInDataBase();
	//public void addUserToDataBase();
	//public void updateUserScoreInDatabase();
	
	public String validateUsername(String username);
	public void addClient(String player, GameListener client);
	void removeClient(String player);
	
	public void initializeClientBoard(GameListener client);
	
	void updateClientsCurrentPlayer();
	void updateClientsBoardCase(int column, int row, String player);
	void notifyOfEndOfTheGame(boolean isNull);
	
	boolean makeMove(int column, int row, String player);
	
	void makeNextPlayerCurrent();
	void setCurrentPlayer(String player);
	boolean isPlaying(String player);
	
	String getCurrentPlayer();
	int getColumnLowestFreeRow(int column);
	
	boolean floorFull();
	boolean playerAvailable();
	boolean positionAvailable(int column, int row);
	boolean positionMakeWinning(int column, int row);
}