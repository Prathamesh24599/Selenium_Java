package browser_agnostic_feature;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.ScriptKey;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class JavaScriptExecutor {
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
	public void Synchronous() throws Exception {
		JavascriptExecutor js = (JavascriptExecutor)driver;
		WebElement Web_form = driver.findElement(By.linkText("Web form"));
		js.executeScript("arguments[0].click()", Web_form);
		Thread.sleep(3000);
		WebElement Input_box = driver.findElement(By.id("my-text-id"));
		js.executeScript("arguments[0].setAttribute('value', 'Name')", Input_box);
		Thread.sleep(3000);
		js.executeScript("window.scrollBy(0,3000)", "");
		Thread.sleep(3000);
		js.executeScript("document.getElementById('my-text-id').value  = 'Prathamesh'");
		Thread.sleep(3000);
	}
	@Test
	public void Pinned_Script() {
		JavascriptExecutor js = (JavascriptExecutor) driver;

		// Step 1: Define and pin the script
		ScriptKey scriptKey = js.pin("return arguments[0] + arguments[1];");

		// Step 2: Reuse the pinned script
		Object result = js.executeScript(scriptKey, 5, 10);
		System.out.println(result); // Output: 15

	}
	
	@Test
	public void Asynchronous_Script() {
		// Set the script timeout
		driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(10));

		// Execute asynchronous JS with callback
		JavascriptExecutor js = (JavascriptExecutor) driver;

		js.executeAsyncScript(
		  "let callback = arguments[arguments.length - 1];" +
		  "window.setTimeout(function() {" +
		  "  callback('Done waiting 3 seconds');" +
		  "}, 3000);"
		);

	}
	
	@Test
	public void Asynchronous_AJAX() {
		JavascriptExecutor js = (JavascriptExecutor) driver;

		// Execute async JS
		Object response = js.executeAsyncScript(
		    "const callback = arguments[arguments.length - 1];" + // required callback
		    "fetch('https://jsonplaceholder.typicode.com/users/1')" +
		    "  .then(response => response.json())" +
		    "  .then(data => callback(data.name))" +  // return 'Leanne Graham'
		    "  .catch(error => callback('Fetch failed: ' + error));"
		);

		System.out.println("User Name from API: " + response);

	}
}
