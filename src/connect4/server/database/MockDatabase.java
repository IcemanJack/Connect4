package connect4.server.database;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class MockDatabase implements IDatabase
{
	// TODO change for HashSet
	Set<Game> games = Collections.synchronizedSet(new HashSet<Game>());
	Set<User> users = Collections.synchronizedSet(new HashSet<User>());

	// Tests
	public static void main(String[] args) throws SQLException, ClassNotFoundException 
	{
		IDatabase db = new MockDatabase();
		
		User u1 = new User("1");
		User u2 = new User("2");
		db.addGame(u1, u2, u1, u2, false);
		if(!db.containsUser(u1))
		{
			db.addUser(u1);
		}
		db.updateUserScore(u1, 10);
		db.updateUserScore(u1, -1);
		
		System.out.println(db.containsUser(u1));
		System.out.println(db.getPlayerScore(u1));
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
					new User("u").getTableInfo() +
					"----------\n";
		}
		return output;
	}

	@Override
	public void addGame(User player1, User player2,
			User winner, User loser, boolean isNull) 
	{
		games.add(new Game(games.size(), player1, player2, winner, loser, isNull));
	}

	@Override
	public void addUser(User user) 
	{
		users.add(user);
	}

	@Override
	public void updateUserScore(User user, int points) 
	{
		for(User current: users)
		{
			if(current.getName().equals(user.getName()))
			{
				user.setScore(user.getScore() + points);
			}
		}
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
	public int getPlayerScore(User user)
	{
		for(User current: users)
		{
			if(current.getName().equals(user.getName()))
			{
				return current.getScore();
			}
		}
		return -1;
	}
	
	@Override
	public boolean containsUser(User user)
	{
		for(User current: users)
		{
			if(current == user)
			{
				return true;
			}
		}
		return false;
	}
}
