package connect4.local;

public class AI 
{
	private CaseType myPlayer = CaseType.PLAYER2;
	private CaseType otherPlayer = CaseType.PLAYER1;
	private int searchLimit;
	private int maxColumn;
	private BoardAnalysis boardAnalysis;
	
	public AI(int limit, CaseType[][] gameBoard) 
	{
		searchLimit = limit;
		this.boardAnalysis = new BoardAnalysis(gameBoard);
	}
	
	
	public int miniMaxDecision(BoardAnalysis board) 
	{
		miniMaxValue(board, 0, myPlayer);
		return maxColumn;
	}
	
	
	private int miniMaxValue(BoardAnalysis board, int depth, CaseType playerToMove)
	{
		
		if (board.isFinished() != CaseType.NONE)
			if (board.isFinished() == myPlayer)
			{
				return 255 - depth;
			}
			else
			{
				return 0 + depth;
			}
		
		if (depth == searchLimit || board.isTie())
		{
			//return board.evaluateContent();
		}
		depth = depth + 1;
		if (playerToMove == myPlayer)
		{
			int max = Integer.MIN_VALUE;
			int column = 0;
			for (int i = 0; i < board.columns; i++) 
				if (board.isLegalMove(i)) 
				{
					board.insert(i, myPlayer);
					int value = miniMaxValue(board, depth, otherPlayer);
					if (max < value) 
					{
						max = value;
						column = i;
					}
					board.remove(i);
				}
			maxColumn = column;
			return max;
		} 
		else
		{
			int min = Integer.MAX_VALUE;
			for (int i = 0; i < board.columns; i++) 
				if (board.isLegalMove(i)) 
				{
					board.insert(i, CaseType.PLAYER2);
					int value = miniMaxValue(board, depth, myPlayer);
					if (min > value)
						min = value;
					board.remove(i);
				}
			return min;
		}
	}
	
	
	public int alphaBetaSearch(BoardAnalysis board) 
	{
		maxValue(board, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
		return maxColumn;
	}
	
	
	private int maxValue(BoardAnalysis board, int depth, int alpha, int beta) 
	{
		
		if (board.isFinished() != CaseType.NONE)
		{
			if (board.isFinished() == myPlayer)
			{
				return 255 - depth;
			}
			else return 0 + depth; 
		}
		
		if (depth == searchLimit || board.isTie())
		{
			//return board.evaluateContent();
		}
		depth = depth + 1;
		int column = 0;
		for (int i = 0; i < board.columns; i++) 
		{ 
			if (board.isLegalMove(i)) 
			{
				board.insert(i, myPlayer);
				int value = minValue(board, depth, alpha, beta);
				if (value > alpha) 
				{
					alpha = value;
					column = i;
				}
				board.remove(i);
				if (alpha >= beta) 
				{
					return alpha;
				}
			}
		}
		maxColumn = column;
		return alpha;
	}

	
	private int minValue(BoardAnalysis board, int depth, int alpha, int beta)
	{
		
		if (board.isFinished() != CaseType.NONE)
		{
			if (board.isFinished() == myPlayer)
			{
				return 255 - depth;
			}
			else return 0 + depth;
		}
		
		if (depth == searchLimit || board.isTie())
		{
			//return board.evaluateContent();
		}
		depth = depth + 1;
		for (int i = 0; i < board.columns; i++) 
		{
			if (board.isLegalMove(i))
			{
				board.insert(i, otherPlayer);
				int value = maxValue(board, depth, alpha, beta);
				if (value < beta)
				{
					beta = value;
				}
				board.remove(i);
				if (beta <= alpha) 
				{
					return beta;
				}
			}
		}
		return beta;
	}
	
	private class BoardAnalysis
	{
		CaseType[][] board;
		public int columns;
		public int rows;
		
		public BoardAnalysis(CaseType[][] gameBoard)
		{
			this.board = gameBoard;
			this.columns = board.length;
			this.rows = board[0].length;
		}
		
		public boolean insert(int column, CaseType currentPlayer) 
		{
			if (column > 6 || column < 0 || board[0][column] != CaseType.NONE)
			{
				return false;
			}
			else 
			{ 
				for (int i = rows-1; i >= 0; i--)
				{
					if (board[i][column] == CaseType.NONE) 
					{
						board[i][column] = currentPlayer;
						break;
					}
				}
				return true;
				}
		}
		
		public void remove(int column)
		{
			for (int i = 0; i < rows; i++) 
			{
				if (board[i][column] != CaseType.NONE) 
				{
					board[i][column] = CaseType.NONE;
					break;
				}
			}
		}
		
		public CaseType isFinished() 
		{
			//check for win horizontally
			for (int row=0; row<rows; row++) 
			{
			    for (int col=0; col<columns-3; col++)
			    {
			    	if (board[row][col] != CaseType.NONE &&
			    		board[row][col] == board[row][col+1] &&  
						board[row][col] == board[row][col+2] && 
						board[row][col] == board[row][col+3]) 
			    	{
						return board[row][col];
			    	}
			    }
			}
			//check for win vertically
			for (int row = 0; row < rows-3; row++)
			{
			    for (int col = 0; col < columns; col++)
			    {
					if (board[row][col] != CaseType.NONE &&
						board[row][col] == board[row+1][col] &&
						board[row][col] == board[row+2][col] &&
						board[row][col] == board[row+3][col])
					{
						return board[row][col];
					}
			    }
			}
			//check for win diagonally (upper left to lower right)
			for (int row = 0; row < rows-3; row++) 
			{
			    for (int col = 0; col < columns-3; col++) 
			    {
					if (board[row][col] != CaseType.NONE &&
						board[row][col] == board[row+1][col+1] &&
						board[row][col] == board[row+2][col+2] &&
						board[row][col] == board[row+3][col+3]) 
					{
						return board[row][col];
					}
			    }
			}
			//check for win diagonally (lower left to upper right)
			for (int row = 3; row < rows; row++) 
			{
			    for (int col = 0; col < columns-3; col++) 
			    {
					if (board[row][col] != CaseType.NONE &&
						board[row][col] == board[row-1][col+1] &&
						board[row][col] == board[row-2][col+2] &&
						board[row][col] == board[row-3][col+3])
					{
						return board[row][col];
					}
			    }
			}
			return CaseType.NONE;
		}
		
		public boolean isTie() 
		{
			for (int j = 0; j < columns; j++)
			{
					if (board[0][j] == CaseType.NONE)
						return false;
			}
			return true;
		}
		/*public int evaluateContent() 
		{
			int utility = 128;
			int sum = 0;
			for (int i = 0; i < rows; i++)
			{
				for (int j = 0; j <columns; j++)
				{
					if (board[i][j] == CaseType.PLAYER1)
					{
						sum += evaluationTable[i][j];
					}
					else if (board[i][j] == CaseType.PLAYER2)
					{
						sum -= evaluationTable[i][j];
					}
				}
			}
			return utility + sum;
		}*/
		public boolean isLegalMove(int column)
{
			if (column > 6 || column < 0 || board[0][column] != CaseType.NONE)
			{
				return false;
			}
			return true;
		
		
	}
	}
}
