package connect4.client.interfaces;

public interface GenericUI 
{
	// local user actions from controller
	public void alertMessage(String message);
	public int choiceDialog(String title, String message);
	public void close();
}
