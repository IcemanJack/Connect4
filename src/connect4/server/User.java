package connect4.server;

import connect4.client.IModelListener;

public class User 
{
	private String name;
	private IModelListener listener;
	private UserType type;
	
	User(String name, IModelListener listener, UserType type)
	{
		this.name = name;
		this.listener = listener;
		this.type = type;
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
	
	public enum UserType
	{
		PLAYER,
		SPECTATOR
	}
}
