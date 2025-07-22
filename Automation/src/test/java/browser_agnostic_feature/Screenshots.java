package browser_agnostic_feature;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.OutputType;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.imageio.ImageIO;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v136.page.Page;
import org.openqa.selenium.remote.RemoteWebElement;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Base64;
import java.util.Optional;


public class Screenshots {
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
	public void f() throws Exception {
		 File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		 FileUtils.copyFile(screenshot, new File("./screenshots/homePageScreenshot.png"));
	}
	
	@Test
	public void fullPageSc() throws Exception {
		driver.get("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");
		WebElement firstName = driver.findElement(By.name("my-colors"));
		
		File src = firstName.getScreenshotAs(OutputType.FILE);
		File dest = new File("./screenshots/screenshot_webelement.png");
		Files.createDirectories(dest.getParentFile().toPath());
		Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
	 
	}
	
	@Test
	public void Screenshot() throws Exception {
		Robot robot = new Robot();
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
		ImageIO.write(screenFullImage, "png", new File("./screenshots/screenshot.png"));

	}
	
	@Test
	public void fullScreenSC3() throws Exception {
		 Object devicePixelRatio = ((JavascriptExecutor)driver).executeScript("return window.devicePixelRatio");
	       String dprValue = String.valueOf(devicePixelRatio);
	       float windowDPR = Float.parseFloat(dprValue);
	 
	       Screenshot screenshot = new AShot()
	               .shootingStrategy(ShootingStrategies.viewportPasting(ShootingStrategies.scaling(windowDPR),1000))
	               .takeScreenshot(driver);
	 
	       ImageIO.write(screenshot.getImage(), "png", new File("./screenshots/AshotFullPageScreen.png"));
	}
	
}
