package connect4.server;

import connect4.client.IModelListener;

// used by client
public interface IMyServer 
{
	String registerListener(String username, IModelListener client);
	void unregisterListener(String username);
	
	String makeMove(int column, int row, String player);
}
