package connect4.server.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.postgresql.util.PSQLException;

import connect4.client.IModelListener;
import connect4.server.database.MockDatabase.NoUsers;
import connect4.server.database.MockDatabase.UserAlreadyExists;
import connect4.server.database.MockDatabase.UserNotFound;
import connect4.server.database.User.UserType;


public class Database implements IDatabase
{
	private static String url = "jdbc:postgresql://localhost:5432/postgres";
	private static String user = "postgres";
	private static String passwd = "mypassword";
	private static Connection connection;

	public static void main(String[] args) throws SQLException, ClassNotFoundException 
	{
		IDatabase db = null;
		try
		{
			db = new Database();
		}
		catch(SQLException e)
		{
			System.out.println("Failed to connect\n"+e.getMessage()+" "+e.getSQLState());
		}
		
		System.out.println(db.getTableDescription(Tables.Game));
		System.out.println(db.getTableDescription(Tables.User));
		
		
		try
		{
			db.addUser(new User("Peter", UserType.PLAYER));
		}
		catch (PSQLException e)
		{
			System.out.println(e.getMessage());
		}
		catch (UserAlreadyExists e)
		{
			e.printStackTrace();
		}
		
		System.out.println(db.getListOfUsers());
	}
	
	public Database() throws ClassNotFoundException, SQLException
	{
		this.openConnection();
		ResultSet rs = connection.createStatement().executeQuery("SELECT VERSION()");
        if (rs.next())
        {
            System.out.println(rs.getString(1));
        }
	}
	
	@Override
	public String getTableDescription(Tables table) throws SQLException
	{
		String output = "No table " + table;
		DatabaseMetaData dbmd = connection.getMetaData();
        ResultSet rs = dbmd.getColumns(null, null, table.toString(), null);
		if((table == Tables.Game) || (table == Tables.User))
		{
			String column = "";
			String lineEnd = "        |\n";
			output = "---------\n| " + table + "  |\n---------"; 
			while (rs.next())
	        {
				column = rs.getString("COLUMN_NAME");
	            output += "\n" + column + lineEnd.substring
	            		(column.length(), lineEnd.length() - 1);
	        }	
			output += "\n---------\n";
		}
		return output;
	}
	
	@Override
	public void addUser(User user) throws PSQLException, UserAlreadyExists 
	{
        try
        {
            String stm = "INSERT INTO usr (name, score, type) VALUES (?, ?, ?)";
            PreparedStatement state = connection.prepareStatement(stm);
			state.setString(1, user.getName());
			state.setInt(2, user.getScore());
			state.setInt(3, user.getType().toInt());
			state.executeUpdate();

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
	public void addGame(User player1, User player2,
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

	@Override
	public void updateUserScore(User user, int score) throws SQLException 
	{		
			PreparedStatement state = connection.prepareStatement(
					"SELECT score FROM User WHERE name = ?");
			ResultSet result = state.executeQuery();
			result.updateInt("score", user.getScore() + score);
			state.close();
			result.close();
	}
	
	@Override
	public void closeConnection() throws SQLException 
	{
			Database.connection.close();
	}

	@Override
	public void openConnection() throws SQLException, ClassNotFoundException 
	{
		Database.connection = DriverManager.getConnection(url, user, passwd);
		Class.forName("org.postgresql.Driver");
		System.out.println("DRIVER OK ! ");
		System.out.println("Connection effective !");
	}

	@Override
	public int getPlayerScore(User user) throws SQLException
	{
		
		PreparedStatement state = connection.prepareStatement(
				"SELECT * FROM User WHERE name = ?");
		state.setString(1, user.getName());

		ResultSet result = state.executeQuery();
		int score = result.getInt("score");
		
		state.close();
		result.close();
		
		return score;
	}

	@Override
	public void removeUser(String username) throws UserNotFound {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean containsUser(String username) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public User getUserByName(String username) throws UserNotFound {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getListOfUsers()
	{
		String output = "No users";
		try
		{
			ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM usr");
			output = "Users:\n";
	        while (rs.next())
	        {
	        	output += rs.getString(1) + "\n";
	        }
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
        return output;
	}

	@Override
	public IModelListener[] getClientsListeners() throws NoUsers {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	@Override
	public HashSet<PlayerScore> getScoreTable() throws SQLException
	{
		HashSet<PlayerScore> scoreTable = new HashSet<PlayerScore>();
		PreparedStatement state = conn.prepareStatement("SELECT * FROM ?");
		state.setString(1, "User");
		//ResultSet contains the result of SQL request
		ResultSet result = state.executeQuery();
		while(result.next())
		{
			scoreTable.add(getPlayerScore(result.getString("username")));
		}
		
		return scoreTable;
	}
	
	public class PlayerScore
	{
		public String username ="";
		public int win = 0;
		public int lost = 0;
	}
	*/
}
