package pom.test;

import org.testng.annotations.Test;

import pom.pages.Home_Page;
import pom.pages.Login_Page;
import pom.utils.Assertions;

import org.testng.annotations.BeforeMethod;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;

public class LoginTest {
	WebDriver driver;
	@BeforeMethod
	public void beforeMethod() {
		driver = new ChromeDriver();
		driver.get("https://bonigarcia.dev/selenium-webdriver-java/login-form.html");
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
	}

	@AfterMethod
	public void afterMethod() {
		if(driver != null) {
			driver.quit();
		}
	}
	
	@Test
	public void PassedLogin() {
		Login_Page Lp = new Login_Page(driver);
		Home_Page Hp =Lp.Login("user", "user");
		Assertions.verify_object_equals(Hp.getSuccessMessage(), "Login successful", "Login successful");
	}
	
	@Test
	public void FailedLogin() {
		Login_Page Lp = new Login_Page(driver);
		Lp.Login("User", "user1");
		Assertions.verify_object_equals(Lp.getErrorMessage(), "Invalid credentials", "Invalid credentials");
	}
}
