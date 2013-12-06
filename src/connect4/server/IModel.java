package connect4.server;

import connect4.client.IModelListener;

// used by server
public interface IModel 
{
	public boolean connectToDatabase();
	public void fallBackOnMock();
	//public void containsUserInDataBase();
	//public void addUserToDataBase();
	//public void updateUserScoreInDatabase();
	
	String addClient(String player, IModelListener client);
	void removeClient(String player);
	
	void updateClientUsername(String username, IModelListener client);
	public void initializeClientBoard(IModelListener client);
	
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

/* add saveScore this or update.
try
{
	database.addUser(user);
}
catch (SQLException e)
{
	System.err.println(e.getMessage());
}
catch (UserAlreadyExists e) 
{
	System.err.println(e.getMessage());
}
*/