package connect4;

public interface ModelUpdateListenerI 
{
	void initializeViews(int columns, int rows);
	void updateCurrentPlayer(CaseType newPlayer);
}
