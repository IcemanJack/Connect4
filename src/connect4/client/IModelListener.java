package connect4.client;


public interface IModelListener
{
	void initializeViews(int columns, int rows);
	void updateCurrentPlayer(CaseType newPlayer);
	// TODO temp
	void updateActionCounter(int i);
}
