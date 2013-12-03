package connect4.server.database;

import connect4.client.IModelListener;

public class User 
{
	private String name;
	private IModelListener listener;
	private UserType type;
	private int score;
	
	// For testing in main of MockDatabase;
	public User(String name)
	{
		this.name = name;
	}
	
	public User(String name, IModelListener listener, UserType type)
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

	public IModelListener getListener()
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

	public enum UserType
	{
		PLAYER,
		SPECTATOR
	}
	
	public String getTableInfo()
	{
		return "|name    |\n" +
				"|listener|\n" +
				"|type    |\n" +
				"|score   |\n";
	}
}
