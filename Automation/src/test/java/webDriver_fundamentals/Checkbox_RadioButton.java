package webDriver_fundamentals;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;


public class Checkbox_RadioButton {
	WebDriver driver;
	@BeforeMethod
	public void BeforeMethod() {
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\Prathamesh\\OneDrive\\Desktop\\Selenium\\Java\\chromedriver-win64");
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
			  
		driver.get("https://testautomationpractice.blogspot.com/");
		
	}
	
	
	@Test
  	public void sigle_checkbox() {

		  //for selecting one checkbox
		driver.findElement(By.xpath("//input[@id='sunday']")).click();

  }
	
	@Test
	public void multi_checkbox() {
		List<WebElement> elements = driver.findElements(By.xpath("//input[@class='form-check-input' and @type= 'checkbox']"));
		for (WebElement webElement : elements) {
			webElement.click();
		}
	}
	
	@Test
	public void isSelected_method() {
		WebElement checkbox = driver.findElement(By.xpath("//input[@id='sunday']"));
		Boolean status = checkbox.isSelected();
		if(status==false) {
			checkbox.click();
			System.out.println(checkbox.isSelected());
		}
	}
	
	@Test
	public void isEnabled_method() {
		WebElement checkbox = driver.findElement(By.xpath("//input[@id='sunday']"));
		Boolean status = checkbox.isEnabled();
		if(status==true) {
			checkbox.click();
			System.out.println(checkbox.isEnabled());
		}
	}
	
	@Test
	public void isDisplayed_method() {
		WebElement checkbox = driver.findElement(By.xpath("//input[@id='sunday']"));
		Boolean status = checkbox.isDisplayed();
		if(status==true) {
			checkbox.click();
			System.out.println(checkbox.isDisplayed());
		}
	}
	
	@Test
	public void Radio_button() {
		
		WebElement radio_button = driver.findElement(By.xpath("//input[@id='male']"));
		Boolean status = radio_button.isSelected();
		if(status = false) {
			radio_button.click();
			System.out.println(radio_button.isSelected());
		}
	}
	
	
	@AfterMethod
	public void AfterMethod() {
		driver.quit();
	}
}
