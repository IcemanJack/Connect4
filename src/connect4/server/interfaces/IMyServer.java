package connect4.server.interfaces;

import connect4.client.interfaces.GameListener;
import connect4.server.enums.Status;
import connect4.server.objects.User;
import connect4.server.objects.User.UserType;

// used by client
public interface IMyServer 
{
	// will return new name if this one taken
	public String validateUsername(String username);
	public Status register(String username, GameListener client, UserType userType);
	public Status makeMove(int column, int row, String player);
	public User[] getScoreTable();
}
