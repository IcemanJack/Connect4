package connect4.local;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class ComputerTests
{
	static Controller game1 = new Controller(7, 6, 4);
	//static Controller game2 = new Controller(7, 6, 4);
	
	@Test
	public void checkIfComputerPlayWhen3CaseTypePlayer1()
	{
		//NO WINNING CASE
		game1.play(0, 1); //RED
		Assert.assertFalse(game1.modelAtPositionIsAvailable(1, 4)); //BLACK
		game1.play(0, 2); //RED
		Assert.assertFalse(game1.modelAtPositionIsAvailable(2, 4)); //BLACK
		
		//LEFT
		game1.play(0, 3); //RED
		Assert.assertFalse(game1.modelAtPositionIsAvailable(0, 5)); //BLACK
		
		//RIGHT
		game1.play(0, 3); //RED
		Assert.assertFalse(game1.modelAtPositionIsAvailable(4, 5)); //BLACK

		//UP
		game1.play(0, 3); //RED
		Assert.assertFalse(game1.modelAtPositionIsAvailable(3, 2)); //BLACK
		
//		//DIAGONAL
//		x.play(0, 4); //RED
//		Assert.assertFalse(x.modelAtPositionIsAvailable(3, 2)); //BLACK
	}

//	@Test
//	public void checkIfComputerPlayWhen3TokenComputer()
//	{
		
//	}

	public static void main(String[] args)
	{
		Result result = JUnitCore.runClasses(ComputerTests.class);
		System.out.println(result.getRunCount() + " tests executed.");
		Collection<Failure> failures = result.getFailures();
		System.out.println(failures.size() + " tests failed:");
		
		for (Failure failure : failures)
		{
			System.out.println("\t" + failure.toString() + "\n\n");	
		}
		
		//threadSleep(1000);
		
		//game1.quitTheGame();
	}
	
	@SuppressWarnings("unused")
	private static void threadSleep(int milliseconds)
	{
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}

