package browser_agnostic_feature.Listner;

import java.util.List;
import java.util.Map;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.xml.XmlSuite;

public class ReporterListner implements IReporter {
	
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        System.out.println("\n======== CUSTOM TESTNG REPORT ========");

        for (ISuite suite : suites) {
            String suiteName = suite.getName();
            System.out.println("SUITE: " + suiteName);

            Map<String, ISuiteResult> suiteResults = suite.getResults();

            for (ISuiteResult result : suiteResults.values()) {
                ITestContext context = result.getTestContext();

                int passed = context.getPassedTests().getAllResults().size();
                int failed = context.getFailedTests().getAllResults().size();
                int skipped = context.getSkippedTests().getAllResults().size();

                System.out.println("  Test: " + context.getName());
                System.out.println("    PASSED:  " + passed);
                System.out.println("    FAILED:  " + failed);
                System.out.println("    SKIPPED: " + skipped);
            }
        }

        System.out.println("Report generated in: " + outputDirectory);
        System.out.println("======================================\n");
    }
}
