package browser_agnostic_feature.Listner;

import org.testng.Assert;
import org.testng.annotations.Test;

public class IReportTest {
	 	@Test
	    public void firstMethod() {
	        Assert.assertTrue(true); // Pass
	    }

	    @Test
	    public void secondMethod() {
	        Assert.assertTrue(false); // Fail
	    }

	    @Test(dependsOnMethods = {"firstMethod"})
	    public void thirdMethod() {
	        Assert.assertTrue(true); // Pass if firstMethod passed
	    }
}
