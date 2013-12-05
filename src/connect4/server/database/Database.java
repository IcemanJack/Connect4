package connect4.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import connect4.client.IModelListener;
import connect4.server.database.MockDatabase.NoUsers;
import connect4.server.database.MockDatabase.UserNotFound;


public class Database implements IDatabase
{
	private static String url = "jdbc:postgresql://localhost:5432/postgres";
	private static String user = "postgres";
	private static String passwd = "mypassword";
	private Connection conn;

	public static void main(String[] args) throws SQLException, ClassNotFoundException 
	{
		try
		{
			IDatabase db = new Database();
		}
		catch(SQLException e)
		{
			System.out.println("Failed to connect\n"+e.getMessage()+" "+e.getSQLState());
		}
	}
	
	public Database() throws ClassNotFoundException, SQLException
	{
		this.openConnection();
	}
	
	@Override
	public String getTableDescription(Tables table) throws SQLException
	{
			PreparedStatement state = conn.prepareStatement("SELECT * FROM ?");
			state.setString(1, table.toString());
			//ResultSet contains the result of SQL request
			ResultSet result = state.executeQuery();
			ResultSetMetaData resultMeta = result.getMetaData();
			
			String output = "";
			
			output += "\n**********************************";
	
			for(int i = 1; i <=  resultMeta.getColumnCount(); i++)
			{
				output += "\t" + resultMeta.getColumnName(i).toUpperCase() + "\t *";
			}
			
			output += "\n**********************************";
			
			while(result.next())
			{			
				for(int i = 1; i <=  resultMeta.getColumnCount(); i++)
				{
					output += "\t" + result.getObject(i).toString() + "\t |";
				}
				output += "\n---------------------------------";
			}
            result.close();
            state.close();
            
            return output;
	}

	@Override
	public void addGame(User player1, User player2,
			User winner, User loser, boolean isNull) throws SQLException 
	{
			
			PreparedStatement state = conn.prepareStatement("INSERT INTO Game"
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
	public void addUser(User user) throws SQLException 
	{
		
			PreparedStatement state = conn.prepareStatement("INSERT INTO User"
					+ "(name, score) " + "VALUES"
					+ "(?,?,?)");
			state.setString(1, user.getName());
			state.setInt(2, user.getScore());
			state.executeUpdate();
			
			state.close();
	}

	@Override
	public void updateUserScore(User user, int score) throws SQLException 
	{
		
			PreparedStatement state = conn.prepareStatement(
					"SELECT score FROM User WHERE name = ?");
			ResultSet result = state.executeQuery();
			result.updateInt("score", user.getScore() + score);
			state.close();
			result.close();
	}
	
	@Override
	public void closeConnection() throws SQLException 
	{
			this.conn.close();
	}

	@Override
	public void openConnection() throws SQLException, ClassNotFoundException 
	{
		this.conn = DriverManager.getConnection(url, user, passwd);
		Class.forName("org.postgresql.Driver");
		System.out.println("DRIVER OK ! ");
		System.out.println("Connection effective !");
	}

	@Override
	public int getPlayerScore(User user) throws SQLException
	{
		
		PreparedStatement state = conn.prepareStatement(
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
	public String getListOfUsers() {
		// TODO Auto-generated method stub
		return null;
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
