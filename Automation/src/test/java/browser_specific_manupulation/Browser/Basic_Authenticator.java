package browser_specific_manupulation.Browser;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.HasAuthentication;
import org.openqa.selenium.UsernameAndPassword;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

public class Basic_Authenticator {
	WebDriver driver;

	@Test
	public void f() {
		driver = new ChromeDriver();
		((HasAuthentication) driver).register(() -> new UsernameAndPassword("guest", "guest"));

		driver.get("https://jigsaw.w3.org/HTTP/Basic/");

		WebElement body = driver.findElement(By.tagName("body"));
		assertThat(body.getText()).contains("Your browser made it!");
	}
}
