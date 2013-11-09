package connect4;

import java.awt.image.BufferedImage;

public interface ModelUpdateListenerI 
{
	void initializeViews(int columns, int rows, BufferedImage defaultImage, Players defaultPlayer);
	void updateCurrentPlayer(Players newPlayer);
}
