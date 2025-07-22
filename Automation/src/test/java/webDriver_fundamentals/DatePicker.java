package webDriver_fundamentals;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.locators.RelativeLocator;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DatePicker {
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
//		driver.quit();
	}
	
	@Test
	public void f() throws Exception {
		driver.get("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");
				 
		 WebElement dateInput = driver.findElement(By.name("my-date"));
		    dateInput.click();
		    Thread.sleep(1000); // Wait for calendar to appear

		    // Step 2: Navigate to the desired month
		    while (true) {
		        WebElement monthLabel = driver.findElement(By.className("datepicker-switch"));
		        String currentMonthYear = monthLabel.getText();  // Example: "May 2025"
		        
		        if (currentMonthYear.equals("July 2025")) {
		            break;
		        } else {
		            WebElement nextBtn = driver.findElement(By.className("next"));
		            nextBtn.click();
		            Thread.sleep(500);
		        }
		    }

		    // Step 3: Click the day: 15
		    List<WebElement> days = driver.findElements(By.className("day"));
		    for (WebElement day : days) {
		        if (day.getText().equals("15") && day.isDisplayed()) {
		            day.click();
		            break;
		        }
		    }
	}
	
	@Test
	public void dropDown_date() throws Exception {
		driver.get("https://testautomationpractice.blogspot.com/#");
		driver.findElement(By.id("txtDate")).click();
		WebElement monthDD = driver.findElement(By.xpath("//*[@class='ui-datepicker-month']"));
		Select dd1 = new Select(monthDD); 
		dd1.selectByVisibleText("May");
		
		WebElement yearDD = driver.findElement(By.xpath("//*[@class='ui-datepicker-year']"));
		Select dd2 = new Select(yearDD); 
		dd2.selectByVisibleText("2024");
		int cur_date = 23;
		driver.findElement(By.xpath("(//*[@class='ui-state-default'])["+cur_date+"]")).click();
		Thread.sleep(3000);
	}
	
	@Test
	public void Date_element() {
		driver.get("https://testautomationpractice.blogspot.com/#");
		driver.findElement(By.id("datepicker")).click();
		
		
	}
}
