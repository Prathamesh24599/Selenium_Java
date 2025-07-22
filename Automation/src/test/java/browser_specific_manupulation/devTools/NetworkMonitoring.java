package browser_specific_manupulation.devTools;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v138.network.Network;
import org.openqa.selenium.devtools.v138.network.model.Headers;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class NetworkMonitoring {
	WebDriver driver;

	DevTools devTools;
	
	@BeforeMethod
	public void SetUp() {
		driver = new ChromeDriver();
		devTools = ((ChromeDriver)driver).getDevTools();
		devTools.createSession();
	}
	
	@AfterMethod
	public void Teardown() {
		devTools.close();
		driver.quit();
	}
	@Test
	public void f() {
		devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));

		devTools.addListener(Network.requestWillBeSent(), request -> {
			System.out.print("Request " + request.getRequestId());
			System.out.print("\t Method: " + request.getRequest().getMethod());
			System.out.print("\t URL: " + request.getRequest().getUrl());
			System.out.print(request.getRequest().getHeaders());
		});

		devTools.addListener(Network.responseReceived(), response -> {
			System.out.print("Response }" + response.getRequestId());
			System.out.print("\t URL: " + response.getResponse().getUrl());
			System.out.print("\t Status: " + response.getResponse().getStatus());
			logHeaders(response.getResponse().getHeaders());
		});

		driver.get("https://bonigarcia.dev/selenium-webdriver-java/");
		assertThat(driver.getTitle()).contains("Selenium WebDriver");
	}
	
	void logHeaders(Headers headers) {
		 System.out.println("\t Headers:");
	        headers.toJson().forEach((k, v) -> System.out.print("\t\t "+k+":"+v));
		
	}

	
}
