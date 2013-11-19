package connect4.local;

public interface ModelUpdateListenerI 
{
	void initializeViews(int columns, int rows);
	void updateCurrentPlayer(CaseType newPlayer);
}
