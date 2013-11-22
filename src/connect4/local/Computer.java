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
		for (int i = 0; i < model.columns; i++)
		{
			for (int j = 0; j < model.rows; j++)
			{
				System.out.println(model.board[i][j]);
			}
		}
	}
}
