package browser_agnostic_feature.retryAnalyzer;

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

public class Flacky_Case {
	
	
	@Test(retryAnalyzer = Retry_Analyzer.class)
	public void f() {
		int num1 = 5;
        int num2 = 10;
        
		Random random = new Random();
        int randomValue = random.nextBoolean() ? num1 : num2;
        
        
		System.out.println("Flacky Case");
		Assert.assertEquals(randomValue, 5);
	}
	
	private static int counter = 0;

    @Test(retryAnalyzer = Retry_Analyzer.class)
    public void flakyTest() {
        counter++;
        System.out.println("Executing test attempt #" + counter);
        assert counter >= 3 : "Failing intentionally until 3rd attempt";
    }
	
}
