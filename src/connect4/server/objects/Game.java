package connect4.server.objects;


/* Not used
 * If in future we want to store all the games that has been played.
 */
public class Game 
{
	private int id;
	private User player1;
	private User player2;
	private User winner;
	private User loser;
	private boolean isNull;
	
	public Game(){}
	
	public Game(int id, User player1,User player2,
			User winner , User loser, boolean isNull)
	{
		this.id = id;
		this.player1 = player1;
		this.player2 = player2;
		this.winner = winner;
		this.loser = loser;
		this.isNull = isNull;
	}
	public int getId()
	{
		return id;
	}
	
	public User getPlayer1()
	{
		return player1;
	}
	
	public User getPlayer2()
	{
		return player2;
	}

	public void setPlayer2(User player2)
	{
		this.player2 = player2;
	}

	public User getWinner() {
		return winner;
	}

	public User getLoser()
	{
		return loser;
	}
	
	public boolean isNull() 
	{
		return isNull;
	}
	
	public String getTableInfo()
	{
		return "|Player1|\n" +
				"|Player2|\n" +
				"|Winner |\n" +
				"|Loser  |\n" +
				"|IsNull |\n";
	}
}
