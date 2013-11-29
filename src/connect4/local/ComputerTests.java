package connect4.local;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class ComputerTests
{
	@Test
	public void test()
	{
		Assert.assertEquals(1, 1);
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
	}
}

