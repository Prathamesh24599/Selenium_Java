package browser_agnostic_feature.Listner;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

import net.bytebuddy.asm.Advice.Return;

public class InvokeMethodListner implements IInvokedMethodListener{
	@Override
	public void beforeInvocation(IInvokedMethod method, ITestResult result) {
        System.out.println("----- BEFORE INVOCATION -----");

        System.out.println("Method Name: " + method.getTestMethod().getMethodName());
        System.out.println("Is Test Method? " + method.isTestMethod());
        System.out.println("Is Configuration Method? " + method.isConfigurationMethod());
        System.out.println("Execution Date (ms): " + method.getDate());

        System.out.println("-----------------------------");
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult result) {
        System.out.println("----- AFTER INVOCATION ------");

        System.out.println("Method Name: " + method.getTestMethod().getMethodName());
        System.out.println("Test Status: " 
//        +
//        		switch (result.getStatus()) {
//                case ITestResult.SUCCESS:
//                	return "PASS";
//                case ITestResult.FAILURE:
//                	return "FAIL";
//                case ITestResult.SKIP:
//                	return "SKIP";
//                default:
//                	return "UNKNOWN";
//            }
        		);

        if (result.getThrowable() != null) {
            System.out.println("Error: " + result.getThrowable().getMessage());
        }

        System.out.println("-----------------------------");
    }
}
