package connect4.client.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import connect4.client.ClientController;
import connect4.client.interfaces.GenericUI;

public class LoginView implements GenericUI
{
	private JFrame mainFrame;
	private JPanel mainPanel;
	private JLabel titleLabel;
	private JLabel userLabel;
	private JTextField userText;
	private JButton loginButton;
	
	ClientController controller;
	
	public LoginView(ClientController controller)
	{
		this.controller = controller;
		
		mainFrame = new JFrame("Login");
		mainFrame.setSize(290, 150);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		mainPanel = new JPanel();
		placeComponents();
		mainFrame.add(mainPanel);

		mainFrame.setVisible(true);
		
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
	}

	public void placeComponents()
	{
		mainPanel.setLayout(null);
		
		titleLabel = new JLabel("Choose your name to start the game.");
		titleLabel.setBounds(10, 10, 280, 25);
		mainPanel.add(titleLabel);

		userLabel = new JLabel("Username");
		userLabel.setBounds(10, 45, 80, 25);
		mainPanel.add(userLabel);

		userText = new JTextField(20);
		userText.setBounds(100, 45, 180, 25);
		userText.setText("Horse");
		mainPanel.add(userText);

		loginButton = new JButton("play");
		loginButton.addActionListener(loginButtonListener);
		loginButton.setBounds(180, 83, 100, 25);
		mainPanel.add(loginButton);
	}
	
	private ActionListener loginButtonListener = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			controller.loggedIn(userText.getText());
		}
	};

	@Override
	public void alertMessage(String message)
	{
		JOptionPane.showMessageDialog(null, message);
	}

	@Override
	public int choiceDialog(String title, String message)
	{
		return JOptionPane.showConfirmDialog(mainFrame, message, title, JOptionPane.YES_NO_OPTION);
	}

	@Override
	public void close()
	{
		mainFrame.dispose();
	}
}
