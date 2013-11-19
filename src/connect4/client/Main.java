package connect4.client;

public class Main 
{
	public static void main(String[] args)
	{
		if(args.length != 1)
		{
			new ClientController();
		}
		else
		{
			new ClientController(args[1], 12345);
		}
	}
}
