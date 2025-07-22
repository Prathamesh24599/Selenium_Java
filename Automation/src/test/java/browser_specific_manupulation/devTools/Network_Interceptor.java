package browser_specific_manupulation.devTools;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.NetworkInterceptor;
import org.openqa.selenium.remote.http.Contents;
import org.openqa.selenium.remote.http.HttpResponse;
import org.openqa.selenium.remote.http.Route;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class Network_Interceptor {
	WebDriver driver;
	
	@BeforeMethod
	public void SetUpMethod() {
		driver = new ChromeDriver();
	}

	@Test
	public void f() throws Exception {
		Path img = Paths.get(ClassLoader.getSystemResource("tools.png").toURI());
		byte[] bytes = Files.readAllBytes(img);

		try (NetworkInterceptor interceptor = new NetworkInterceptor(driver,
				Route.matching(req -> req.getUri().endsWith(".png"))
						.to(() -> req -> new HttpResponse().setContent(Contents.bytes(bytes))))) {
			driver.get("https://bonigarcia.dev/selenium-webdriver-java/");

			int width = Integer.parseInt(driver.findElement(By.tagName("img")).getDomProperty("width"));
			assertThat(width).isGreaterThan(80);
		}
	}
}
