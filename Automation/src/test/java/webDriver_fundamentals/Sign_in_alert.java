package webDriver_fundamentals;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.HasAuthentication;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.UsernameAndPassword;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Sign_in_alert {
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
	  public void f() throws InterruptedException {
		  driver.navigate().to("https://the-internet.herokuapp.com/basic_auth");
		  Thread.sleep(5000);
		  driver.navigate().to("https://admin:admin@the-internet.herokuapp.com/basic_auth");
		  Thread.sleep(5000);
		  
//		  Proxy proxy = new Proxy();
//			String proxyStr = "38.154.227.167:5868";
//			proxy.setHttpProxy(proxyStr);
//			proxy.setSslProxy(proxyStr);
//			ChromeOptions options = new ChromeOptions();
//			options.setAcceptInsecureCerts(true);
//			options.setProxy(proxy);
//			driver = WebDriverManager.chromedriver().capabilities(options).create();
//			((HasAuthentication) driver)
//	        .register(() -> new UsernameAndPassword("lgzlswko", "m9s7add53nqz"));
//			driver.get("https://whatismyipaddress.com/");
//			Thread.sleep(15000);
	  }
	  @Test
	  public void test1() throws Exception {
		  driver.navigate().to("https://the-internet.herokuapp.com");
		  driver.findElement(By.linkText("File Upload")).click();
		  driver.findElement(By.id("file-upload")).sendKeys("D:\\chromedriver-win64\\chromedriver.exe");
		  Thread.sleep(5000);
	  }
	  
	  
	  
}
