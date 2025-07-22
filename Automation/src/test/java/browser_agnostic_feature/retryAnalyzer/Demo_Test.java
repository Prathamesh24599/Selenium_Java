package browser_agnostic_feature.retryAnalyzer;

import org.testng.annotations.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Demo_Test {

    static {
        Logger.getLogger("org.testng.internal.Utils").setLevel(Level.SEVERE);
    }

    @Test
    public void testPass() {
        System.out.println("✅ Test - testPass");
    }

    @Test
    public void testFail() {
        System.out.println("❌ Test - testFail");
        assert false : "Intentional failure!";
    }

    @Test(dependsOnMethods = "testFail")
    public void skipTest() {
        System.out.println("⚠️ Skipped Test due to dependency");
    }
}
