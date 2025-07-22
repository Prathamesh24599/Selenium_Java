package browser_specific_manupulation.Browser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class IE_In_Edge {
	WebDriver driver;

	@BeforeMethod
	public void SetUp() {
//		 System.setProperty("webdriver.ie.driver", "C:\\WebDriver\\IEDriverServer.exe");

		    InternetExplorerOptions options = new InternetExplorerOptions();
		    options.attachToEdgeChrome();
		    options.withEdgeExecutablePath("C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe");

		    // Recommended options
		    options.ignoreZoomSettings();
		    options.introduceFlakinessByIgnoringSecurityDomains();
		    options.requireWindowFocus();

		    // Launch Edge in IE Mode
		    driver = new InternetExplorerDriver(options);
	}

	@Test
	public void testIEmodeEdge() {
		driver.get("https://bonigarcia.dev/selenium-webdriver-java/");
		String title = driver.getTitle();
		System.out.println("Title in IE mode: " + title);
		assertThat(title).contains("Selenium WebDriver");
	}
}
