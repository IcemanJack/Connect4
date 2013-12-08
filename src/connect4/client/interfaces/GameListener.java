package connect4.client.interfaces;

import connect4.server.enums.CaseType;

public interface GameListener
{
	void initializeView(int columns, int rows);
	void updateCurrentPlayer(String player);
	void updateCase(int column, int row, CaseType caseType);
	void updateEndOfTheGame(String winner);
}
