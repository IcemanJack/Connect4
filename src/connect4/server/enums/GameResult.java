package connect4.server.enums;

public enum GameResult
{
	/* Score definitions
	 * Win: 1 point
	 * Null: 0 points
	 * Loose: -1 point
	 */
	LOSE(-1),
	NULL(0),
	WIN(1);
	
	private final int score;
	
	private GameResult(int score)
	{
	    this.score = score;
	}
	
	public int getScore()
	{
		return score;
	}
}
