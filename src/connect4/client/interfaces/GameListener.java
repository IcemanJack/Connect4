package connect4.client.interfaces;

import java.util.concurrent.atomic.AtomicInteger;

import connect4.server.enums.CaseType;

public interface GameListener
{
	void initializeView(AtomicInteger columns, AtomicInteger rows, String username, String current);
	void updateCurrentPlayer(String player);
	void updateCase(int column, int row, CaseType caseType);
	void updateEndOfTheGame(String message);
}
