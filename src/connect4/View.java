package connect4;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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

public class View implements ModelUpdateListenerI
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
	private final Controller controller;
	
	public View(final Controller controller)
	{
		// INIT
		this.controller = controller;
		mainFrame = new JFrame();
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
	
	public final void updateToken(final int column, final int row, TokenType playerToken)
	{
		int index = (row * floorColumns) + column;
		playgroundPanel.remove(index);

		JLabel tokenImageLabel = new JLabel(new ImageIcon(getTokenImage(playerToken)));
		tokenImageLabel.addMouseListener(tokenImageMouseListener);
		playgroundPanel.add(tokenImageLabel, index);

		mainFrame.pack();
		mainFrame.repaint();
	}
	
	public final int endGameChoiceConformDialog(final String message, final String title)
	{
		return JOptionPane.showConfirmDialog(mainFrame, message, title, JOptionPane.YES_NO_OPTION);
	}
	
	@Override
	public final void initializeViews(final int floorColumns, final int floorRows, final Players defaultPlayer) 
	{
		this.floorColumns = floorColumns;
		this.floorRows = floorRows;
		playerTurnLabel = new JLabel(defaultPlayer + " turn!");

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
	public final void updateCurrentPlayer(final Players newPlayer) 
	{
		getMainPanel().remove(playerTurnLabel);
		playerTurnLabel = new JLabel(newPlayer + " turn!");
		getMainPanel().add(playerTurnLabel);
		mainFrame.pack();
		mainFrame.repaint();
	}
	
	private final BufferedImage getTokenImage(final TokenType token)
	{
		switch (token)
		{
			case RED:
				return redTokenImage;
			case BLACK:
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
	
	private final JPanel getMainPanel()
	{
		return (JPanel) mainFrame.getContentPane().getComponents()[0];
	}

	private final int[] getClickedImageLabel(final JLabel imageLabel)
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

	private final ActionListener exitSubMenuListener = new ActionListener()
	{
		@Override
		public final void actionPerformed(final ActionEvent e) 
		{
			controller.quitTheGame();
		}
	};
	
	private final void loadTokenImages()
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
	
	private final MouseListener tokenImageMouseListener = new MouseListener()
	{
		@Override
		public void mouseClicked(MouseEvent e) 
		{
			int[] clickedImageXY = getClickedImageLabel((JLabel) e.getSource());
			controller.play(clickedImageXY[0], clickedImageXY[1]);
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
}
