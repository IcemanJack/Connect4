package connect4.server;

import connect4.client.IModelListener;

public interface IMyServer 
{
	public String registerListener(IModelListener client);
	public void unregisterListener(String uid);
	// temp
	public void play();
	//public void play(int row, int column);
}
