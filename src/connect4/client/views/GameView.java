package connect4.client.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import connect4.client.ClientController;
import connect4.client.interfaces.GameListener;
import connect4.client.interfaces.GenericUI;
import connect4.server.enums.CaseType;

public class GameView implements GameListener, GenericUI
{
	private BufferedImage redTokenImage;
	private BufferedImage blackTokenImage;
	private BufferedImage emptyTokenImage;
	
	private JFrame mainFrame;
	private JPanel mainPanel;
	private JLabel usernameLabel;
	private JLabel currentPlayerLabel;
	private JPanel playgroundPanel;
	
	private int rows;
	private int columns;
	private ClientController controller;
	
	public GameView(ClientController controller)
	{
		this.controller = controller;
		
		mainFrame = new JFrame();
		mainFrame.addWindowListener(windowListener);
		
		mainPanel = new JPanel(new GridLayout(3, 1));
		mainPanel.setName("MainPanel");
		
		loadTokenImages();
	}
	
	@Override
	public void initializeView(AtomicInteger columns, AtomicInteger rows, String username, String current)
	{
		this.columns = columns.get();
		this.rows = rows.get();
		
		makeMenuBar();
		
		currentPlayerLabel = new JLabel("Now playing: " + current);
		usernameLabel = new JLabel("Username: " + username);
		mainPanel.add(usernameLabel);
		mainPanel.add(currentPlayerLabel);
		
		mainFrame.getContentPane().add(mainPanel);
		makeNewPlayground();
		
		mainFrame.pack();
		mainFrame.setResizable(false);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void makeNewPlayground()
	{
		if(playgroundPanel != null)
		{
			mainFrame.getContentPane().remove(playgroundPanel);
		}
		playgroundPanel = new JPanel(
				new GridLayout
				(
						rows, columns
				));
		playgroundPanel.setName("PlaygroundPanel");
		// POPULATE
		int totalTokens = columns * rows;
		for(int i = 0; i < totalTokens; i++)
		{
			JLabel tokenImageLabel = new JLabel(new ImageIcon(emptyTokenImage));
			tokenImageLabel.addMouseListener(tokenImageMouseListener);
			playgroundPanel.add(tokenImageLabel);
		}
		mainFrame.getContentPane().add(playgroundPanel, BorderLayout.SOUTH);
		mainFrame.pack();
		mainPanel.repaint();
	}
	
	@Override
	public void updateCase(int column, int row, CaseType caseType)
	{
		int index = (row * columns) + column;
		playgroundPanel.remove(index);

		JLabel tokenImageLabel = new JLabel(new ImageIcon(getCaseImage(caseType)));
		tokenImageLabel.addMouseListener(tokenImageMouseListener);
		playgroundPanel.add(tokenImageLabel, index);

		mainFrame.pack();
		mainFrame.repaint();
	}
	
	@Override
	public void updateCurrentPlayer(String username) 
	{
		if(currentPlayerLabel != null)
		{
			mainPanel.remove(currentPlayerLabel);
		}
		currentPlayerLabel = new JLabel("Now playing: " + username);
		mainPanel.add(currentPlayerLabel);
		mainFrame.pack();
		mainFrame.repaint();
	}
	
	@Override
	public void updateEndOfTheGame(String message)
	{
		alertMessage(message);
		controller.quitTheGame();
	}

	@Override
	public void alertMessage(String message) 
	{
		JOptionPane.showMessageDialog(null, message);
	}

	@Override
	// @return 0 = yes | 1 = no | -1 = exit
	public int choiceDialog(String title, String message)
	{
		return JOptionPane.showConfirmDialog(mainFrame, message, title, JOptionPane.YES_NO_OPTION);
	}
	
	private BufferedImage getCaseImage(CaseType caseType)
	{
		switch (caseType)
		{
			case PLAYER1:
				return redTokenImage;
			case PLAYER2:
				return blackTokenImage;
		}
		return emptyTokenImage;
	}
	
	private void makeMenuBar()
	{
	  JMenuBar menuBar = new JMenuBar();
	  
	  JMenu fileMenu = new JMenu("File"); 
	  fileMenu.setMnemonic(KeyEvent.VK_F);
	  
	  JMenuItem quitSubMenu = new JMenuItem("Quit/Exit", KeyEvent.VK_E);
	  quitSubMenu.addActionListener(exitSubMenuListener);
	  
	  fileMenu.add(quitSubMenu);
	  menuBar.add(fileMenu);
	  mainPanel.add(menuBar);
	}

	private int[] getClickedImageLabel( JLabel imageLabel)
	{
		int[] clickedImageXY = new int[2];
		for(Component component: playgroundPanel.getComponents())
		{
			JLabel currentImageLabel = (JLabel) component;
			if(imageLabel == currentImageLabel)
			{
				clickedImageXY[0] = component.getY();
				clickedImageXY[1] = component.getX();
				if(clickedImageXY[0] > 0)
				{
					clickedImageXY[0] /= 50;
				}
				if(clickedImageXY[1] > 0)
				{
					clickedImageXY[1] /= 50;
				}
				break;
			}
		}
		return clickedImageXY;
	}

	private ActionListener exitSubMenuListener = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			controller.quitTheGame();
		}
	};
	
	private void loadTokenImages()
	{
		try
		{
			redTokenImage = ImageIO.read(new File("./src/img/Red50x50.png"));
			blackTokenImage = ImageIO.read(new File("./src/img/Black50x50.png"));
			emptyTokenImage = ImageIO.read(new File("./src/img/Empty50x50.png"));
		}
		catch (IOException e)
		{
			System.out.println("The token image can't be loaded:\n" + e.toString());
		}
	}
	
	private MouseListener tokenImageMouseListener = new MouseListener()
	{
		@Override
		public void mouseClicked(MouseEvent e) 
		{
			int[] clickedImageXY = getClickedImageLabel((JLabel) e.getSource());
			controller.makeMove(clickedImageXY[0], clickedImageXY[1]);
		}

		@Override
		public void mouseEntered(MouseEvent e) 
		{
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseExited(MouseEvent e) 
		{
			// TODO Auto-generated method stub
		}

		@Override
		public void mousePressed(MouseEvent e) 
		{
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseReleased(MouseEvent e) 
		{
			// TODO Auto-generated method stub
		}
	};
	
	private final WindowListener windowListener = new WindowListener() 
	{

		@Override
		public void windowActivated(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosed(WindowEvent arg0)
		{
			controller.quitTheGame();
		}

		@Override
		public void windowClosing(WindowEvent arg0) 
		{
			controller.quitTheGame();
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowIconified(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowOpened(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	};

	
	@Override
	public void close()
	{
		mainFrame.dispose();
	}
}
