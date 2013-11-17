package connect4;

import java.awt.image.BufferedImage;

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
		model.addListener(view);
		model.initializeObserversViews();
		// Default
		model.setCurrentPlayer(Players.PLAYER1);
		model.set2Players(Players.PLAYER1, Players.PLAYER2);
		model.updateObserversViews();
	}
	
	public final void play(final int row, final int column)
	{
		int mostLowRow = getColumnMostLowFreeRow(column, row);
		if(mostLowRow != -1)
		{
			model.setCurrentPlayerAtPosition(column, mostLowRow);
			BufferedImage playerTokenImage = model.getTokenImage(model.getPlayerToken(model.getCurrentPlayer()));
			view.updateToken(column, mostLowRow, playerTokenImage);
			
			if(model.isPositionMakeWinning(column, mostLowRow))
			{
				endOfTheGame(GameWinner.PLAYER);
			}
			else if(floorIsFull())
			{
				endOfTheGame(GameWinner.NOONE);
			}
			else
			{
				model.setCurrentPlayer(model.getNextPlayer());
				model.updateObserversViews();
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
		// -0: new game & 1: exit
		int endGameChoice = -1;
		String newGameOffer = "\nWould you like to play a new game?";
		switch(winner)
		{
			case PLAYER:
				endGameChoice = view.endGameChoiceConformDialog
				(
						model.getCurrentPlayer()+" won!" + newGameOffer, "CONGRADULATION!"
				);
				break;
			case NOONE:
				endGameChoice = view.endGameChoiceConformDialog
				(
						"No one wins... It's a null!" + newGameOffer, "OHH NO!"
				);
		}
		if(endGameChoice == 1)
		{
			quitTheGame();
		}
		model.makeNewBoard();
		view.makeNewPlayground(model.getTokenImage(TokenType.NONE));
		
	}
	
	private final boolean floorIsFull()
	{
		for(int column = 0; column < model.getFloorColumns(); column++)
		{
			if(getColumnMostLowFreeRow(column, 0) != -1)
			{
				return false;
			}
		}
		return true;
	}
	
	private final int getColumnMostLowFreeRow(final int column, final int currentRow)
	{
		for(int row = model.getFloorRows() - 1; row >= currentRow; row--)
		{
				if(model.isAvailable(column, row))
				{
					return row;
				}
		}
		return -1;
	}
}
