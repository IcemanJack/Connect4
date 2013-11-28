package connect4.server;

public interface UserMessages
{
	String WELCOME = "Welcome!";
	String GAME_FULL = "The game is already full...";
	String UNKNOWN = "Well, this is embarrassing... Something went wrong.";
	String WAITING_FOR_SECOND_PLAYER = "Sorry, but you can't play alone. Wainting for second player...";
	String NO_AVAILABLE_POSITION = "This column is full.";
	String MOVE_DONE = "Move done.";
	String YOU_WON = "You won! Give that man a cookie.";
	String NOONE_WON = "It's a null. You're just too smart for each other!";
	String USER_NOT_PLAYING = "You are not in the game.";
}
