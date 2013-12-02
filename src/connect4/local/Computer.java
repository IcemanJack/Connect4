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
				if (model.isPositionMakeWinning(i, j) && model.isAvailable(i, j))
				{
					if (j != 5 && model.isAvailable(i, j + 1))
					{
						if (checkIfNumberIsImpair(model.getColumnLowestFreeRow(i) - j))
						{
							play(mostLowRow, column);
						}
						else
						{
							//Attaquer la diagonal
							play(j + 1, i);	
						}
					}
					else
					{
						play(j, i);
					}
					
					return true;
				}
			}
		}
		
		play(mostLowRow, column);
		return false;
	}
	
	private void play(int mostLowRow, int column)
	{
		model.setCurrentPlayer(model.getNextPlayer());
		controller.play(mostLowRow, column);
		model.setCurrentPlayer(model.getNextPlayer());
	}
	
	private boolean checkIfNumberIsImpair(int number)
	{
		if (number % 2 == 1)
		{
			return true;
		}
		
		return false;
	}
	
//	private int getRandomNumber(int lowerRandomNumber, int higherRandomNumber)
//	{
//		return lowerRandomNumber + (int)(Math.random() * higherRandomNumber);
//	}
	
//	public void computerPlay(int column, int mostLowRow)
//	{
//		checkAroundCurrentToken(column, mostLowRow);
//	}
//	
//	public boolean checkIfThreeTokenComputer()
//	{
//		return false;
//	}
//	
//	public boolean checkIfTwoOrOneTokenComputer()
//	{		
//		return false;
//	}
	
	
//	public void checkAroundCurrentToken(int column, int mostLowRow)
//	{
//		checkRightHorizontalPosition(column, mostLowRow);
//		checkLeftHorizontalPosition(column, mostLowRow);
//		checkUpVerticalPosition(column, mostLowRow);
//		checkUpLeftDiagonalPosition(column, mostLowRow);
//		checkUpRightDiagonalPosition(column, mostLowRow);
//		checkDownLeftDiagonalPosition(column, mostLowRow);
//		checkDownRightDiagonalPosition(column, mostLowRow);
//	}

//	public int checkRightHorizontalPosition(int column, final int mostLowRow, CaseType caseType)
//	{
//		int x = 0;
//		
//		while (model.board[column][mostLowRow] == caseType)
//		{
//			x++;
//			column++;
//		}
//		
//		if(x == 3)
//		{
//			controller.play(mostLowRow, column + 3);
//		}
//		
//		return x;
//	}
	
//	public final boolean checkLeftHorizontalPosition(int column, final int mostLowRow)
//	{
//		if (column > 0 && model.isAvailable(column - 1, mostLowRow))
//		{
//			System.out.println("nothing left");
//			return true;
//		}
//		return false;
//	}
//	public final boolean checkUpVerticalPosition(final int column, final int row)
//	{
//		if (row > 0 && model.isAvailable(column, row - 1))
//		{
//			System.out.println("nothing up");
//			return true;
//		}
//		return false;
//	}
//	public final boolean checkUpLeftDiagonalPosition(final int column, final int row)
//	{
//		if (column > 0 && row > 0 && model.isAvailable(column - 1, row - 1))
//		{
//			System.out.println("nothing up-left-diagonal");
//			return true;
//		}
//		return false;
//	}
//	public final boolean checkUpRightDiagonalPosition(final int column, final int row)
//	{
//		if (column < model.columns - 1 && row > 0 && model.isAvailable(column + 1, row - 1))
//		{
//			System.out.println("nothing up-right-diagonal");
//			return true;
//		}
//		return false;
//	}
//	public final boolean checkDownLeftDiagonalPosition(final int column, final int row)
//	{
//		if (column > 0 && row < model.rows - 1 && model.isAvailable(column - 1, row + 1))
//		{
//			System.out.println("nothing down-left-diagonal");
//			return true;
//		}
//		return false;
//	}
//	public final boolean checkDownRightDiagonalPosition(final int column, final int row)
//	{
//		if (column < model.columns - 1 && row < model.rows - 1 && model.isAvailable(column + 1, row + 1))
//		{
//			System.out.println("nothing down-left-diagonal");
//			return true;
//		}
//		return false;
//	}
	
//	if (checkIfThreeTokenComputer(column, mostLowRow))
//	{
//		// Attaque
//	}
//	else if (checkIfThreeTokenPlayer1(column, mostLowRow))
//	{
//		// Defense
//		System.out.println("3TokenPlayer1");
//	}
//	else if (checkIfTwoOrOneTokenComputer())
//	{
//		// Attaque
//	}
//	else
//	{
//		// Random move
//	}
	
	// controller.play(mostLowRow, column);
	
//	if (model.board[column][mostLowRow] == CaseType.PLAYER1)
//	{
//		if (checkRightHorizontalPosition(column, mostLowRow, CaseType.PLAYER1) == 3)
//		{
//			System.out.println("player 1");
//		}
//	}
}