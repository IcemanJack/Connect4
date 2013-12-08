package connect4.server.database;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import connect4.server.enums.Tables;
import connect4.server.interfaces.IDatabase;
import connect4.server.objects.User;

public class MockDatabase implements IDatabase
{
	private Map<String, User> users = Collections.synchronizedMap(new TreeMap<String, User>());

	// Tests
	public static void main(String[] args) throws SQLException, ClassNotFoundException 
	{
		IDatabase db = new MockDatabase();
		
		User u1 = new User("user");
		User u2 = new User("user");
		try
		{
			db.addUser(u1);
			db.addUser(u2);
			// test error
			db.addUser(u1);
		}
		catch (UserAlreadyExists e){System.err.println(e.getMessage());}
		
		try
		{
			db.updateUserScore(u1, 10);
		}
		catch (UserIsNotFound e){e.printStackTrace();}
		
		try
		{
			db.updateUserScore(u1, -1);
		}
		catch (UserIsNotFound e){e.printStackTrace();}
		
		try
		{
			System.out.println(db.getPlayerScore(u1));
		}
		catch (UserIsNotFound e){e.printStackTrace();}
		
		try 
		{
			System.out.println(db.getTableDescription(Tables.usr));
		}
		catch (TableDoesNotExist e) {System.err.println(e.getMessage());}
		
		User[] users = null;
		try
		{
			users = db.getScoreTable();
		}
		catch (SQLException e){System.err.println(e.getMessage());}
		catch (NoUsers e){System.err.println(e.getMessage());}
		
		for(User user: users)
		{
			System.out.println(user.getName()+" "+user.getScore());
		}
	}
	
	@Override
	public String getTableDescription(final Tables table) 
	{
		String output = "No table " + table;
		if(table == Tables.usr)
		{
			output = "----------\n|  " + table + "   |\n----------\n" + 
					new User("u").getTableInfo() +
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
	public void addUser(final User user) throws UserAlreadyExists
	{
		if(containsUser(user.getName()))
		{
			throw new UserAlreadyExists();
		}
		users.put(user.getName(), user);
	}
	
	@Override
	public void removeUser(final String username) throws UserIsNotFound
	{
		if(!containsUser(username))
		{
			throw new UserIsNotFound();
		}
		users.remove(username);
	}

	@Override
	public void updateUserScore(final User user, final int points) throws UserIsNotFound
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
			throw new UserIsNotFound();
		}
	}
	
	@Override
	public int getPlayerScore(final User user) throws UserIsNotFound
	{
		for(String current: users.keySet())
		{
			if(current.equals(user.getName()))
			{
				return users.get(current).getScore();
			}
		}
		throw new UserIsNotFound();
	}
	
	@Override
	public User[] getScoreTable() throws NoUsers
	{
		User[] usrs = new User[users.size()];
		int count = 0;
		for(User user: users.values())
		{
			usrs[count] = user;
			count++;
		}
		return usrs;
	}
	
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
	
	public class TableDoesNotExist extends Exception
	{
		private static final long serialVersionUID = -4618619414569071264L;

		public TableDoesNotExist() 
	    {
	        super("The table doesn't exists.");
	    }
	}	
	public class UserIsNotFound extends Exception
	{
		private static final long serialVersionUID = -4618619414569071264L;

		public UserIsNotFound() 
	    {
	        super("User isn't found.");
	    }
	}
	
	public class UserAlreadyExists extends Exception
	{
		private static final long serialVersionUID = -40295224831334873L;

		public UserAlreadyExists() 
	    {
	        super("User already exists.");
	    }
	}
	
	public class NoUsers extends Exception
	{
		private static final long serialVersionUID = -40295224831334873L;

		public NoUsers() 
	    {
	        super("There is no users in the table.");
	    }
	}

	@Override
	public void addGame(User player1, User player2, User winner, User loser,
			boolean isNull) throws SQLException 
	{
		System.out.println("Mock called to register game");
	}
}
