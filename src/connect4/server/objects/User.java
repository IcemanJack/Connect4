package connect4.server.objects;

import connect4.client.interfaces.GameListener;

public class User 
{
	private String name;
	private GameListener listener;
	private UserType type;
	private int score;
	
	// Tests
	public User(String name)
	{
		this.name = name;
		this.score = 0;
	}
	
	// MockDabase
	public User(String name, GameListener listener, UserType type)
	{
		this.name = name;
		this.listener = listener;
		this.type = type;
		score = 0;
	}

	public void setName(String name)
	{
		this.name = name;
	}	
	
	public String getName()
	{
		return name;
	}

	public GameListener getListener()
	{
		return listener;
	}

	public void setType(UserType type)
	{
		this.type = type;
	}	
	
	public UserType getType()
	{
		return type;
	}
	
	public int getScore()
	{
		return score;
	}

	public void setScore(int score)
	{
		this.score = score;
	}
	
	public String getTableInfo()
	{
		return "|name    |\n" +
				"|listener|\n" +
				"|type    |\n" +
				"|score   |\n";
	}
	
	public enum UserType
	{
		PLAYER(0),
		SPECTATOR(2);
		
		private final int value;
		
	    private UserType(int value)
	    {
	        this.value = value;
	    }
		
		public int toInt()
		{
			return value;
		}
	}
}
