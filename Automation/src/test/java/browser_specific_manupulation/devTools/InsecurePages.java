package browser_specific_manupulation.devTools;



import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v138.security.Security;
import org.openqa.selenium.support.Color;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class InsecurePages {
	WebDriver driver;
	DevTools devTools;

	@BeforeMethod
	public void SetUp() {
		driver = new ChromeDriver();
		devTools = ((ChromeDriver) driver).getDevTools();
		devTools.createSession();
	}

	@AfterMethod
	public void TearDown() {
		devTools.close();
//		driver.close();
	}

	@Test
	public void f() {
		devTools.send(Security.enable());
		devTools.send(Security.setIgnoreCertificateErrors(true));
		driver.get("https://expired.badssl.com/");
		String bgColor = driver.findElement(By.tagName("body")).getCssValue("background-color");
		Color red = new Color(255, 0, 0, 1);
		assertThat(Color.fromString(bgColor)).isEqualTo(red);
	}
}
