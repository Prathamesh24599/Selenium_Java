package webDriver_fundamentals;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SSL_Certificate {
	WebDriver driver;
	@BeforeMethod
	public void BeforeMethod() {
		System.setProperty("webdriver.chrome.driver", "D:\\chromedriver-win64\\chromedriver.exe");
		
	}
	
	@AfterMethod
	public void AfterMethod() {
		
	}
	
	@Test
	public void f() {
		//Create instance of ChromeOptions Class
				ChromeOptions handlingSSL = new ChromeOptions();

				//Using the accept insecure cert method with true as parameter to accept the untrusted certificate
				handlingSSL.setAcceptInsecureCerts(true);
						
				//Creating instance of Chrome driver by passing reference of ChromeOptions object
				WebDriver driver = new ChromeDriver(handlingSSL);
				
				//Launching the URL
				driver.get("https://expired.badssl.com/");
				System.out.println("The page title is : " +driver.getTitle());
				driver.quit();
	}
}
