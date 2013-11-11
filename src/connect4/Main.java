package connect4;

public final class Main 
{
	public static void main(String[] args) 
	{
		int totalColumns = 7;
		int totalRows = 6;
		int winningAlignTokenNumber = 4;
		if(args.length == 0)
		{
			new Controller(totalColumns, totalRows, winningAlignTokenNumber);
		}
		else if(args.length == 3)
		{
			try
			{
				totalColumns = Integer.parseInt(args[0]);
				totalRows = Integer.parseInt(args[1]);
				winningAlignTokenNumber = Integer.parseInt(args[2]);
			}
			catch(NumberFormatException e)
			{
				throw new IllegalArgumentException("Arguments must be digits");
			}
			if((totalColumns < 3) || (totalRows < 3) || (winningAlignTokenNumber < 3))
			{
				throw new IllegalArgumentException("All the input params must be at least 3 digits long.");
			}
			else
			{
				new Controller(totalColumns, totalRows, winningAlignTokenNumber);
			}
			
		}
		else 
		{
			throw new IllegalArgumentException("The game takes 3 arguments (Colomns, Rows, Number of tokens to win)");
		}
		
	}

}