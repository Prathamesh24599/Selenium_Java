package webDriver_fundamentals;

import java.time.Duration;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class Driver_Methods {
	WebDriver driver;
	@BeforeMethod
	public void BeforeMethod() {
		System.setProperty("webdriver.chrome.driver", "D:\\chromedriver-win64\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
			  
		
		
	}
	
	@AfterMethod
	public void AfterMethod() {
		driver.quit();
	}
	
	@Test
	public void GetMethods() {
		driver.get("https://bonigarcia.dev/selenium-webdriver-java/");
		
		String title = driver.getTitle();
		System.out.println(title);
		
		String url = driver.getCurrentUrl();
		System.out.println(url);
		
		
		String window = driver.getWindowHandle();
		System.out.println(window);
		
		Set<String> windows = driver.getWindowHandles();
		System.out.println(windows.size());
		
		String pageSource = driver.getPageSource();
		System.out.println(pageSource);
	}
	
	@Test
	public void Navigate() {
		driver.get("https://bonigarcia.dev/selenium-webdriver-java/");
		driver.navigate().to("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");
		driver.navigate().back();
		driver.navigate().forward();
		driver.navigate().refresh();
		
	}
}
