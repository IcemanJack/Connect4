package connect4.local;

import connect4.local.Model;

public class Computer
{
	private Model model = new Model();
	
	public Computer(Model model)
	{
		this.model = model;
		initCurrentTableToken();
	}
	
	public void initCurrentTableToken()
	{
		int totalColumns = 7;
		int totalRows = 6;
		
		for (int i = 0; i < totalColumns; i++)
		{
			for (int j = 0; j < totalRows; j++)
			{
				System.out.println(model.board[i][j]);
			}
		}
	}
}
