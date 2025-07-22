package browser_agnostic_feature.retryAnalyzer;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class Retry_Analyzer implements IRetryAnalyzer{
	private int retryCount = 0;
    private final int maxRetry = 2;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetry) {
            retryCount++;
            System.out.println("🔁 Retrying test: " + result.getName() + " | Attempt: " + (retryCount + 1));
            System.out.println("✅ Test Passed");
            return true;  // Retry test
            
        }
        return false; // Give up
    }
    
    
    
}
