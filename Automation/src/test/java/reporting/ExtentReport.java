package reporting;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import io.github.bonigarcia.wdm.WebDriverManager;

public class ExtentReport {
	 WebDriver driver;

	    ExtentReports reports;

	    @BeforeClass
	    public void setupClass() {
	        reports = new ExtentReports();
	        ExtentSparkReporter htmlReporter = new ExtentSparkReporter(
	                "extentReport.html");
	        reports.attachReporter(htmlReporter);
	    }

	    @BeforeMethod
	    public void setup(Method method) {
	        reports.createTest(method.getName());

	        driver = WebDriverManager.chromedriver().create();
	    }

	    @AfterMethod
	    public void teardown() {
	        driver.quit();
	    }

	    @AfterClass
	    public void teardownClass() {
	        reports.flush();
	    }

	    @Test
	    public void testReporting1() {
	        checkIndex(driver);
	    }

	    @Test
	    public void testReporting2() {
	        checkIndex(driver);
	    }

	    void checkIndex(WebDriver driver) {
	        driver.get("https://bonigarcia.dev/selenium-webdriver-java/");
	        assertThat(driver.getTitle()).contains("Selenium WebDriver");
	    }
}
