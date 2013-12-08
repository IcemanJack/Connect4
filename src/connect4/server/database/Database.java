package connect4.server.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.postgresql.util.PSQLException;

import connect4.server.database.MockDatabase.NoUsers;
import connect4.server.database.MockDatabase.TableDoesNotExist;
import connect4.server.database.MockDatabase.UserAlreadyExists;
import connect4.server.database.MockDatabase.UserIsNotFound;
import connect4.server.enums.Tables;
import connect4.server.interfaces.IDatabase;
import connect4.server.objects.User;


public class Database implements IDatabase
{
	private final static String url = "jdbc:postgresql://localhost:5432/postgres";
	private final static String user = "postgres";
	private final static String passwd = "mypassword";
	private final String classForName = "org.postgresql.Driver";
	private static Connection connection;

	// Tests
	public static void main(String[] args) throws SQLException, ClassNotFoundException 
	{
		IDatabase db = null;
		try
		{
			db = new Database();
		}
		catch(SQLException e)
		{
			System.err.println("Failed to connect\n"+e.getMessage()+" "+e.getSQLState());
		}
		
		User[] users = null;
		try {users = db.getScoreTable();}
		catch (SQLException e){System.err.println(e.getMessage());}
		catch (NoUsers e){System.err.println(e.getMessage());}
		
		for(User user: users)
		{
			System.out.println(user.getName() + " " + user.getScore());
		}
		
		
		try {System.out.println(db.getTableDescription(Tables.usr));} 
		catch (TableDoesNotExist e1){ e1.printStackTrace();}
		
		User u1 = new User("Peter");
		u1.setScore(1000);
		
		try{db.addUser(u1);}
		catch (SQLException e){System.err.println(e.getMessage());}
		catch (UserAlreadyExists e){e.printStackTrace();}
		
		try { System.out.println(db.getPlayerScore(u1));} 
		catch (UserIsNotFound e) {e.printStackTrace();}
		
		try{db.updateUserScore(u1, 12);}
		catch (SQLException e){e.printStackTrace();}
		catch (UserIsNotFound e) {e.printStackTrace();}
		
		try { System.out.println(db.getPlayerScore(u1));} 
		catch (UserIsNotFound e) {e.printStackTrace();}
		
		
		System.out.println(db.containsUser(u1.getName()));
		
		try{db.removeUser(u1.getName());}
		catch (SQLException e){e.printStackTrace();}
		catch (UserIsNotFound e) {e.printStackTrace();}
		
		System.out.println(db.containsUser(u1.getName()));
	}
	
	public Database() throws ClassNotFoundException, SQLException
	{
		this.openConnection();
		ResultSet rs = connection.createStatement().executeQuery("SELECT VERSION()");
        if (rs.next())
        {
            System.out.println(rs.getString(1));
        }
        rs.close();
	}
	
	@Override
	public void openConnection() throws SQLException, ClassNotFoundException 
	{
		Database.connection = DriverManager.getConnection(url, user, passwd);
		Class.forName(classForName);
		System.out.println("DRIVER OK ! ");
		System.out.println("Connection effective !");
	}
	
	@Override
	public void closeConnection() throws SQLException 
	{
			Database.connection.close();
	}
	
	@Override
	public String getTableDescription(final Tables table) throws SQLException
	{
		String output = "No table " + table;
		DatabaseMetaData dbmd = connection.getMetaData();
        ResultSet rs = dbmd.getColumns(null, null, table.toString(), null);
		if(table == Tables.usr)
		{
			String column = "";
			String lineEnd = "        |\n";
			output = "---------\n| " + table + "   |\n---------"; 
			while (rs.next())
	        {
				column = rs.getString("COLUMN_NAME");
	            output += "\n" + column + lineEnd.substring
	            		(column.length(), lineEnd.length() - 1);
	        }	
			output += "\n---------\n";
		}
		rs.close();
		return output;
	}
	
	@Override
	public void addUser(final User user) throws SQLException 
	{
        try
        {
            String stm = "INSERT INTO usr (name, score) VALUES (?, ?)";
            PreparedStatement state = connection.prepareStatement(stm);
			state.setString(1, user.getName());
			state.setInt(2, user.getScore());
			state.executeUpdate();
			state.close();
        }
        catch(PSQLException e)
        {
        	throw e;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
	}

	@Override
	public void updateUserScore(final User user, final int score) throws SQLException 
	{		
			user.setScore(getPlayerScore(user) + score);
			PreparedStatement state = connection.prepareStatement
					("UPDATE usr SET score = ? WHERE name = ?");
			state.setInt(1, user.getScore());
			state.setString(2, user.getName());
			state.executeUpdate();
			state.close();
	}

	@Override
	public int getPlayerScore(final User user) throws SQLException
	{
		PreparedStatement state = connection.prepareStatement
				("SELECT score FROM usr WHERE name = ?");
		state.setString(1, user.getName());
		ResultSet result = state.executeQuery();
		
		int score = -1;
		if(result.next())
		{
			score = result.getInt("score");
		}
		state.close();
		result.close();
		
		return score;
	}

	@Override
	public void removeUser(final String username) throws SQLException
	{
		PreparedStatement state = connection.prepareStatement
				("DELETE FROM usr WHERE name = ?");
		state.setString(1, username);
		state.execute();
		state.close();
	}

	@Override
	public boolean containsUser(final String username) throws SQLException
	{
		try
		{
			PreparedStatement state = connection.prepareStatement("SELECT name FROM usr WHERE name = ?");
			state.setString(1, username);
	        state.execute();
	        if(state.getMetaData().getColumnCount()>0)
	        {
	        	return true;
	        }
	        state.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public User[] getScoreTable() throws SQLException
	{
		int usersCount = countNumberOfPlayer();
		User[] users = new User[usersCount];
		try
		{
			ResultSet result = connection.prepareStatement
					("SELECT * FROM usr ORDER BY score DESC").executeQuery();
			int counter = 0;
			User user;
			while(result.next())
			{
				user = new User(result.getString("name"));
				user.setScore(result.getInt("score"));
				try
				{
					users[counter] = user;
				}
				catch(IndexOutOfBoundsException e)
				{
					System.err.println(e.getMessage());
					break;
				}	
				counter++;
			}
			result.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return users;
	}
	
	private int countNumberOfPlayer()
	{
		int usersCount = -1;
		try
		{
			ResultSet result = connection.createStatement().executeQuery
					("SELECT * FROM usr;");
			ResultSetMetaData metadata = result.getMetaData();
			usersCount = metadata.getColumnCount();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return usersCount;
	}
	
	/* Not used
	 * If in future we want to store all the games that has been played.
	 */
	@SuppressWarnings("unused")
	private void addGame(User player1, User player2,
			User winner, User loser, boolean isNull) throws SQLException 
	{	
			PreparedStatement state = connection.prepareStatement("INSERT INTO Game"
					+ "(date, user1, user2, winner, loser, isNull) " +
					"VALUES"+"(?,?,?,?,?)");
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
						
			state.setString(1,dateFormat.format(date.getTime()));
			state.setString(2, player1.getName());
			state.setString(3, player2.getName());
			state.setString(4, winner.getName());
			state.setString(5, loser.getName());
			state.setBoolean(6, isNull);
			state.executeUpdate();
			
			state.close();
	}
}