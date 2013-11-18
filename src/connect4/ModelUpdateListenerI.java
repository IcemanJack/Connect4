package connect4;

public interface ModelUpdateListenerI 
{
	void initializeViews(int columns, int rows, Players defaultPlayer);
	void updateCurrentPlayer(Players newPlayer);
}
