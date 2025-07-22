package webDriver_fundamentals;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class Action_class {
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
	public void right_click() throws Exception {
		Actions actions = new Actions(driver);
		driver.findElement(By.linkText("Dropdown menu")).click();
		WebElement dc_dopdown = driver.findElement(By.id("my-dropdown-2"));
		actions.contextClick(dc_dopdown).build().perform();
		Thread.sleep(3000);
	}
	
	@Test
	public void mouse_hover() throws Exception {
		driver.findElement(By.linkText("Mouse over")).click();
		Actions actions = new Actions(driver);
		List<WebElement> img = driver.findElements(By.xpath("//div[@class='figure text-center col-3 py-2']"));
		for (WebElement img1 : img) {
			actions.moveToElement(img1).build().perform();
			Thread.sleep(3000);
		}
	}
	
	
	@Test
	public void double_click() throws Exception {
		Actions actions = new Actions(driver);
		driver.findElement(By.linkText("Dropdown menu")).click();
		WebElement dc_dopdown = driver.findElement(By.id("my-dropdown-3"));
		actions.doubleClick(dc_dopdown).build().perform();
		Thread.sleep(3000);
	}
	
	@Test
	public void Drag_Drop() throws Exception {
		Actions actions = new Actions(driver);
		driver.findElement(By.linkText("Drag and drop")).click();
		WebElement Drag = driver.findElement(By.id("draggable"));
		WebElement Drop = driver.findElement(By.id("target"));
		actions.dragAndDrop(Drag, Drop).build().perform();
		Thread.sleep(3000);
	}
	
	@Test
	public void click_hold() throws Exception {
		Actions actions = new Actions(driver);
		driver.findElement(By.linkText("Drag and drop")).click();
		WebElement Drag = driver.findElement(By.id("draggable"));
		WebElement Drop = driver.findElement(By.id("target"));
		actions.clickAndHold(Drag).release(Drop).build().perform();
		Thread.sleep(3000);
	}
	
	@Test
	public void slider() throws Exception {
		Actions actions = new Actions(driver);
		driver.findElement(By.linkText("Web form")).click();
        WebElement slider = driver.findElement(By.cssSelector("input[type='range']"));

        actions.clickAndHold(slider).moveByOffset(50, 0).release().perform();
		Thread.sleep(3000);
	}
	
	@Test
	public void keyBoard() throws Exception {
		Actions actions = new Actions(driver);
		driver.findElement(By.linkText("Web form")).click();
		WebElement textbox = driver.findElement(By.id("my-text-id"));
		textbox.sendKeys("Prathamesh");
		actions.keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).perform(); // Ctrl+A to select all
        actions.keyDown(Keys.CONTROL).sendKeys("c").keyUp(Keys.CONTROL).perform(); // Ctrl+C to copy
		Thread.sleep(2000);
		actions.sendKeys(Keys.TAB).perform();
		actions.keyDown(Keys.CONTROL).sendKeys("v").keyUp(Keys.CONTROL).perform();
		Thread.sleep(3000);
		
	}
	
	@Test
    public void moveByOffsetFromElement() {
        driver.get("https://www.selenium.dev/selenium/web/mouse_interaction.html");
        driver.manage().window().fullscreen();

        WebElement tracker = driver.findElement(By.id("mouse-tracker"));
        new Actions(driver)
                .moveToElement(tracker, 8, 0)
                .perform();

    }
	
	@Test
	public void testClickAndHold() {
		 driver.get(
		 "https://bonigarcia.dev/selenium-webdriver-java/draw-in-canvas.html");
		 Actions actions = new Actions(driver);
		 WebElement canvas = driver.findElement(By.tagName("canvas"));
		 actions.moveToElement(canvas).clickAndHold();
		 int numPoints = 10;
		 int radius = 30;
		 for (int i = 0; i <= numPoints; i++) {
			 double angle = Math.toRadians(360 * i / numPoints);
			 double x = Math.sin(angle) * radius;
			 double y = Math.cos(angle) * radius;
			 actions.moveByOffset((int) x, (int) y);
		 }
		 actions.release(canvas).build().perform();
	}
}
