package webDriver_fundamentals;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.module.ModuleDescriptor.Builder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;



public class Dropdowns {
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
	public void Select_class() throws InterruptedException {
		driver.findElement(By.linkText("Web form")).click();
		WebElement ddElement = driver.findElement(By.name("my-select"));
		Select dd = new Select(ddElement);
		dd.selectByVisibleText("One");
		Thread.sleep(3000);
		List<WebElement> options = dd.getOptions();
		for (WebElement webElement : options) {
			System.out.println(webElement.getText());
		}
	}
	
	@Test
	public void Search_DropDown() throws Exception {
		WebDriverWait mywait = new WebDriverWait(driver, Duration.ofSeconds(10));
		driver.get("https://www.google.co.in");
		driver.findElement(By.id("APjFqb")).sendKeys("Fac");
//		Thread.sleep(3000);
		WebElement facebook = mywait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@class='wM6W7d'][1]/span")));
		facebook.click();
		Thread.sleep(3000);
	}
	
	@Test
	void testDatalist() throws Exception {
		driver.get("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");
		WebElement datalist = driver.findElement(By.name("my-datalist"));
		datalist.click();
		WebElement option = driver.findElement(By.xpath("//datalist/option[2]"));
		String optionValue = option.getAttribute("value");
		datalist.sendKeys(optionValue);
		Thread.sleep(3000);
		}
	
	@Test
	public void Multi_Select() throws Exception {
		driver.get("https://demoqa.com/select-menu");
		Actions actions = new Actions(driver);
		Thread.sleep(3000);
		WebElement dropdown = driver.findElement(By.xpath("//div[@id='withOptGroup']"));
		actions.scrollToElement(dropdown);
		dropdown.click();
		

		// 2. Wait for options to appear and click desired option
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		WebElement option = wait.until(ExpectedConditions
		    .visibilityOfElementLocated(By.xpath("//div[contains(text(),'Group 1, option 2')]")));
		
		
		option.click();
		Robot robot = new Robot();
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
		ImageIO.write(screenFullImage, "png", new File("screenshot.png"));
		Thread.sleep(3000);
	}
	
	@Test
	public void colors() throws Exception {
		driver.get("https://demoqa.com/select-menu");
		Thread.sleep(3000);
		
		WebElement dropdown = driver.findElement(By.xpath("//div[@id='selectMenuContainer']//div[@class='row']//div[contains(@class,'css-tlfecz-indicatorContainer')]"));
		dropdown.click();
		
		String pageSource = driver.getPageSource();
				  String filePath = "output.txt";

        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(pageSource);
            writer.close();
            System.out.println("File written successfully at: " + filePath);
        } catch (IOException e) {
            System.out.println("An error occurred while writing the file.");
            e.printStackTrace();
        }
		
		List<WebElement> options = driver.findElements(By.cssSelector("div[id^='react-select-4-option']"));
		for (WebElement option : options) {
		    System.out.println(option.getText());
		    option.click();
		    dropdown.click();
		    Thread.sleep(3000);
		}
	}
}
