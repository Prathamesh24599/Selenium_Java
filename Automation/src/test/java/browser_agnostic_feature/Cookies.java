package browser_agnostic_feature;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class Cookies {
	WebDriver driver;
	@BeforeMethod
	public void BeforeMethod() {
		System.setProperty("webdriver.chrome.driver", "D:\\chromedriver-win64\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.get("https://bonigarcia.dev/selenium-webdriver-java/");
	}
	
	@AfterMethod
	public void AfterMethod() {
		driver.quit();
	}
	
	@Test
	public void Read_cookies() {
		driver.get("https://bonigarcia.dev/selenium-webdriver-java/cookies.html");
		Options option = driver.manage();
		Cookie username = option.getCookieNamed("username");
		System.out.println(username.getValue());
		System.out.println(username.getDomain());
		System.out.println(username.getName());
		System.out.println(username.getPath());
		System.out.println(username.getSameSite());
		System.out.println(username.getClass());
		System.out.println(username.getExpiry());
		assertThat(username.getValue()).isEqualTo("John Doe");
		assertThat(username.getPath()).isEqualTo("/");
		driver.findElement(By.id("refresh-cookies")).click();

	}
	
	@Test
	public void Add_Cookis() throws Exception {
		driver.get("https://bonigarcia.dev/selenium-webdriver-java/cookies.html");
		Options option = driver.manage();
		Cookie newCookie = new Cookie("New_Cookie", "Prathamesh");
		option.addCookie(newCookie);
		
		Thread.sleep(5000);
		String cookie_text = option.getCookieNamed("New_Cookie").getValue();
		System.out.println(cookie_text);
		driver.findElement(By.id("refresh-cookies")).click();
	}
	
	@Test
	public void edit_Cookie() throws InterruptedException {
		driver.get("https://bonigarcia.dev/selenium-webdriver-java/cookies.html");
		Options option = driver.manage();
		Cookie username = option.getCookieNamed("username");
		Cookie editCookie = new Cookie(username.getName(), "Prathamesh");
		option.addCookie(editCookie);
		
		Thread.sleep(5000);
		String cookie_text = option.getCookieNamed("username").getValue();
		System.out.println(cookie_text);
		driver.findElement(By.id("refresh-cookies")).click();
	}
	
	@Test
	public void delete_Cookie() {
		driver.get("https://bonigarcia.dev/selenium-webdriver-java/cookies.html");
		Options option = driver.manage();
		Set<Cookie> cookie = option.getCookies();
		for (Cookie cookie1 : cookie) {
			System.out.println(cookie1.getName()+"\t"+cookie1.getValue());
		}
		Cookie username = option.getCookieNamed("username");
		option.deleteCookie(username);
		System.out.println("\n"+ "Cookie Deleted"+ "\n");
		cookie = option.getCookies();
		for (Cookie cookie1 : cookie) {
			System.out.println(cookie1.getName()+"\t"+cookie1.getValue());
		}
	}
	
}
