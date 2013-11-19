package connect4.client;


public interface IModelListener
{
	void initializeViews(int columns, int rows);
	void updateCurrentPlayer(CaseType newPlayer);
}
