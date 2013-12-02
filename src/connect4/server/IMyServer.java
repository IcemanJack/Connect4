package connect4.server;

import connect4.client.IModelListener;

// used by client
public interface IMyServer 
{
	Status registerAsPlayer(String username, IModelListener client);
	Status registerAsSpectator(IModelListener client);
	
	Status unregisterUser(String username);

	Status makeMove(int column, int row, String player);
}
