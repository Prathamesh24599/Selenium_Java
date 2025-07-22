package browser_specific_manupulation.Browser;

import static java.lang.invoke.MethodHandles.lookup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class AddExtension {

    static final Logger log = getLogger(lookup().lookupClass());

    WebDriver driver;

    @BeforeMethod
    public void setup() throws URISyntaxException {
//        Path extension = Paths.get(
//                ClassLoader.getSystemResource("shade_dark_mode.crx").toURI());
//        ChromeOptions options = new ChromeOptions();
//        options.addExtensions(extension.toFile());
    	
//    	working  //
//    	 Path extension = Paths
//                 .get(ClassLoader.getSystemResource("web-extension").toURI());
//         ChromeOptions options = new ChromeOptions();
//         options.addArguments(
//                 "--disable-features=DisableLoadExtensionCommandLineSwitch");
//         options.addArguments(
//                 "--load-extension=" + extension.toAbsolutePath().toString());
    	
   	 Path extension = Paths
      .get(ClassLoader.getSystemResource("Extfile").toURI());
ChromeOptions options = new ChromeOptions();
options.addArguments(
      "--disable-features=DisableLoadExtensionCommandLineSwitch");
options.addArguments(
      "--load-extension=" + extension.toAbsolutePath().toString());

        driver = WebDriverManager.chromedriver().capabilities(options).create();
    }

    @AfterMethod
    public void teardown() throws InterruptedException {
        // FIXME: pause for manual browser inspection
        Thread.sleep(Duration.ofSeconds(3).toMillis());

//        driver.quit();
    }

    @Test
    public void testAddExtension() {
//        driver.get("https://bonigarcia.dev/selenium-webdriver-java/");
//        WebElement body = driver.findElement(By.tagName("body"));
//        log.debug("Background color is {}"
//                + body.getCssValue("background-color"));
    	
    	driver.get("https://bonigarcia.dev/selenium-webdriver-java/");

        WebElement h1 = driver.findElement(By.tagName("h1"));
        assertThat(h1.getText())
                .isNotEqualTo("Hands-On Selenium WebDriver with Java");
    }

}