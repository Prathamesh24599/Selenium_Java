package browser_specific_manupulation.devTools;

import static java.lang.invoke.MethodHandles.lookup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Duration;
import java.util.Optional;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v138.network.Network;
import org.openqa.selenium.devtools.v138.network.model.ConnectionType;
import org.openqa.selenium.devtools.v138.network.model.Headers;
import org.slf4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class EmulateNetworkCondition {
	WebDriver driver;
	DevTools devTools;

	@BeforeMethod
	public void SetUp() {
		driver = new ChromeDriver();
		devTools = ((ChromeDriver) driver).getDevTools();
		devTools.createSession();
	}

	@Test
	public void f() {
		devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty(), java.util.Optional.empty()));
		//We emulate a mobile 3G network with 50 KBps as download and upload bandwidth.
		devTools.send(Network.emulateNetworkConditions(false, 100, 50 * 1024, 50 * 1024,
				Optional.of(ConnectionType.CELLULAR4G), java.util.Optional.empty(), java.util.Optional.empty(),
				java.util.Optional.empty()));
		long initMillis = System.currentTimeMillis();
		driver.get("https://bonigarcia.dev/selenium-webdriver-java/");
		Duration elapsed = Duration.ofMillis(System.currentTimeMillis() - initMillis);
		System.out.println("The page took " + elapsed.toMillis() + " ms to be loaded");
		assertThat(driver.getTitle()).contains("Selenium WebDriver");
	}
}
