package connect4.local;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class ComputerTests
{
	static Controller x = new Controller(7, 6, 4);
	
	@Test
	public void checkIfComputerPlayWhen3TokenPlayer1()
	{
		// UP
		x.play(0, 0); //RED
		x.play(0, 1); //BLACK
		x.play(0, 0); //RED
		x.play(0, 2); //BLACK
		x.play(0, 0); //RED
		Assert.assertFalse(x.modelAtPositionIsAvailable(0, 2)); //BLACK
		
		// RIGHT
		x.play(0, 1); //RED
		x.play(0, 3); //BLACK
		x.play(0, 2); //RED
		Assert.assertFalse(x.modelAtPositionIsAvailable(3, 4)); //BLACK
		
		// LEFT
		x.play(0, 4); //RED
		x.play(0, 4); //BLACK
		x.play(0, 4); //RED
		x.play(0, 5); //BLACK
		x.play(0, 3); //RED
		x.play(0, 5); //BLACK
		x.play(0, 2); //RED
		Assert.assertFalse(x.modelAtPositionIsAvailable(1, 3)); //BLACK
		
		//DIAGONAL
		x.play(0, 4); //RED
		Assert.assertFalse(x.modelAtPositionIsAvailable(3, 2)); //BLACK
	}

	@Test
	public void checkIfComputerPlayWhen3TokenComputer()
	{
//		// UP
//		x.play(0, 1); //RED
//		x.play(0, 0); //BLACK
//		x.play(0, 1); //RED
//		x.play(0, 0); //BLACK
//		x.play(0, 2); //RED
//		x.play(0, 0); //BLACK
//		x.play(0, 2); //RED
//		Assert.assertFalse(x.modelAtPositionIsAvailable(0, 2)); //BLACK
//		
//		// RIGHT
//		x.play(0, 1); //RED
//		x.play(0, 3); //BLACK
//		x.play(0, 2); //RED
//		Assert.assertFalse(x.modelAtPositionIsAvailable(3, 4)); //BLACK
//		
//		// LEFT
//		x.play(0, 4); //RED
//		x.play(0, 4); //BLACK
//		x.play(0, 4); //RED
//		x.play(0, 5); //BLACK
//		x.play(0, 3); //RED
//		x.play(0, 5); //BLACK
//		x.play(0, 2); //RED
//		Assert.assertFalse(x.modelAtPositionIsAvailable(1, 3)); //BLACK
	}
	
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
		x.quitTheGame();
	}
}

