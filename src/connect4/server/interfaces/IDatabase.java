package connect4.server.interfaces;

import java.sql.SQLException;

import connect4.server.database.MockDatabase.NoUsers;
import connect4.server.database.MockDatabase.TableDoesNotExist;
import connect4.server.database.MockDatabase.UserAlreadyExists;
import connect4.server.database.MockDatabase.UserIsNotFound;
import connect4.server.enums.Tables;
import connect4.server.objects.User;

public interface IDatabase
{
	public void openConnection() throws SQLException, ClassNotFoundException;
	public void closeConnection() throws SQLException;
	
	public void addUser(User user) throws SQLException, UserAlreadyExists;
	public void removeUser(String username) throws SQLException, UserIsNotFound;
	
	public void updateUserScore(User user, int score) throws SQLException, UserIsNotFound;
	
	public boolean containsUser(String username) throws SQLException;
	public int getPlayerScore(User player) throws SQLException, UserIsNotFound;
	public String getTableDescription(Tables table) throws SQLException, TableDoesNotExist;
	public User[] getScoreTable() throws SQLException, NoUsers;
}