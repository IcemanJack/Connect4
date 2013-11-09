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
	private Map<Integer, Players[]> availablePositions;
	
	private BufferedImage redTokenImage;
	private BufferedImage blackTokenImage;
	private BufferedImage emptyTokenImage;
	
	private LinkedList<ModelUpdateListenerI> viewsListeners;
	
	private final int floorColumns;
	private final int floorRows;
	private final int winningAlignTokenNumber;
	
	private Players currentPlayer;
	
	public Model()
	{
		floorColumns = 7;
		floorRows = 6;
		winningAlignTokenNumber = 4;
		viewsListeners = new LinkedList<ModelUpdateListenerI>();
		
		initializeAvailablePositions();
		loadTokenImages();	
	}
	public Model(final int columns, final int rows, final int _winningAlignTokenNumber)
	{
		floorColumns = columns;
		floorRows = rows;
		winningAlignTokenNumber = _winningAlignTokenNumber;
		viewsListeners = new LinkedList<ModelUpdateListenerI>();
		
		initializeAvailablePositions();
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
			listener.initializeViews(floorColumns, floorRows,
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
	
	public final int getWinningAlignTokenNumber()
	{
		return winningAlignTokenNumber;
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
		return floorColumns;
	}
	
	public final int getFloorRows()
	{
		return floorRows;
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
		if(availablePositions.get(column)[row] == Players.NONE)
		{
			return true;
		}
		return false;
	}
	
	public final void setCurrentPlayerAtPosition(final int column, final int row)
	{
		availablePositions.get(column)[row] = getCurrentPlayer();
	}
	
	public final Players getPositionPlayer(final int column, final int row)
	{
		if((column < 0) || (column >= floorColumns) || (row < 0) || (row >= floorRows)) 
		{
			return Players.NONE;
		}
		return availablePositions.get(column)[row];
	}
	
	public final void resetAvailablePostions()
	{
		initializeAvailablePositions();
	}
	
	public final boolean checkPositionMakeWinning(final int column, final int row)
	{
		// WinningAlignTokenNumber from current position to both ways & current position
		int caseSize = getWinningAlignTokenNumber() * 2 + 1;
		
		// Horizontal
		Players[] case1 = new Players[caseSize];
		// Vertical
		Players[] case2 = new Players[caseSize];
		// Diagonal West-North & South-East
		Players[] case3 = new Players[caseSize];
		// Diagonal East-North & South-West
		Players[] case4 = new Players[caseSize];

		// The middle index is the actual player, were he just played
		case1[getWinningAlignTokenNumber()] = getCurrentPlayer();
		case2[getWinningAlignTokenNumber()] = getCurrentPlayer();
		case3[getWinningAlignTokenNumber()] = getCurrentPlayer();
		case4[getWinningAlignTokenNumber()] = getCurrentPlayer();
		
		// For all cases go both ways from the actual player index
		for(int i = 1; i <= getWinningAlignTokenNumber(); i++)
		{
			case1[i + getWinningAlignTokenNumber()] = getPositionPlayer(column + i, row);
			case1[getWinningAlignTokenNumber() - i] = getPositionPlayer(column - i, row);	

			case2[i + getWinningAlignTokenNumber()] = getPositionPlayer(column, row + i);
			case2[getWinningAlignTokenNumber() - i] = getPositionPlayer(column, row - i);

			case3[i + getWinningAlignTokenNumber()] = getPositionPlayer(column + i, row + i);
			case3[getWinningAlignTokenNumber() - i] = getPositionPlayer(column - i, row - i);

			case4[i + getWinningAlignTokenNumber()] = getPositionPlayer(column - i, row + i);
			case4[getWinningAlignTokenNumber() - i] = getPositionPlayer(column + i, row - i);
		}
		Map<Integer, Players[]> winningCases = new HashMap<Integer, Players[]>();
		
		winningCases.put(1, case1);
		winningCases.put(2, case2);
		winningCases.put(3, case3);
		winningCases.put(4, case4);
		
		return checkIfWinningCases(winningCases);
	}
	
	private final void initializeAvailablePositions()
	{
		availablePositions = new HashMap<Integer, Players[]>();
		for(int column = 0; column < floorColumns; column++)
		{
			availablePositions.put(column, new Players[floorRows]);
			for(int row = 0; row < floorRows; row++)
			{
				availablePositions.get(column)[row] = Players.NONE;
			}
		}
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
				if(consecutivePlayerMatches >= getWinningAlignTokenNumber())
				{
					return true;
				}
			}
		}
		return false;
	}
}
