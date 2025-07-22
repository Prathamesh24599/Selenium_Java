package browser_agnostic_feature.Listner;

import org.testng.annotations.*;

public class IInvokeMethodTest {

    @BeforeClass
    public void beforeClass() {
        System.out.println("✅ BeforeClass");
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

    @AfterClass
    public void afterClass() {
        System.out.println("✅ AfterClass");
    }
}
