package browser_specific_manupulation.devTools;

import java.time.Duration;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v138.emulation.Emulation;
import org.openqa.selenium.html5.Location;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LocationContext {
	WebDriver driver;
    DevTools devTools;
	
	@BeforeMethod
	public void SetUp() {
		 ChromeOptions options = new ChromeOptions();
	        options.addArguments("--use-fake-ui-for-media-stream");
	        driver = new ChromeDriver(options);

	        devTools = ((ChromeDriver) driver).getDevTools();
	        devTools.createSession();
	        
	        // Set location using DevTools CDP command
	        devTools.send(Emulation.setGeolocationOverride(
	                Optional.of(27.5916),   // latitude
	                Optional.of(86.5640),   // longitude
	                Optional.of(8850.0)     // accuracy in meters
, java.util.Optional.empty(), java.util.Optional.empty(), java.util.Optional.empty(), java.util.Optional.empty()
	        ));
	}
	
	@AfterMethod
	public void TearDown() {
		devTools.close();
		driver.close();
	}
	
  @Test
  public void f() {
	  driver.get("https://bonigarcia.dev/selenium-webdriver-java/geolocation.html");

      driver.findElement(By.id("get-coordinates")).click();

      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
      WebElement coordinates = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("coordinates")));

      System.out.println("📍 Coordinates: " + coordinates.getText());
  

  }

  
}
