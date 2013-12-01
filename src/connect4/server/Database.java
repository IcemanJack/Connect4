package connect4.server;

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
import java.util.HashSet;
import java.util.Map;

public class Database implements IDatabase
{
	
	private final String url = "jdbc:postgresql://localhost:5432/postgres";
	private final String user = "postgres";
	private final String passwd = "mypassword";
	private Connection conn;

	public static void main(String[] args) throws SQLException, ClassNotFoundException 
	{
		Database myDB = new Database();
		myDB.getTableInfo("User");
	}
	
	public Database() throws ClassNotFoundException, SQLException
	{
		this.openConnection();
	}
	
	@Override
	public void getTableInfo(String tableName) throws SQLException
	{

			PreparedStatement state = conn.prepareStatement("SELECT * FROM ?");
			state.setString(1, tableName);
			//ResultSet contains the result of SQL request
			ResultSet result = state.executeQuery();
			ResultSetMetaData resultMeta = result.getMetaData();
			
			System.out.println("\n**********************************");
	
			for(int i = 1; i <=  resultMeta.getColumnCount(); i++)
				System.out.print("\t" + resultMeta.getColumnName(i).toUpperCase() + "\t *");
			
			System.out.println("\n**********************************");
			
			while(result.next())
			{			
				for(int i = 1; i <=  resultMeta.getColumnCount(); i++)
				{
					System.out.print("\t" + result.getObject(i).toString() + "\t |");
				}
			System.out.println("\n---------------------------------");
			}
			
            result.close();
            state.close();			
	}

	@Override
	public void registerGameInfo(String player1, String player2,
			String winner, String loser) throws SQLException 
	{
			
			PreparedStatement state = conn.prepareStatement("INSERT INTO Game"
					+ "(date, user1, user2, winner, loser) " +
					"VALUES"+"(?,?,?,?,?)");
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
						
			state.setString(1,dateFormat.format(date.getTime()));
			state.setString(2, player1);
			state.setString(3, player2);
			state.setString(4, winner);
			state.setString(5, loser);
			state.executeUpdate();
			
			state.close();
	}

	@Override
	public void registerUser(String username, String password, String ipAdress) throws SQLException 
	{
		
			PreparedStatement state = conn.prepareStatement("INSERT INTO Game"
					+ "(username, password, ipAdress) " + "VALUES"
					+ "(?,?,?)");
			state.setString(1, username);
			state.setString(2, password);
			state.setString(3, ipAdress);
			state.executeUpdate();
			
			state.close();
	}

	@Override
	public void updateWinner(String winner) throws SQLException 
	{
		
			PreparedStatement state = conn.prepareStatement("SELECT nbrVictory FROM User WHERE username = ?");
			state.setString(1, winner);
			
			ResultSet result = state.executeQuery();
			int nbrVictory = result.getInt("nbrVictory");
			nbrVictory++;
			result.updateInt("nbrVictory", nbrVictory);
			
			state.close();
			result.close();
	}

	@Override
	public void updateLoser(String loser) throws SQLException 
	{
		
			PreparedStatement state = conn.prepareStatement("SELECT nbrLost FROM User WHERE username = ?");
			state.setString(1, loser);

			ResultSet result = state.executeQuery();
			int nbrLost = result.getInt("nbrLost");
			nbrLost++;
			result.updateInt("nbrLost", nbrLost);
			
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
	public PlayerScore getPlayerScore(String player) throws SQLException
	{
		
		PreparedStatement state = conn.prepareStatement("SELECT * FROM User WHERE username = ?");
		state.setString(1, player);

		ResultSet result = state.executeQuery();
		String username = player;
		int nbrLost = result.getInt("nbrLost");
		int nbrWin = result.getInt("nbrVictory");
		
		PlayerScore playerScore = new PlayerScore();
		playerScore.username = username;
		playerScore.lost = nbrLost;
		playerScore.win = nbrWin;
		
		state.close();
		result.close();
		return playerScore;
	}

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
	
}
