package webDriver_fundamentals;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Duration;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;







public class Robot_class {
	WebDriver driver;
	@BeforeMethod
	public void BeforeMethod() {
		System.setProperty("webdriver.chrome.driver", "D:\\chromedriver-win64\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
			  
		driver.get("https://testautomationpractice.blogspot.com/");
		
	}
	
	@AfterMethod
	public void AfterMethod() {
		driver.quit();
	}
	
	
	@Test
	public void Screenshot() throws Exception {
		Robot robot = new Robot();
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
		ImageIO.write(screenFullImage, "png", new File("./screenshots/screenshot.png"));

	}
	
	@Test
	public void Scroll() throws Exception {
		Robot robot = new Robot();
	
		robot.mouseWheel(10); // Scroll down 3 notches
		Thread.sleep(5000);
		robot.mouseWheel(-10); // Scroll up 3 notches
		Thread.sleep(5000);
	}
	
	@Test
	public void mouse_move() throws Exception {
		Robot robot = new Robot();
		robot.mouseMove(30, 500); // Move cursor to x=300, y=500
		Thread.sleep(3000);
		robot.mouseMove(500, 30);
		robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
		Thread.sleep(3000);
		robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
		Thread.sleep(3000);
	}
	
	@Test
	public void keyPress() throws Exception {
		Robot robot = new Robot();
		Thread.sleep(3000);
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_R);
		robot.keyRelease(KeyEvent.VK_R);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		Thread.sleep(5000);
	}
}
