package connect4.server;

import java.sql.SQLException;
import java.util.HashSet;

import connect4.server.Database.PlayerScore;


public class MockDatabase implements IDatabase
{

	public static void main(String[] args) 
	{
				
	}

	@Override
	public void getTableInfo(String tableName) 
	{
		System.out.println("getTableInfo() mock called");
		
	}

	@Override
	public void registerGameInfo(String player1, String player2,
			String winner, String loser) 
	{
		System.out.println("registerGameInfo() mock called");
		System.out.println("Info: player1:" + player1 + " player2:" + player2 + " winner:" + winner + " loser:" + loser);
		
	}

	@Override
	public void registerUser(String username, String password, String ipAdress) 
	{
		System.out.println("regiterUser() mock called");
		System.out.println("info: username:" + username + " password:" + password + " ipAdress:" + ipAdress);
		
	}

	@Override
	public void updateWinner(String winner) 
	{
		System.out.println("updateWinner() mock called");
		System.out.println("info: winner:" + winner);
		
	}

	@Override
	public void updateLoser(String loser) 
	{
		System.out.println("updateLoser() mock called");
		System.out.println("info: winner:" + loser);		
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
	public PlayerScore getPlayerScore(String player)
	{
		
		return null;
	}

	@Override
	public HashSet<PlayerScore> getScoreTable() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}



}
