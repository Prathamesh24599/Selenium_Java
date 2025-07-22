package browser_agnostic_feature.Listner;

import org.testng.annotations.Test;

public class IHookableTest {
	  @Test
	    public void allowedTest() {
	        System.out.println("✅ This test should run.");
	    }

	    @Test
	    public void blockedTest() {
	        System.out.println("❌ This should be skipped.");
	    }

	    @Test
	    public void skipThis() {
	        System.out.println("❌ This should also be skipped.");
	    }

	    @Test
	    public void notNow() {
	        System.out.println("❌ This should be skipped too.");
	    }
}
