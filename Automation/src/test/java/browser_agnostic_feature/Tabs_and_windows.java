package browser_agnostic_feature;

import java.time.Duration;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class Tabs_and_windows {
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
	public void Tab() throws Exception {
		 String initPage = "https://bonigarcia.dev/selenium-webdriver-java/";
	     driver.get(initPage);

	     Keys modifier = SystemUtils.IS_OS_MAC ? Keys.COMMAND : Keys.CONTROL;
	     String openInNewTab = Keys.chord(modifier, Keys.RETURN);
	     driver.findElement(By.linkText("Web form")).sendKeys(openInNewTab);
	     Thread.sleep(3000);

	     Set<String> windowHandles = driver.getWindowHandles();
	     System.out.println(windowHandles.size());
	     for (String windowHandle : windowHandles) {
	    	 if (driver.getWindowHandle().equals(windowHandle)) {
	    		 System.out.println("Current window handle {} "+ windowHandle);
	         } else {
	        	 System.out.println("Current window handle {} "+ windowHandle);
	             driver.switchTo().window(windowHandle);
	             Thread.sleep(3000);
	             WebElement textbox = driver.findElement(By.id("my-text-id"));
	     		 textbox.sendKeys("Prathamesh");
	         }
	     }
	}
	
	@Test
	public void newTab() {
		driver.get("https://bonigarcia.dev/selenium-webdriver-java/");
		  String initHandle = driver.getWindowHandle();

	        driver.switchTo().newWindow(WindowType.TAB);
	        driver.get(
	                "https://bonigarcia.dev/selenium-webdriver-java/web-form.html");
	        assertThat(driver.getWindowHandles().size()).isEqualTo(2);

	        driver.switchTo().window(initHandle);
	        driver.close();
	        assertThat(driver.getWindowHandles().size()).isEqualTo(1);
	}
	
	
	@Test
	public void new_window() throws Exception {
		driver.get("https://bonigarcia.dev/selenium-webdriver-java/");
		 String initHandle = driver.getWindowHandle();
		 driver.switchTo().newWindow(WindowType.WINDOW);
		 driver.get("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");
		 Set<String> windowHandles = driver.getWindowHandles();
	     System.out.println(windowHandles.size());
		 Thread.sleep(3000);
		 WebElement textbox = driver.findElement(By.id("my-text-id"));
 		 textbox.sendKeys("Prathamesh");
 		Thread.sleep(3000);
 		driver.switchTo().window(initHandle);
		driver.findElement(By.linkText("Drag and drop")).click();
		Thread.sleep(3000);
	}
}
