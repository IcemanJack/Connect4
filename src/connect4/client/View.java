package connect4.client;

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

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import connect4.server.CaseType;

public class View implements IModelListener
{
	private BufferedImage redTokenImage;
	private BufferedImage blackTokenImage;
	private BufferedImage emptyTokenImage;
	
	private JFrame mainFrame;
	private JPanel mainPanel;
	private JLabel playerTurnLabel;
	private JPanel playgroundPanel;
	
	private int floorRows;
	private int floorColumns;
	private ClientController controller;
	
	public View( ClientController controller)
	{
		// INIT
		this.controller = controller;
		
		mainFrame = new JFrame();
		mainFrame.addWindowListener(windowListener);
		
		mainPanel = new JPanel(new GridLayout(2, 1));
		mainPanel.setName("MainPanel");
		
		loadTokenImages();
	}
	
	public void makeNewPlayground()
	{
		// INIT
		if(playgroundPanel != null)
		{
			mainFrame.getContentPane().remove(playgroundPanel);
		}
		playgroundPanel = new JPanel(
				new GridLayout
				(
						floorRows, floorColumns
				));
		playgroundPanel.setName("PlaygroundPanel");
		// POPULATE
		int totalTokens = floorColumns * floorRows;
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
		int index = (row * floorColumns) + column;
		playgroundPanel.remove(index);

		JLabel tokenImageLabel = new JLabel(new ImageIcon(getCaseImage(caseType)));
		tokenImageLabel.addMouseListener(tokenImageMouseListener);
		playgroundPanel.add(tokenImageLabel, index);

		mainFrame.pack();
		mainFrame.repaint();
	}
	
	public int endGameChoiceDialog(String message, String title)
	{
		return JOptionPane.showConfirmDialog(mainFrame, message, title, JOptionPane.YES_NO_OPTION);
	}
	
	@Override
	public void initializeView( int floorColumns, int floorRows) 
	{
		System.out.println("Initialzing view");
		this.floorColumns = floorColumns;
		this.floorRows = floorRows;
		playerTurnLabel = new JLabel();

		makeMenuBar();
		mainPanel.add(playerTurnLabel);
		mainFrame.getContentPane().add(mainPanel);
		makeNewPlayground();
		
	  mainFrame.pack();
		mainFrame.setResizable(false);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	@Override
	public void updateCurrentPlayer(String player) 
	{
		System.out.println("Updating current player");
		getMainPanel().remove(playerTurnLabel);
		playerTurnLabel = new JLabel(player + " turn!");
		getMainPanel().add(playerTurnLabel);
		mainFrame.pack();
		mainFrame.repaint();
	}
	
	@Override
	public void updateListenerNotAvailableUsername(String username)
	{
		controller.updateNotAvailableUsername(username);
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
	  
	  JMenuItem quitSubMenu = new JMenuItem("Exit", KeyEvent.VK_E);
	  quitSubMenu.addActionListener(exitSubMenuListener);
	  
	  fileMenu.add(quitSubMenu);
	  menuBar.add(fileMenu);
	  mainPanel.add(menuBar);
	}
	
	private JPanel getMainPanel()
	{
		return (JPanel) mainFrame.getContentPane().getComponents()[0];
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
		public void windowClosed(WindowEvent arg0) {
			// TODO Auto-generated method stub
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
}
