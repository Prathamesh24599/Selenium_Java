package browser_agnostic_feature.Listner;

import java.util.HashSet;
import java.util.Set;

import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.ITestResult;

public class HookableListner implements IHookable{
	
	private static final Set<String> blockedMethods = new HashSet<>(Set.of(
	        "blockedTest", "skipThis", "notNow"
	    ));
	
	@Override
	public void run(IHookCallBack callBack, ITestResult testResult) {
		String testName = testResult.getMethod().getMethodName();

        System.out.println("🔹 Hooked into: " + testName);

        if (blockedMethods.contains(testName)) {
            System.out.println("⛔ Skipping execution of: " + testName);
            testResult.setStatus(ITestResult.SKIP);
            return;
        }

        // ✅ Allow the test to run
        callBack.runTestMethod(testResult);
        System.out.println("✅ Executed: " + testName);
	}
}
