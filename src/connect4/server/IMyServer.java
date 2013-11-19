package connect4.server;


import java.rmi.server.UID;

import connect4.client.IModelListener;


public interface IMyServer 
{
	public UID registerListener(IModelListener client);
	public void unregisterListener(UID uid);
	public void play(int row, int column);
}
