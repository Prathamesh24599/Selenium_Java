package browser_specific_manupulation.Browser;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.CapabilityType;
import org.slf4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static java.lang.invoke.MethodHandles.lookup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Duration;

public class PageLoadingStrategy {

    static final Logger log = getLogger(lookup().lookupClass());
    WebDriver driver;
    PageLoadStrategy pageLoadStrategy;

    @BeforeMethod
    void setup() {
        ChromeOptions options = new ChromeOptions();
        pageLoadStrategy = PageLoadStrategy.NONE; // try EAGER, NONE too
        options.setPageLoadStrategy(pageLoadStrategy);

        driver = new ChromeDriver(options); // pass options during instantiation
    }

    @AfterMethod
    void teardown() {
        driver.quit();
    }

    @Test
    void testPageLoad() {
        long initMillis = System.currentTimeMillis();
        driver.get("https://bonigarcia.dev/selenium-webdriver-java/");
        Duration elapsed = Duration.ofMillis(System.currentTimeMillis() - initMillis);

        Capabilities capabilities = ((ChromeDriver) driver).getCapabilities(); // Local cast
        Object pageLoad = capabilities.getCapability(CapabilityType.PAGE_LOAD_STRATEGY);
        String browserName = capabilities.getBrowserName();

        System.out.println("The page took "+elapsed.toMillis()+" ms to load using "+pageLoad+" strategy in "+browserName);

        assertThat(pageLoad).isEqualTo(pageLoadStrategy.toString());
    }
}
