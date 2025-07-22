package browser_agnostic_feature.Listner;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.SkipException;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.Reporter;

public class TestNG {
	WebDriver driver = new ChromeDriver();
	
	@Test  //Success Test
	public void CloseBrowser() {
//		driver.close();
		Reporter.log("Driver Closed After Testing");
	}
	
	@Test //Failed Test
	public void OpenBrowser() {
	        String expectedTitle = "Free QA Automation Tools For Everyone";
	        String originalTitle = "";
	        Assert.assertEquals(originalTitle, expectedTitle, "Titles of the website do not match");
  }
	private int i = 1;

	@Test (successPercentage = 60, invocationCount = 3) //Failing Within Success
	public void AccountTest() {
			if(i < 2)
				Assert.assertEquals(i , i);
		i++;
	}
	
	@Test  // Skip Test
	public void SkipTest() {
		throw new SkipException("Skipping The Test Method ");
	}
}