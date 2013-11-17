package connect4;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.imageio.ImageIO;

public class Model
{
	private Map<Players, TokenType> playersAndTokens;
	// 0 = empty, 1 = player 1, 2 = player 2
	private int[][] board;
	//private Map<Integer, Players[]> availablePositions;
	
	private BufferedImage redTokenImage;
	private BufferedImage blackTokenImage;
	private BufferedImage emptyTokenImage;
	
	private LinkedList<ModelUpdateListenerI> viewsListeners = new LinkedList<ModelUpdateListenerI>();
	
	private final int columns;
	private final int rows;
	private final int connectToWin;
	
	private Players currentPlayer;
	
	public Model()
	{
		this.columns = 7;
		this.rows = 6;
		this.connectToWin = 4;
		viewsListeners = new LinkedList<ModelUpdateListenerI>();
		
		makeNewBoard();
		loadTokenImages();	
	}
	public Model(final int columns, final int rows, final int connectToWin)
	{
		this.columns = columns;
		this.rows = rows;
		this.connectToWin = connectToWin;
		board = new int[this.columns][this.rows];
		
		makeNewBoard();
		loadTokenImages();
	}
	
	public final void addListener(final ModelUpdateListenerI listener)
	{
		viewsListeners.add(listener);
	}
	
	public final void initializeObserversViews()
	{
		for(ModelUpdateListenerI listener: viewsListeners)
		{
			listener.initializeViews(columns, rows,
					emptyTokenImage, Players.NONE);
		}
	}
	
	public final void updateObserversViews()
	{
		for(ModelUpdateListenerI listener: viewsListeners)
		{
			listener.updateCurrentPlayer(getCurrentPlayer());
		}
	}
	
	public final void set2Players(final Players _player1, final Players _player2)
	{
		playersAndTokens = new HashMap<Players, TokenType>();
		playersAndTokens.put(_player1, TokenType.BLACK);
		playersAndTokens.put(_player2, TokenType.RED);
	}
	
	public final TokenType getPlayerToken(final Players player)
	{
		if(playersAndTokens.containsKey(player))
		{
			return playersAndTokens.get(player);
		}
		return TokenType.NONE;
	}
	
	public final Players getNextPlayer()
	{
		if(currentPlayer == Players.PLAYER1)
		{
			return Players.PLAYER2;
		}
		return Players.PLAYER1;
	}
	
	public final void setCurrentPlayer(final Players player)
	{
		currentPlayer = player;
	}
	
	public final Players getCurrentPlayer()
	{
		return currentPlayer;
	}
	
	public final int getFloorColumns()
	{
		return columns;
	}
	
	public final int getFloorRows()
	{
		return rows;
	}
	
	public final BufferedImage getTokenImage(final TokenType token)
	{
		switch (token)
		{
			case RED:
				return redTokenImage;
			case BLACK:
				return blackTokenImage;
		}
		return emptyTokenImage;
	}
	
	public final boolean isAvailable(final int column, final int row)
	{
		if(board[column][row] == 0)
		{
			return true;
		}
		return false;
	}
	
	public final void setCurrentPlayerAtPosition(final int column, final int row)
	{
		switch (getCurrentPlayer())
		{
			case NONE:
				board[column][row] = 0;
				break;
			case PLAYER1:
				board[column][row] = 1;
				break;
			case PLAYER2:
				board[column][row] = 2;
				break;
		}
	}
	
	public final Players getPositionPlayer(final int column, final int row)
	{
		try
		{
			return Players.values()[board[columns][rows]];
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			// TODO make throws
			return Players.NONE;
		}
	}
	
	public final void makeNewBoard()
	{
		board = new int[columns][rows];
	}
	
	public final boolean isPositionMakeWinning(final int column, final int row)
	{
		// WinningAlignTokenNumber from current position to both ways & current position
		int caseSize = connectToWin * 2 + 1;
		
		// Horizontal
		Players[] case1 = new Players[caseSize];
		// Vertical
		Players[] case2 = new Players[caseSize];
		// Diagonal West-North & South-East
		Players[] case3 = new Players[caseSize];
		// Diagonal East-North & South-West
		Players[] case4 = new Players[caseSize];

		// The middle index is the actual player, were he just played
		case1[connectToWin] = getCurrentPlayer();
		case2[connectToWin] = getCurrentPlayer();
		case3[connectToWin] = getCurrentPlayer();
		case4[connectToWin] = getCurrentPlayer();
		
		// For all cases go both ways from the actual player index
		for(int i = 1; i <= connectToWin; i++)
		{
			case1[i + connectToWin] = getPositionPlayer(column + i, row);
			case1[connectToWin - i] = getPositionPlayer(column - i, row);	

			case2[i + connectToWin] = getPositionPlayer(column, row + i);
			case2[connectToWin - i] = getPositionPlayer(column, row - i);

			case3[i + connectToWin] = getPositionPlayer(column + i, row + i);
			case3[connectToWin - i] = getPositionPlayer(column - i, row - i);

			case4[i + connectToWin] = getPositionPlayer(column - i, row + i);
			case4[connectToWin - i] = getPositionPlayer(column + i, row - i);
		}
		Map<Integer, Players[]> winningCases = new HashMap<Integer, Players[]>();
		
		winningCases.put(1, case1);
		winningCases.put(2, case2);
		winningCases.put(3, case3);
		winningCases.put(4, case4);
		
		return checkIfWinningCases(winningCases);
	}
	
	private final boolean checkIfWinningCases(final Map<Integer, Players[]> winningCases)
	{
		for(int caseIndex = 1; caseIndex <= 4; caseIndex++)
		{
			Players[] currentCase = winningCases.get(caseIndex);
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
	
	private final void loadTokenImages()
	{
		try
		{
			redTokenImage = ImageIO.read(new File("./src/img/Red50x50.png"));
			blackTokenImage = ImageIO.read(new File("./src/img/Black50x50.png"));
			emptyTokenImage = ImageIO.read(new File("./src/img/Empty50x50.png"));
		}
		catch (IOException e)
		{
			System.out.println("The token image can't be loaded:\n" + e.toString());
		}
	}
}
