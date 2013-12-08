package connect4.server.enums;

public enum Status
{
	OPERATION_DONE(0, "Operation done successfully."),
	YOU_WON(1, "You won!"),
	GAME_FULL(2, "The game is already full..."),
	UNKNOWN(3, "Well, this is embarrassing... Something went wrong."),
	WAITING_FOR_SECOND_PLAYER(4, "Sorry, but you can't play alone. Wainting for second player..."),
	NO_AVAILABLE_POSITION(5, "This column is full."),
	ITS_A_NULL(6, "It's a null. You're just too smart for each other!"),
	USER_NOT_PLAYING(7, "You are not in the game."),
	NOT_YOUR_TURN(8, "Isn't your turn. Please wait..."),
	IN_GAME(9, "Can't become spectator, you're already in game."),
	YOU_LOOSE(10, "Since you left... You loose."),
	NOT_LOGGED_IN(11, "You are not logged it"),
	NAME_NOT_VALIDATED(12, "You have to validate the username first.");
	
	private final int code;
	private final String description;
	
	 Status(int code, String description)
	{
	    this.code = code;
	    this.description = description;
	}
	
	public String getDescription()
	{
		return description;
	}

	public int getCode()
	{
		return code;
	}
	
	@Override
	public String toString()
	{
	    return code + ": " + description;
	}
}
