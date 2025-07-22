package webDriver_fundamentals;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class Wait {
	WebDriver driver;
	@BeforeMethod
	public void BeforeMethod() {
		System.setProperty("webdriver.chrome.driver", "D:\\chromedriver-win64\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().window().maximize();
			  
		driver.get("https://bonigarcia.dev/selenium-webdriver-java/");
		
	}
	
	@AfterMethod
	public void AfterMethod() {
		driver.quit();
	}
	
	
	@Test
	public void Failed_case() {
		driver.findElement(By.linkText("Loading images")).click();
		
		WebElement landscape = driver.findElement(By.id("landscape"));
		assertThat(landscape.getAttribute("src")).containsIgnoringCase("landscape");
	}
	
	@Test
	public void Implicit_wait() {
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.findElement(By.linkText("Loading images")).click();
		
		WebElement landscape = driver.findElement(By.id("landscape"));
		System.out.println(landscape.getAttribute("alt"));
	}
	
	@Test
	public void Explicit_wait() throws Exception {
		WebDriverWait mywait = new WebDriverWait(driver, Duration.ofSeconds(10));
		
		driver.findElement(By.linkText("Loading images")).click();
		WebElement landscape = mywait.until(ExpectedConditions.visibilityOfElementLocated(By.id("landscape")));
		System.out.println(landscape.getAttribute("alt"));	
		
	}
	
	@Test
	public void Fluent_wait() {
		driver.get(
                "https://bonigarcia.dev/selenium-webdriver-java/loading-images.html");
	    FluentWait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(NoSuchElementException.class);
	    
	    WebElement landscape = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("landscape")));
        assertThat(landscape.getDomProperty("src")).containsIgnoringCase("landscape");
	}
}
