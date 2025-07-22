package browser_specific_manupulation.Browser;

import java.util.logging.Level;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;
import org.testng.Assert;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.slf4j.Logger;

public class Logging {
	WebDriver driver;
	static final Logger log = getLogger(lookup().lookupClass());
	
	@BeforeMethod
	public void Setup() {
		LoggingPreferences logs = new LoggingPreferences();
		logs.enable(LogType.BROWSER, Level.ALL);
		ChromeOptions options = new ChromeOptions();
		options.setCapability("goog:loggingPrefs", logs);
		driver = new ChromeDriver(options);
	}
	
	@AfterMethod
	public void TearDown() {
		driver.quit();
	}
	
	@Test
	public void f() {
		driver.get(
				"https://bonigarcia.dev/selenium-webdriver-java/console-logs.html");
//		driver.get("https://www.google.co.in");
		LogEntries browserLogs = driver.manage().logs().get(LogType.BROWSER);
        Assertions.assertThat(browserLogs.getAll()).isNotEmpty();
        browserLogs.forEach(l -> log.debug("{}", l));
	}
}
