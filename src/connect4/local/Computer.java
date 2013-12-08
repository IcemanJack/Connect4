package connect4.local;

import connect4.local.Model;

public class Computer
{
	private Controller controller;
	private Model model;
	
	public Computer(final Controller controller, final Model model, final int column, final int lowestRow)
	{
		this.controller = controller;
		this.model = model;

		computerPlay(column, lowestRow);
	}
	
	private boolean computerPlay(int column, int lowestRow)
	{
		for (int i = 0; i < model.columns; i++)
		{
			for (int j = 0; j < model.rows; j++)
			{
				if (playWhenRowEqualZero(lowestRow) || playWhenThreeCaseType(column, lowestRow, i, j))
				{
					return true;
				}
			}
		}
		play(lowestRow, column);
		return false;
	}
	
	private void play(final int lowestRow,final int column)
	{
		setCurrentPlayerToNextPlayer();
		controller.play(lowestRow, column);
		setCurrentPlayerToNextPlayer();
	}
	
	private void setCurrentPlayerToNextPlayer()
	{
		model.setCurrentPlayer(model.getNextPlayer());
	}
	
	private boolean playWhenRowEqualZero(final int lowestRow)
	{
		if (lowestRow == 0)
		{
			for (int i = 0; i < model.columns; i++)
			{
				if (isAvailable(i, 0) && !model.isPositionMakeWinning(i, model.getColumnLowestFreeRow(i) + 1))
				{
					play(0, i);
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean playWhenThreeCaseType(final int column, final int lowestRow, final int posX, final int posY)
	{
		if (model.isPositionMakeWinning(posX, posY) && isAvailable(posX, posY))
		{
			if (posY != 5 && isAvailable(posX, posY + 1))
			{
				playIfNumberIsNotPair((model.getColumnLowestFreeRow(posX) - posY), column, lowestRow, posX, posY);
			}
			else
			{
				play(posY, posX);
			}
			return true;
		}
		return false;
	}
	
	private boolean isAvailable(final int column,final int row)
	{
		return model.isAvailable(column, row);
	}
	
	private void playIfNumberIsNotPair(final int number, final int column, final int lowestRow, final int posX, final int posY)
	{
		if ((number % 2) == 1)
		{
			play(lowestRow, column);
		}
		play(posY + 1, posX);
	}
}