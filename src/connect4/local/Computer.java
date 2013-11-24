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
		
		model.setCurrentPlayer(model.getNextPlayer());
		System.out.println("(" + column + ", " + mostLowRow + ")");
		
		computerPlay(column, mostLowRow);
		
		model.setCurrentPlayer(model.getNextPlayer());
	}
	
	public void computerPlay(int column, int mostLowRow)
	{
		checkAroundCurrentToken(column, mostLowRow);
		
		if (checkIfThreeTokenComputer())
		{
			// Attaque
		}
		else if (checkIfThreeTokenPlayer1())
		{
			// Defense
		}
		else if (checkIfTwoOrOneTokenComputer())
		{
			// Attaque
		}
		else
		{
			// Random move
		}
		
		// controller.play(mostLowRow, column);
	}
	
	public boolean checkIfThreeTokenComputer()
	{
		return false;
	}
	public boolean checkIfTwoOrOneTokenComputer()
	{
		return false;
	}
	
	public boolean checkIfThreeTokenPlayer1()
	{
		return false;
	}
	
	public void checkAroundCurrentToken(int column, int mostLowRow)
	{
		checkRightHorizontalPosition(column, mostLowRow);
		checkLeftHorizontalPosition(column, mostLowRow);
		checkUpVerticalPosition(column, mostLowRow);
		checkUpLeftDiagonalPosition(column, mostLowRow);
		checkUpRightDiagonalPosition(column, mostLowRow);
		checkDownLeftDiagonalPosition(column, mostLowRow);
		checkDownRightDiagonalPosition(column, mostLowRow);
	}
	
	public final boolean checkRightHorizontalPosition(int column, final int mostLowRow)
	{
		if (column < model.columns - 1 && model.isAvailable(column + 1, mostLowRow))
		{
			System.out.println("nothing right");
			return true;
		}
		return false;
	}
	public final boolean checkLeftHorizontalPosition(int column, final int mostLowRow)
	{
		if (column > 0 && model.isAvailable(column - 1, mostLowRow))
		{
			System.out.println("nothing left");
			return true;
		}
		return false;
	}
	public final boolean checkUpVerticalPosition(final int column, final int row)
	{
		if (row > 0 && model.isAvailable(column, row - 1))
		{
			System.out.println("nothing up");
			return true;
		}
		return false;
	}
	public final boolean checkUpLeftDiagonalPosition(final int column, final int row)
	{
		if (column > 0 && row > 0 && model.isAvailable(column - 1, row - 1))
		{
			System.out.println("nothing up-left-diagonal");
			return true;
		}
		return false;
	}
	public final boolean checkUpRightDiagonalPosition(final int column, final int row)
	{
		if (column < model.columns - 1 && row > 0 && model.isAvailable(column + 1, row - 1))
		{
			System.out.println("nothing up-right-diagonal");
			return true;
		}
		return false;
	}
	public final boolean checkDownLeftDiagonalPosition(final int column, final int row)
	{
		if (column > 0 && row < model.rows - 1 && model.isAvailable(column - 1, row + 1))
		{
			System.out.println("nothing down-left-diagonal");
			return true;
		}
		return false;
	}
	public final boolean checkDownRightDiagonalPosition(final int column, final int row)
	{
		if (column < model.columns - 1 && row < model.rows - 1 && model.isAvailable(column + 1, row + 1))
		{
			System.out.println("nothing down-left-diagonal");
			return true;
		}
		return false;
	}
}