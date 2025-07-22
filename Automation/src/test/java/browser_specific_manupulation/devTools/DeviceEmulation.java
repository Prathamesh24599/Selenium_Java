package browser_specific_manupulation.devTools;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v138.network.Network;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DeviceEmulation {
	WebDriver driver;
	DevTools devTools;

	@BeforeMethod
	public void SetUp() {
		driver = new ChromeDriver();
		devTools = ((ChromeDriver) driver).getDevTools();
		devTools.createSession();
	}

	@AfterMethod
	public void TearDown() {
		devTools.close();
//		driver.close();
	}

	@Test
	public void f() {
		 String userAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) "
	                + "AppleWebKit/600.1.3 (KHTML, like Gecko) "
	                + "Version/8.0 Mobile/12A4345d Safari/600.1.4";
	        devTools.send(Network.setUserAgentOverride(userAgent, Optional.empty(),
	                Optional.empty(), Optional.empty()));

	        // 2. Emulate device dimension
	        Map<String, Object> deviceMetrics = new HashMap<>();
	        deviceMetrics.put("width", 375);
	        deviceMetrics.put("height", 667);
	        deviceMetrics.put("mobile", true);
	        deviceMetrics.put("deviceScaleFactor", 2);
	        ((ChromeDriver) driver).executeCdpCommand(
	                "Emulation.setDeviceMetricsOverride", deviceMetrics);

	        driver.get("https://bonigarcia.dev/selenium-webdriver-java/");
	        assertThat(driver.getTitle()).contains("Selenium WebDriver");
	}
}
