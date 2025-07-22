package browser_agnostic_feature.Listner;

import org.testng.ISuite;
import org.testng.ISuiteListener;

public class SuiteListner implements ISuiteListener {
	@Override
    public void onStart(ISuite suite) {
        System.out.println("✅ Suite STARTED: " + suite.getName());
    }

    @Override
    public void onFinish(ISuite suite) {
        System.out.println("🛑 Suite FINISHED: " + suite.getName());
    }
}
