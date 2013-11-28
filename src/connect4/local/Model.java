package connect4.local;

import java.util.LinkedList;

public class Model
{
	public CaseType[][] board;
	
	private LinkedList<ModelUpdateListenerI> viewsListeners = new LinkedList<ModelUpdateListenerI>();
	
	public final int columns;
	public final int rows;
	public final int connectToWin;
	
	private CaseType currentPlayer;
	
	public Model()
	{
		this.columns = 7;
		this.rows = 6;
		this.connectToWin = 4;
	}
	public Model(final int columns, final int rows, final int connectToWin)
	{
		this.columns = columns;
		this.rows = rows;
		this.connectToWin = connectToWin;
	}
	
	public void makeNewBoard()
	{
		board = new CaseType[this.columns][this.rows];
		
		for(int column = 0; column < this.columns; column++)
		{
			for(int row = 0; row < this.rows; row++)
			{
				board[column][row] = CaseType.NONE;
			}
		}
	}
	
	public final void addListener(final ModelUpdateListenerI listener)
	{
		viewsListeners.add(listener);
	}
	
	public final void initializeObserversViews()
	{
		for(ModelUpdateListenerI listener: viewsListeners)
		{
			listener.initializeViews(columns, rows);
		}
	}
	
	public final void updateListenersCurrentPlayer()
	{
		for(ModelUpdateListenerI listener: viewsListeners)
		{
			listener.updateCurrentPlayer(getCurrentPlayer());
		}
	}
	
	public final CaseType getNextPlayer()
	{
		if(currentPlayer == CaseType.PLAYER1)
		{
			return CaseType.PLAYER2;
		}
		return CaseType.PLAYER1;
	}
	
	public final void setCurrentPlayer(final CaseType player)
	{
		currentPlayer = player;
	}
	
	public final CaseType getCurrentPlayer()
	{
		return currentPlayer;
	}
	
	public final boolean isAvailable(final int column, final int row)
	{
		if(board[column][row] == CaseType.NONE)
		{
			return true;
		}
		return false;
	}
	
	public final boolean floorIsFull()
	{
		for(int column = 0; column < this.columns; column++)
		{
			if(getColumnLowestFreeRow(column) != -1)
			{
				return false;
			}
		}
		return true;
	}
	
	public final int getColumnLowestFreeRow(final int column)
	{
		for(int currentRow = this.rows - 1; currentRow >= 0; currentRow--)
		{
				if(isAvailable(column, currentRow))
				{
					return currentRow;
				}
		}
		return -1;
	}
	
	public final void setCurrentPlayerAtPosition(final int column, final int row)
	{
		try
		{
			board[column][row] = currentPlayer;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			// TODO
		}
	}
	
	private final CaseType getPositionCaseType(final int column, final int row)
	{
		CaseType caseType = null;
		try
		{
			caseType = board[column][row];
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			// TODO
			//System.out.println("getPositionCaseType is outOfBounds: "+ column + " " + row);
		}
		return caseType;
	}
	
	// TODO change this, make A LOT of ArrayIndexOutOfBoundsException with getPositionCaseType()
	public final boolean isPositionMakeWinning(final int column, final int row)
	{
		// WinningAlignTokenNumber from current position to both ways & current position
		int caseSize = connectToWin * 2 + 1;
		
		// Horizontal
		CaseType[] case1 = new CaseType[caseSize];
		// Vertical
		CaseType[] case2 = new CaseType[caseSize];
		// Diagonal West-North & South-East
		CaseType[] case3 = new CaseType[caseSize];
		// Diagonal East-North & South-West
		CaseType[] case4 = new CaseType[caseSize];

		// The middle index is the actual player, were he just played.
		case1[connectToWin] = getCurrentPlayer();
		case2[connectToWin] = getCurrentPlayer();
		case3[connectToWin] = getCurrentPlayer();
		case4[connectToWin] = getCurrentPlayer();
		
		// For all cases go both ways from the actual player index.
		for(int i = 1; i <= connectToWin; i++)
		{
			case1[i + connectToWin] = getPositionCaseType(column + i, row);
			case1[connectToWin - i] = getPositionCaseType(column - i, row);	

			case2[i + connectToWin] = getPositionCaseType(column, row + i);
			case2[connectToWin - i] = getPositionCaseType(column, row - i);

			case3[i + connectToWin] = getPositionCaseType(column + i, row + i);
			case3[connectToWin - i] = getPositionCaseType(column - i, row - i);

			case4[i + connectToWin] = getPositionCaseType(column - i, row + i);
			case4[connectToWin - i] = getPositionCaseType(column + i, row - i);
		}
		
		CaseType[][] winningCases = new CaseType[4][caseSize];
		winningCases[0] = case1;
		winningCases[1] = case2;
		winningCases[2] = case3;
		winningCases[3] = case4;
		
		return checkIfWinningCases(winningCases);
	}
	
	private final boolean checkIfWinningCases(final CaseType[][] winningCases)
	{
		for(int caseIndex = 0; caseIndex < 4; caseIndex++)
		{
			CaseType[] currentCase = winningCases[caseIndex];
			int consecutivePlayerMatches = 0;
			for(int index = 0; index < currentCase.length; index++)
			{
				if(currentCase[index] == getCurrentPlayer())
				{
					consecutivePlayerMatches++;
				}
				else
				{
					consecutivePlayerMatches = 0;
				}
				if(consecutivePlayerMatches >= connectToWin)
				{
					return true;
				}
			}
		}
		return false;
	}
}
