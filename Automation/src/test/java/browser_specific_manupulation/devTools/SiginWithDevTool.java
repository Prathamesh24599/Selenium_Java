package browser_specific_manupulation.devTools;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v138.network.Network;
import org.openqa.selenium.devtools.v138.network.model.Headers;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SiginWithDevTool {
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
		driver.close();
	}
	
	@Test
	public void f() {
		devTools.send(Network.enable(Optional.empty(), Optional.empty(),Optional.empty(), java.util.Optional.empty()));
		String userName = "guest";
		String password = "guest";
		Map<String, Object> headers = new HashMap<>();
		String basicAuth = "Basic " + new String(Base64.getEncoder()
		.encode(String.format("%s:%s", userName, password).getBytes()));
		headers.put("Authorization", basicAuth);
		devTools.send(Network.setExtraHTTPHeaders(new Headers(headers)));
		driver.get("https://jigsaw.w3.org/HTTP/Basic/");
		String bodyText = driver.findElement(By.tagName("body")).getText();
		assertThat(bodyText).contains("Your browser made it!");
		
	}
}
