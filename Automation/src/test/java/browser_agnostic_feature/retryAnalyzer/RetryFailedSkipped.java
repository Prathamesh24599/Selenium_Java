package browser_agnostic_feature.retryAnalyzer;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryFailedSkipped implements IRetryAnalyzer {

    private int retryCount = 0;
    private final int maxRetry = 3;

    @Override
    public boolean retry(ITestResult result) {
        // ✅ Retry only if FAILED
        if (result.getStatus() == ITestResult.FAILURE && retryCount < maxRetry) {
            retryCount++;
            System.out.println("🔁 Retrying [FAILED] " + result.getName() + " | Attempt: " + (retryCount + 1));
            return true;
        }

        // ❌ Do not retry skipped tests
        return false;
    }
}
