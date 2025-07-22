package webDriver_fundamentals;

import java.time.Duration;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class Alerts {
	WebDriver driver;
	@BeforeMethod
	public void BeforeMethod() {
		System.setProperty("webdriver.chrome.driver", "D:\\chromedriver-win64\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
			  
		driver.get("https://testautomationpractice.blogspot.com/");
		
	}
	
	@AfterMethod
	public void AfterMethod() {
		driver.quit();
	}
	@Test
	public void simple_alert() throws InterruptedException {
		driver.findElement(By.id("alertBtn")).click();
		driver.switchTo().alert().accept();
		System.out.println();
	}
	
	@Test
	public void confirmation_alert() throws InterruptedException {
		driver.findElement(By.id("confirmBtn")).click();
		Thread.sleep(5000);
		Alert test_alert = driver.switchTo().alert();
		System.out.println(test_alert.getText());
		test_alert.accept();
	}
	
	@Test
	public void promt_alert() throws InterruptedException {
		driver.findElement(By.id("promptBtn")).click();
		Alert test_alert = driver.switchTo().alert();
		System.out.println(test_alert.getText());
		test_alert.sendKeys("Prathamesh");
		Thread.sleep(5000);
		test_alert.accept();
		System.out.println(driver.findElement(By.id("demo")).getText());
	}
	
}
