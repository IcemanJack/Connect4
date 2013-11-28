package connect4.client;

/* ARGS
 * [1] ipv4
 * [2] username
 */
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
			new ClientController(args[1], 12345, args[2]);
		}
	}
}
