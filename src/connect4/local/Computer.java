package connect4.local;

import connect4.local.Model;

public class Computer
{
	Controller controller;
	Model model;
	
	public Computer(Controller controller, Model model, int column, int mostLowRow)
	{
		this.controller = controller;
		this.model = model;

		computerPlay(column, mostLowRow);
	}
	
	private boolean computerPlay(int column, int mostLowRow)
	{
		for (int i = 0; i < model.columns; i++)
		{
			for (int j = 0; j < model.rows; j++)
			{
				if (playWhenRowEqualZero(mostLowRow))
				{
					return true;
				}
				
				if (playWhen3CaseType(column, mostLowRow, i, j))
				{
					return true;
				}
			}
		}
		
		play(mostLowRow, column);
		return false;
	}
	
	private void play(int mostLowRow, int column)
	{
		setCurrentPlayerToNextPlayer();
		controller.play(mostLowRow, column);
		setCurrentPlayerToNextPlayer();
	}
	
	private void setCurrentPlayerToNextPlayer()
	{
		model.setCurrentPlayer(model.getNextPlayer());
	}
	
	private boolean playWhenRowEqualZero(int mostLowRow)
	{
		if (mostLowRow == 0)
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
	
	private boolean playWhen3CaseType(int column, int mostLowRow, int posX, int posY)
	{
		if (model.isPositionMakeWinning(posX, posY) && isAvailable(posX, posY))
		{
			if (posY != 5 && isAvailable(posX, posY + 1))
			{
				if (checkIfNumberIsImpair(model.getColumnLowestFreeRow(posX) - posY))
				{
					play(mostLowRow, column);
				}
				else
				{
					//Attaque diagonal
					play(posY + 1, posX);
				}
			}
			else
			{
				play(posY, posX);
			}
			
			return true;
		}
		return false;
	}
	
	private boolean isAvailable(int column, int row)
	{
		return model.isAvailable(column, row);
	}
	
	private boolean checkIfNumberIsImpair(int number)
	{
		if (number % 2 == 1)
		{
			return true;
		}
		
		return false;
	}
}