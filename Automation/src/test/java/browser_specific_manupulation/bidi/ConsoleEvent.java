package browser_specific_manupulation.bidi;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.events.CdpEventTypes;
import org.openqa.selenium.logging.HasLogEvents;
import org.testng.annotations.AfterMethod;

public class ConsoleEvent {
	WebDriver driver;
  @Test
  public void f() throws Exception {
	  HasLogEvents logger = (HasLogEvents) driver;

      CountDownLatch latch = new CountDownLatch(4);
      logger.onLogEvent(CdpEventTypes.consoleEvent(consoleEvent -> {
    	  System.out.println(consoleEvent.getTimestamp()+" : "+
                  consoleEvent.getType()+" " +consoleEvent.getMessages());
          latch.countDown();
      }));

      driver.get(
              "https://bonigarcia.dev/selenium-webdriver-java/console-logs.html");

      assertThat(latch.await(10, TimeUnit.SECONDS)).isTrue();
  }
  @BeforeMethod
  public void beforeMethod() {
	  driver = new ChromeDriver();
  }

  @AfterMethod
  public void afterMethod() {
	  driver.quit();
  }

}
