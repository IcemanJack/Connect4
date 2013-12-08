package connect4.server.interfaces;

import connect4.client.interfaces.GameListener;
import connect4.server.enums.Status;
import connect4.server.objects.User.UserType;

// used by client
public interface IMyServer 
{
	// will return new name if this one taken
	String validateUsername(String username);
	Status register(String username, GameListener client, UserType userType);

	Status makeMove(int column, int row, String player);
}
