package browser_agnostic_feature;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.html5.SessionStorage;
import org.openqa.selenium.html5.WebStorage;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class Web_Storage {
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
    
//	@SuppressWarnings("deprecation")
	@Test
	void testWebStorage() {
		driver.get("https://bonigarcia.dev/selenium-webdriver-java/web-storage.html");

		WebStorage webStorage = (WebStorage) driver;

		// Local Storage
		LocalStorage localStorage = webStorage.getLocalStorage();
		System.out.printf("Local storage elements: %d%n", localStorage.size());

		// Session Storage
		SessionStorage sessionStorage = webStorage.getSessionStorage();
		sessionStorage.keySet().forEach(key -> 
		    System.out.printf("Session storage: %s = %s%n", key, sessionStorage.getItem(key))
		);

		// Assertion: sessionStorage should have 2 items initially
		assertThat(sessionStorage.size()).isEqualTo(2);

		// Adding a new item to session storage
		sessionStorage.setItem("new element", "new value");

		// Assertion: should now have 3 items
		assertThat(sessionStorage.size()).isEqualTo(3);

		// Trigger page interaction to verify new value (if reflected visually)
		driver.findElement(By.id("display-session")).click();
	}
	
	@Test
	public void test() {
		driver.get("https://bonigarcia.dev/selenium-webdriver-java/web-storage.html");

		WebStorage storage = (WebStorage) driver;

		// Local Storage
		LocalStorage localStorage = storage.getLocalStorage();
		localStorage.setItem("username", "Prathamesh");

		// Session Storage
		SessionStorage sessionStorage = storage.getSessionStorage();
		sessionStorage.setItem("token", "abc123");

		// Output to verify
		System.out.println("LocalStorage: " + localStorage.getItem("username"));
		System.out.println("SessionStorage: " + sessionStorage.getItem("token"));

	}
}
