package connect4.client;

public interface IModelListener
{
	void initializeView(int columns, int rows);
	void updateCurrentPlayer(String player);
	void updateListenerNotAvailableUsername(String username);
	// void updateCase(int column, int row, CaseType caseType);
}
