package connect4.server;

import java.sql.SQLException;
import java.util.HashSet;

import connect4.server.Database.PlayerScore;


public interface IDatabase
{
	public void getTableInfo(String tableName) throws SQLException;
	public void openConnection() throws SQLException, ClassNotFoundException;
	public void closeConnection() throws SQLException;
	public void registerGameInfo(String player1, String player2, String winner, String loser) throws SQLException;
	public void registerUser(String username, String password, String ipAdress) throws SQLException;
	public void updateWinner(String winner) throws SQLException;
	public void updateLoser(String loser) throws SQLException;
	public PlayerScore getPlayerScore(String player) throws SQLException;
	public HashSet<PlayerScore> getScoreTable() throws SQLException;
	
}
