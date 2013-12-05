package connect4.server.database;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import connect4.client.IModelListener;
import connect4.server.database.User.UserType;

public class MockDatabase implements IDatabase
{
	Set<Game> games = Collections.synchronizedSet(new HashSet<Game>());
	Map<String, User> users = Collections.synchronizedMap(new HashMap<String, User>());

	// Tests
	public static void main(String[] args) throws SQLException, ClassNotFoundException 
	{
		IDatabase db = new MockDatabase();
		
		User u1 = new User("1", UserType.PLAYER);
		User u2 = new User("1", UserType.PLAYER);
		try
		{
			db.addUser(u1);
			db.addUser(u2);
			// test error
			db.addUser(u1);
		}
		catch (UserAlreadyExists e)
		{
			e.printStackTrace();
		}
		System.out.println(db.getListOfUsers());
		try
		{
			db.updateUserScore(u1, 10);
		}
		catch (UserNotFound e)
		{
			e.printStackTrace();
		}
		try
		{
			db.updateUserScore(u1, -1);
		}
		catch (UserNotFound e)
		{
			e.printStackTrace();
		}
		try
		{
			System.out.println(db.getPlayerScore(u1));
		}
		catch (UserNotFound e)
		{
			e.printStackTrace();
		}
		System.out.println(db.getTableDescription(Tables.Game));
		System.out.println(db.getTableDescription(Tables.User));
	}
	
	@Override
	public String getTableDescription(Tables table) 
	{
		String output = "No table " + table;
		if(table == Tables.Game)
		{
			output = "---------\n| " + table + "  |\n---------\n" + 
					new Game().getTableInfo() +
					"---------\n";
		}
		else if(table == Tables.User)
		{
			output = "----------\n|  " + table + "  |\n----------\n" + 
					new User("u", UserType.PLAYER).getTableInfo() +
					"----------\n";
		}
		return output;
	}
	
	@Override
	public void closeConnection() 
	{
		System.out.println("Connection Closed (MOCK)");
	}

	@Override
	public void openConnection() throws SQLException 
	{
		System.out.println("Connection Opened (MOCK)");
	}

	@Override
	public void addGame(User player1, User player2,
			User winner, User loser, boolean isNull) 
	{
		games.add(new Game(games.size(), player1, player2, winner, loser, isNull));
	}

	@Override
	public void addUser(User user) throws UserAlreadyExists
	{
		if(containsUser(user.getName()))
		{
			throw new UserAlreadyExists();
		}
		users.put(user.getName(), user);
	}
	
	@Override
	public void removeUser(String username) throws UserNotFound
	{
		if(!containsUser(username))
		{
			throw new UserNotFound();
		}
		users.remove(username);
	}

	@Override
	public void updateUserScore(User user, int points) throws UserNotFound
	{
		boolean updated = false;
		for(String current: users.keySet())
		{
			if(current.equals(user.getName()))
			{
				user.setScore(user.getScore() + points);
				updated = true;
			}
		}
		if(!updated)
		{
			throw new UserNotFound();
		}
	}
	
	@Override
	public IModelListener[] getClientsListeners() throws NoUsers
	{
		IModelListener[] listeners =  new IModelListener[users.size()];
		if(users.size() > 0)
		{
			int index = 0;
			for(String usr: users.keySet())
			{
				listeners[index] = users.get(usr).getListener();
				index++;
			}
		}
		else
		{
			throw new NoUsers();
		}
		return listeners;
	}

	@Override
	public int getPlayerScore(User user) throws UserNotFound
	{
		for(String current: users.keySet())
		{
			if(current.equals(user.getName()))
			{
				return users.get(current).getScore();
			}
		}
		throw new UserNotFound();
	}
	
	@Override
	public User getUserByName(String username) throws UserNotFound
	{
		for(String current: users.keySet())
		{
			if(current.equals(username))
			{
				return users.get(current);
			}
		}
		throw new UserNotFound();
	}
	
	@Override
	public String getListOfUsers()
	{
		String output = "[";
		for(String user: users.keySet())
		{
			output += user + ",";
		}
		if (output.endsWith(","))
		{
			output = output.substring(0, output.length() - 1) + "]";
		}
		else if (output.endsWith("["))
		{
			output += "]";
		}
		return output;
	} 
	
	@Override
	public boolean containsUser(String username)
	{
		for(String current: users.keySet())
		{
			if(current.equals(username))
			{
				return true;
			}
		}
		return false;
	}
	
	public class UserNotFound extends Exception
	{
		private static final long serialVersionUID = -4618619414569071264L;

		public UserNotFound() 
	    {
	        super("User not found");
	    }
	}
	
	public class UserAlreadyExists extends Exception
	{
		private static final long serialVersionUID = -40295224831334873L;

		public UserAlreadyExists() 
	    {
	        super("User already exists");
	    }
	}
	
	public class NoUsers extends Exception
	{
		private static final long serialVersionUID = -40295224831334873L;

		public NoUsers() 
	    {
	        super("There is no users");
	    }
	}
}
