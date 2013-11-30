package connect4.server;

import connect4.client.IModelListener;

// used by client
public interface IMyServer 
{
	Status registerListener(String username, IModelListener client);
	void unregisterListener(String username);
	
	Status makeMove(int column, int row, String player);
}
