package browser_specific_manupulation.Browser;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import java.util.ResourceBundle;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class Language {
	WebDriver driver;
	String lang;

	@BeforeMethod
	public void SetUp() {
		lang = "en-ES";
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--lang=" + lang);
		driver = new ChromeDriver(options);
	}

	@AfterMethod
	public void TearDown() {
		driver.quit();
	}

	@Test
	public void f() throws Exception {
		  driver.get(
	                "https://bonigarcia.dev/selenium-webdriver-java/multilanguage.html");

	        ResourceBundle strings = ResourceBundle.getBundle("strings",
	                Locale.forLanguageTag(lang));
	        String home = strings.getString("home");
	        String content = strings.getString("content");
	        String about = strings.getString("about");
	        String contact = strings.getString("contact");

	        String bodyText = driver.findElement(By.tagName("body")).getText();
	        assertThat(bodyText).contains(home).contains(content).contains(about)
	                .contains(contact);
	        Thread.sleep(3000);
	}
}
