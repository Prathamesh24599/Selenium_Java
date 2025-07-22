package webDriver_fundamentals;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class Frames {
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
	public void f() {
		driver.get("https://bonigarcia.dev/selenium-webdriver-java/frames.html");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		 String frameName = "frame-body";
		 wait.until(ExpectedConditions.presenceOfElementLocated(By.name(frameName)));
		 driver.switchTo().frame(frameName);
		 By pName = By.tagName("p");
		 wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(pName, 0));
		 List<WebElement> paragraphs = driver.findElements(pName);
//		 assertThat(paragraphs).hasSize(20);
		 for (WebElement webElement : paragraphs) {
			System.out.println(webElement.getText());
		}
	}	 
	@Test
	public void new_Frame() {
		//navigate to url
        driver.get("https://demoqa.com/frames");
        
        //Switch to Frame using Index
        driver.switchTo().frame(driver.findElement(By.id("frame1")));
        
        //Identifying the heading in webelement
        WebElement frame1Heading= driver.findElement(By.id("sampleHeading"));
        
        //Finding the text of the heading
        String frame1Text=frame1Heading.getText();
        
        //Print the heading text
        System.out.println(frame1Text);
        
        driver.switchTo().parentFrame();
        
        System.out.println(driver.findElement(By.xpath("//*[@id='framesWrapper']/h1")).getText());
        driver.switchTo().frame(driver.findElement(By.id("frame2")));
        
        WebElement frame2Heading= driver.findElement(By.id("sampleHeading"));
        
      //Finding the text of the heading
        String frame2Text=frame2Heading.getText();
        
        //Print the heading text
        System.out.println(frame2Text);
	}
}
