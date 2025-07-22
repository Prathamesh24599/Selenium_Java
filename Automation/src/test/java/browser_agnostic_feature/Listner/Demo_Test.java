package browser_agnostic_feature.Listner;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class Demo_Test {
	
	 @BeforeMethod
	    public void beforeMethod() {
	        System.out.println("🔧 Setting up before test method");
	        Assert.fail();
	    }

	    @Test
	    public void myTest() {
	        System.out.println("🚀 Running test");
	    }

	    @AfterMethod
	    public void afterMethod() {
	        System.out.println("🧹 Cleaning up after test method");
	    }
}
