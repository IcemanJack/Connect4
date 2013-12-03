package connect4.server.database;

import java.sql.SQLException;

import connect4.client.IModelListener;
import connect4.server.database.MockDatabase.NoUsers;
import connect4.server.database.MockDatabase.UserAlreadyExists;
import connect4.server.database.MockDatabase.UserNotFound;

public interface IDatabase
{
	public void openConnection() throws SQLException, ClassNotFoundException;
	public void closeConnection() throws SQLException;
	
	public void addGame(User player1, User player2,
			User winner, User loser, boolean isNull) throws SQLException;
	public void addUser(User user) throws SQLException, UserAlreadyExists;
	public void updateUserScore(User user, int score) throws SQLException, UserNotFound;
	
	public void removeUser(String username) throws UserNotFound;
	
	public boolean containsUser(String username);
	
	public int getPlayerScore(User player) throws SQLException, UserNotFound;
	public String getTableDescription(Tables table) throws SQLException;
	public User getUserByName(String username) throws UserNotFound;
	public String getListOfUsers();
	public IModelListener[] getClientsListeners() throws NoUsers;
}
