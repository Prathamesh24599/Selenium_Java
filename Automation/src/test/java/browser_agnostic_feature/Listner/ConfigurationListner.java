package browser_agnostic_feature.Listner;

import org.testng.IConfigurationListener;
import org.testng.ITestResult;

public class ConfigurationListner implements IConfigurationListener {

    @Override
    public void onConfigurationSuccess(ITestResult itr) {
        System.out.println("✅ Config method passed: " + itr.getMethod().getMethodName());
    }

    @Override
    public void onConfigurationFailure(ITestResult itr) {
        System.out.println("❌ Config method failed: " + itr.getMethod().getMethodName());
        System.out.println("   Reason: " + itr.getThrowable());
    }

    @Override
    public void onConfigurationSkip(ITestResult itr) {
        System.out.println("⚠️ Config method skipped: " + itr.getMethod().getMethodName());
    }
}
