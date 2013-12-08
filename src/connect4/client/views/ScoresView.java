package connect4.client.views;

import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import connect4.server.objects.User;

public class ScoresView
{
	private JFrame mainFrame;
	private JTable scoreTable;
	private User[] users;
	
	private static String scoresAreEmptyMessage = "No scores to show...";
	
	public ScoresView(User[] users)
	{
		this.users = users;
		
		if(users == null)
		{
			alertMessage(scoresAreEmptyMessage);
			close();
		}
		
		mainFrame = new JFrame("Scores");
		mainFrame.setSize(290, 150);
		mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		String[] columnNames = {"Username", "Score"};
		Object[][] data = convertUsersArrayToTwoDimentionalObjectArrayForJTable();
		scoreTable = new JTable(data, columnNames);
		scoreTable.setFont(new Font("Commic", Font.CENTER_BASELINE, 15));
		scoreTable.setEnabled(false);
		setRowsHeight(20);
		
		mainFrame.add(scoreTable);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
		mainFrame.pack();
	}
	
	private Object[][] convertUsersArrayToTwoDimentionalObjectArrayForJTable()
	{
		Object[][] data = new Object[users.length][users.length];
		int count = 0;
		for(User user: users)
		{
			try
			{
				data[count][0] = user.getName();
				data[count][1] = user.getScore();
			}
			catch(NullPointerException e)
			{
				alertMessage(scoresAreEmptyMessage);
				close();
			}
			count++;
		}
		return data;
	}
	
	private void setRowsHeight(int height)
	{
		for(int index = 0; index < users.length - 1; index++)
		{
		    // last one isn't set weird...
		    scoreTable.setRowHeight(index, height);
		}
	}
	
	private void alertMessage(String message)
	{
		JOptionPane.showMessageDialog(null, message);
	}
	
	private void close()
	{
		mainFrame.dispose();
	}
}
