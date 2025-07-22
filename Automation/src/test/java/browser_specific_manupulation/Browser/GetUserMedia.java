package browser_specific_manupulation.Browser;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class GetUserMedia {
	WebDriver driver;
	
	@BeforeMethod
	public void SetUp() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--use-fake-ui-for-media-stream");
		options.addArguments("--use-fake-device-for-media-stream");
		driver =new ChromeDriver(options);
		driver.manage().window().maximize();
	}
	
	@AfterMethod
	public void TearDown() {
		driver.quit();
	}
	
	
	@Test
	public void f() throws Exception {
		  driver.get(
	                "https://bonigarcia.dev/selenium-webdriver-java/get-user-media.html");
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	        
	        WebElement startBtn = driver.findElement(By.id("start"));
	        wait.until(ExpectedConditions.elementToBeClickable(startBtn));
	        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", startBtn);
	        Thread.sleep(500);
	        startBtn.click();
	        By videoDevice = By.id("video-device");
	        Pattern nonEmptyString = Pattern.compile(".+");
	        wait.until(ExpectedConditions.textMatches(videoDevice, nonEmptyString));
	        System.out.println(nonEmptyString);
	        assertThat(driver.findElement(videoDevice).getText()).isNotEmpty();
	        
	}
}
