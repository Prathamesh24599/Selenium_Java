package browser_specific_manupulation.devTools;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v138.network.Network;
import org.openqa.selenium.devtools.v138.network.model.BlockedReason;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

public class BlockURL {
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
	public void f() throws Exception {
		devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
		String urlToBlock = "https://bonigarcia.dev/selenium-webdriver-java/img/hands-on-icon.png";
//		String urlToBlock = "https://bonigarcia.dev/selenium-webdriver-java/img/compass.png";
		devTools.send(Network.setBlockedURLs(ImmutableList.of(urlToBlock)));
		devTools.addListener(Network.loadingFailed(), loadingFailed -> {
			BlockedReason reason = loadingFailed.getBlockedReason().get();
			System.out.println("Blocking reason: "+reason);
			assertThat(reason).isEqualTo(BlockedReason.INSPECTOR);
		});
		Thread.sleep(10000);
//		driver.get("https://bonigarcia.dev/selenium-webdriver-java/");
		driver.get("https://bonigarcia.dev/selenium-webdriver-java/loading-images.html");
		assertThat(driver.getTitle()).contains("Selenium WebDriver");
	}
}
