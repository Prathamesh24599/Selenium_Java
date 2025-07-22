package browser_specific_manupulation.Browser;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

public class Options {
	WebDriver driver;
	
	@AfterMethod
	public void AfterMethod() {
//		driver.quit();
	}
	
	@Test
	public void f() {
		ChromeOptions options = new ChromeOptions();
		 options.addArguments("--start-maximized");
	     options.addArguments("--incognito");
	     options.addArguments("--disable-notifications");
	     options.addArguments("--remote-allow-origins=*");
	     options.addArguments("--window-size=1080,720");
	     driver = new ChromeDriver(options);
	     driver.get("http://www.google.co.in");
		
	}
	
	@Test
	public void headLess() throws Exception {
		ChromeOptions options = new ChromeOptions();
		 options.addArguments("--headless");
	     driver = new ChromeDriver(options);
	     driver.get("https://bonigarcia.dev/selenium-webdriver-java/");
	     File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		 FileUtils.copyFile(screenshot, new File("homePageScreenshot.png"));
	}
	
	@Test
	public void SetExeperiment() throws Exception {
		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation"));
		driver = new ChromeDriver(options);
		Thread.sleep(3000);
	}
	
	@Test
	public void Location() {
		ChromeOptions options = new ChromeOptions();
		options.setBrowserVersion("138.0.7204.50");
		options.setPlatformName("windows");
		System.out.println(options.getBrowserName());
		System.out.println(options.getBrowserVersion());
		System.out.println(options.getPlatformName());
		driver = new ChromeDriver(options);
	}
	
	@Test
	public void addExtension() throws Exception {
		ChromeOptions options = new ChromeOptions();
		options.addExtensions(new File("C:\\Users\\Prathamesh\\eclipse-workspace\\Automation\\src\\test\\resources\\Extension\\SelectorsHub.crx")); 
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(ChromeOptions.CAPABILITY, options);
		options.merge(capabilities);
		ChromeDriver driver = new ChromeDriver(options);
		driver.get("https:www.google.co.in");
		WebElement searchBox = driver.findElement(By.name("q"));
		Actions actions = new Actions(driver);
		actions.contextClick(searchBox).build().perform();
		Thread.sleep(10000);
	}
	
	@Test
	public void extension() throws Exception {
		 ChromeOptions options = new ChromeOptions();
		 options.addExtensions (new File("C:\\Users\\Prathamesh\\eclipse-workspace\\Automation\\src\\test\\resources\\Extension\\SelectorsHub.crx"));

		 DesiredCapabilities capabilities = new DesiredCapabilities ();

		 capabilities.setCapability(ChromeOptions.CAPABILITY, options);

		 ChromeDriver driver = new ChromeDriver(options);

	        // Open a page
	     driver.get("https://www.google.co.in");

	        // Right-click on search box
	     WebElement searchBox = driver.findElement(By.name("q"));
	     Actions actions = new Actions(driver);
	     actions.contextClick(searchBox).build().perform();

	     Thread.sleep(10000); // Just to keep browser open for you to see
	}
	
	@Test
	public void addPreference() {
		
		 ChromeOptions options = new ChromeOptions();

	        // 3. Create prefs map to store preferences
	        Map<String, Object> prefs = new HashMap<>();

	        // Disable browser notifications
	        prefs.put("profile.default_content_setting_values.notifications", 2);

	        // Set download directory (replace with valid path)
	        prefs.put("download.default_directory", "D:\\Downloads");

	        // Disable download prompt
	        prefs.put("download.prompt_for_download", false);

	        // Enable automatic downloads without confirmation
	        prefs.put("profile.content_settings.exceptions.automatic_downloads.*.setting", 1);

	        // Add prefs to ChromeOptions
	        options.setExperimentalOption("prefs", prefs);
	        // 4. Launch Chrome with options
	        WebDriver driver = new ChromeDriver(options);

	        // 5. Navigate to a page (example)
	        driver.get("https://demoqa.com");
	}
	
}
