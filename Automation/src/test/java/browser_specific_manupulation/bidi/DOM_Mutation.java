package browser_specific_manupulation.bidi;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.events.CdpEventTypes;
import org.openqa.selenium.devtools.events.DomMutationEvent;
import org.openqa.selenium.logging.HasLogEvents;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DOM_Mutation {
	WebDriver driver;

	@BeforeMethod
	public void SetUp() {
		driver = new ChromeDriver();
	}

	@Test
	public void f() throws Exception {
		driver.get("https://bonigarcia.dev/selenium-webdriver-java/");

		HasLogEvents logger = (HasLogEvents) driver;
		JavascriptExecutor js = (JavascriptExecutor) driver;

		AtomicReference<DomMutationEvent> seen = new AtomicReference<>();
		CountDownLatch latch = new CountDownLatch(1);
		logger.onLogEvent(CdpEventTypes.domMutation(mutation -> {
			seen.set(mutation);
			 System.out.println("📌 DOM Mutation Detected:");
			    System.out.println("➡️ Changed Element: " + mutation.getElement());
			    System.out.println("➡️ New src: " + mutation.getElement().getDomProperty("src"));
			latch.countDown();
		}));

		WebElement img = driver.findElement(By.tagName("img"));
		String newSrc = "img/award.png";
		String script = String.format("arguments[0].src = '%s';", newSrc);
		js.executeScript(script, img);

		assertThat(latch.await(10, TimeUnit.SECONDS)).isTrue();
		assertThat(seen.get().getElement().getDomProperty("src")).endsWith(newSrc);
	}
}
