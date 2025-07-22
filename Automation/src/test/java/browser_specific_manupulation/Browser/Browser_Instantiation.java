package browser_specific_manupulation.Browser;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;

public class Browser_Instantiation {
	WebDriver driver;
	
	@AfterMethod
	public void Teardown() {
//		driver.quit();
	}
	@Test
	public void f() {
		System.setProperty("webdriver.chrome.driver", "D:\\chromedriver-win64\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get("https://bonigarcia.dev/selenium-webdriver-java/");
	}
	
	@Test
	public void WebDriverManager() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
	}
	
	@Test
	public void Options() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless", "--disable-gpu");
		driver = new ChromeDriver();
	}
	
	@Test
	public void Capabilities() throws Exception {
		   // Create ChromeOptions
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");

        // Legacy way: Merge DesiredCapabilities into ChromeOptions
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", "chrome");
        capabilities.setCapability("acceptInsecureCerts", true);

        // Merge capabilities with options
        options.merge(capabilities);

        // Pass merged options into ChromeDriver (local browser)
        WebDriver driver = new ChromeDriver(options);

        // Navigate and perform operations
        driver.get("https://example.com");
        System.out.println("Title is: " + driver.getTitle());

        // Cleanup
        driver.quit();
	}
	
	@Test
	public void newTest() {
		WebDriver driver = new ChromeDriver();
		driver.close();
	}
	
	
}
