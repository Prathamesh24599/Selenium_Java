package browser_specific_manupulation.devTools;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v138.dom.model.Rect;
import org.openqa.selenium.devtools.v138.page.Page;
import org.openqa.selenium.devtools.v138.page.Page.GetLayoutMetricsResponse;
import org.openqa.selenium.devtools.v138.page.model.Viewport;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FullPageScreenshot {
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
		driver.quit();
	}
	
	@Test
	public void f() throws Exception {
		 driver.get(
	                "https://bonigarcia.dev/selenium-webdriver-java/long-page.html");
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	        wait.until(ExpectedConditions.presenceOfNestedElementsLocatedBy(
	                By.className("container"), By.tagName("p")));

	        GetLayoutMetricsResponse metrics = devTools
	                .send(Page.getLayoutMetrics());
	        Rect contentSize = metrics.getContentSize();
	        String screenshotBase64 = devTools
	                .send(Page.captureScreenshot(Optional.empty(), Optional.empty(),
	                        Optional.of(new Viewport(0, 0, contentSize.getWidth(),
	                                contentSize.getHeight(), 1)),
	                        Optional.empty(), Optional.of(true),
	                        Optional.of(false)));
	        Path destination = Paths.get("fullpage-screenshot-chrome.png");
	        Files.write(destination, Base64.getDecoder().decode(screenshotBase64));

	        assertThat(destination).exists();
	}
}
