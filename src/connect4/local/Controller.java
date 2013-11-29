package connect4.local;

public class Controller 
{
	private View view;
	private Model model;
	
	public Controller()
	{
		model = new Model();
		initializeGame(model);
	}
	public Controller(final int columns, final int rows, final int winningAlignTokenNumber)
	{
		model = new Model(columns, rows, winningAlignTokenNumber);
		initializeGame(model);
	}
	
	private void initializeGame(Model model)
	{
		view = new View(this);
		model.makeNewBoard();
		model.addListener(view);
		model.initializeObserversViews();
		// Default
		model.setCurrentPlayer(CaseType.PLAYER1);
		model.updateListenersCurrentPlayer();
	}
	
	public final void play(final int row, final int column)
	{
		int mostLowRow = model.getColumnLowestFreeRow(column);
		if (mostLowRow != -1)
		{
			model.setCurrentPlayerAtPosition(column, mostLowRow);
			view.updateToken(column, mostLowRow, model.getCurrentPlayer());
			
			if (model.isPositionMakeWinning(column, mostLowRow))
			{
				endOfTheGame(GameWinner.PLAYER);
			}
			else if (model.floorIsFull())
			{
				endOfTheGame(GameWinner.NOONE);
			}
			else
			{
				if (model.getNextPlayer() == CaseType.PLAYER2)
				{
					new Computer(this, model, column, mostLowRow);
				}
				
				model.setCurrentPlayer(model.getNextPlayer());
				model.updateListenersCurrentPlayer();
			}
		}
	}
	
	public final void quitTheGame()
	{
		System.exit(0);
	}
	
	private enum GameWinner 
	{
		PLAYER,
		NOONE
	}
	
	private final void endOfTheGame(final GameWinner winner)
	{
		// 0: new game & 1: exit
		int endGameChoice = -1;
		String newGameOffer = "\nWould you like to play a new game?";
		switch(winner)
		{
			case PLAYER:
				endGameChoice = view.endGameChoiceDialog
				(
						model.getCurrentPlayer()+" won!" + newGameOffer, "CONGRADULATION!"
				);
				break;
			case NOONE:
				endGameChoice = view.endGameChoiceDialog
				(
						"No one wins... It's a null!" + newGameOffer, "OHH NO!"
				);
		}
		if(endGameChoice == 1)
		{
			quitTheGame();
		}
		model.makeNewBoard();
		view.makeNewPlayground();
	}
	

}
