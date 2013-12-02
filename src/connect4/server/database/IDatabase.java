package connect4.server.database;

import java.sql.SQLException;

public interface IDatabase
{
	public void openConnection() throws SQLException, ClassNotFoundException;
	public void closeConnection() throws SQLException;
	
	public void addGame(User player1, User player2,
			User winner, User loser, boolean isNull) throws SQLException;
	public void addUser(User user) throws SQLException;
	public void updateUserScore(User user, int score) throws SQLException;
	
	public int getPlayerScore(User player) throws SQLException;
	public String getTableDescription(Tables table) throws SQLException;
	
	public boolean containsUser(User user);
}
