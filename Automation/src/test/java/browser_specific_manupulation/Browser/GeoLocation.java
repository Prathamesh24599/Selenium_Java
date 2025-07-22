package browser_specific_manupulation.Browser;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class GeoLocation {
	 WebDriver driver;

	    @BeforeMethod
	    public void setup() {
	        ChromeOptions options = new ChromeOptions();
	        Map<String, Object> prefs = new HashMap<>();
	        prefs.put("profile.default_content_setting_values.geolocation", 1);
	        options.setExperimentalOption("prefs", prefs);

	        driver = new ChromeDriver(options);
	    }
	
	    @AfterMethod
	    public void teardown() throws InterruptedException {
	        // FIXME: pause for manual browser inspection
	        Thread.sleep(Duration.ofSeconds(3).toMillis());

	        driver.quit();
	    }

	    @Test
	    public void testGeolocation() {
	        driver.get(
	                "https://bonigarcia.dev/selenium-webdriver-java/geolocation.html");
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

	        driver.findElement(By.id("get-coordinates")).click();
	        WebElement coordinates = driver.findElement(By.id("coordinates"));
	        wait.until(ExpectedConditions.visibilityOf(coordinates));
	    }

}
