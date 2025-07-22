package webDriver_fundamentals;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class WebElement_Methods {
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
	public void Locator() {
		WebElement aa = driver.findElement(By.xpath("//div[@class='card'][2]"));
		aa.findElement(By.linkText("Web form"));
	}
	
	@Test
	public void basicMethods() {
		driver.findElement(By.linkText("Web form")).click();
		driver.findElement(By.id("my-text-id")).sendKeys("Prathamesh");
		driver.findElement(By.id("my-text-id")).clear();
		driver.findElement(By.xpath("//button[normalize-space()='Submit']")).submit();
	}
	
	@Test
	public void getMethods() {
		String txt = driver.findElement(By.linkText("Web form")).getText();
		System.out.println("text "+txt);
		
		String atrValue = driver.findElement(By.linkText("Web form")).getAttribute("href");
		System.out.println("atrValue"+atrValue);
		
		String domValue = driver.findElement(By.linkText("Web form")).getDomAttribute("href");
		System.out.println("domValue "+domValue);
		
		String domPropValue = driver.findElement(By.linkText("Web form")).getDomAttribute("href");
		System.out.println("domPropValue "+domPropValue);
		
		String cssValue = driver.findElement(By.linkText("Web form")).getCssValue("background-color");
		System.out.println("cssValue "+cssValue);
		
		String tagName = driver.findElement(By.linkText("Web form")).getTagName();
		System.out.println("tagName"+tagName);
		
		Dimension size = driver.findElement(By.linkText("Web form")).getSize();
		System.out.println("Size "+size.width +"  x  "+ size.height);
		
		Point location = driver.findElement(By.linkText("Web form")).getLocation();
		System.out.println("Location "+"("+location.x +","+location.y+")");
		
		Rectangle rect = driver.findElement(By.linkText("Web form")).getRect();
		System.out.println("Rect "+"("+rect.x +","+rect.y+")");
		
		String ariaRole = driver.findElement(By.linkText("Web form")).getAriaRole();
		System.out.println("ariaRole "+ariaRole);
		
		String accessibleName = driver.findElement(By.linkText("Web form")).getAccessibleName();
		System.out.println("accessibleName "+accessibleName);
	}
	
	@Test
	void testByHtmlAttributes() {
	 driver.get(
	 "https://bonigarcia.dev/selenium-webdriver-java/web-form.html");
	 // By name
	 WebElement textByName = driver.findElement(By.name("my-text"));
	 assertThat(textByName.isEnabled()).isTrue();
	 // By id
	 WebElement textById = driver.findElement(By.id("my-text-id"));
	 assertThat(textById.getAttribute("type")).isEqualTo("text");
	 assertThat(textById.getDomAttribute("type")).isEqualTo("text");
	 assertThat(textById.getDomProperty("type")).isEqualTo("text");
	 assertThat(textById.getAttribute("myprop")).isEqualTo("myvalue");
	 assertThat(textById.getDomAttribute("myprop")).isEqualTo("myvalue");
	 assertThat(textById.getDomProperty("myprop")).isNull();
	 // By class name
	 List<WebElement> byClassName = driver
	 .findElements(By.className("form-control"));
	 assertThat(byClassName.size()).isPositive();
	 assertThat(byClassName.get(0).getAttribute("name")).isEqualTo("my-text");
	}
}
