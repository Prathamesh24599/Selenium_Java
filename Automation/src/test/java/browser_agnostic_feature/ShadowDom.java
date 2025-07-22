package browser_agnostic_feature;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ShadowDom {
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
	public void failed() {
		driver.findElement(By.linkText("Shadow DOM")).click();
//		System.out.println(driver.findElement(By.xpath("/html/body/main/div/div[4]/div//p")).getText());
		
		SearchContext shadow0 = driver.findElement(By.cssSelector("#content")).getShadowRoot();
		String ShadowText = shadow0.findElement(By.cssSelector("p")).getText();
		System.out.println(ShadowText);
	}
	
	@Test
	public void nested_dom() {
		driver.get("https://dev.automationtesting.in/shadow-dom");
		SearchContext shadow0 = driver.findElement(By.cssSelector("#shadow-root")).getShadowRoot();
		String ShadowText = shadow0.findElement(By.cssSelector("#shadow-element")).getText();
		System.out.println(ShadowText);
		
	}
	
	@Test
	public void inner_dom() {
		driver.get("https://dev.automationtesting.in/shadow-dom");
		SearchContext shadow1 = driver.findElement(By.cssSelector("#shadow-root")).getShadowRoot();
		SearchContext shadow2 = shadow1.findElement(By.cssSelector("#inner-shadow-dom")).getShadowRoot();
		String ShadowText1 = shadow2.findElement(By.cssSelector("#nested-shadow-root")).getText();
		System.out.println(ShadowText1);
	}	
	
	@Test
	public void nested_dom1() {
		driver.get("https://dev.automationtesting.in/shadow-dom");
		SearchContext shadow3 = driver.findElement(By.cssSelector("#shadow-root")).getShadowRoot();
		SearchContext shadow4 = shadow3.findElement(By.cssSelector("#inner-shadow-dom")).getShadowRoot();
		SearchContext shadow5 = shadow4.findElement(By.cssSelector("#nested-shadow-dom")).getShadowRoot();
		String ShadowText2 = shadow5.findElement(By.cssSelector("#multi-nested-shadow-element")).getText();
		System.out.println(ShadowText2);
	}
	
	
}
