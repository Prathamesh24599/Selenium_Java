package browser_specific_manupulation.Browser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class MobileEmulator {

    WebDriver driver;

    @BeforeMethod
    void setup() {
        // Mobile emulation settings
        Map<String, Object> deviceMetrics = new HashMap<>();
        deviceMetrics.put("width", 500);
        deviceMetrics.put("height", 640);
        deviceMetrics.put("pixelRatio", 3.0);
        deviceMetrics.put("touch", true);

        Map<String, Object> mobileEmulation = new HashMap<>();
        mobileEmulation.put("deviceMetrics", deviceMetrics);
        mobileEmulation.put("userAgent",
            "Mozilla/5.0 (Linux; Android 4.2.1; en-us; Nexus 5 Build/JOP40D) " +
            "AppleWebKit/535.19 (KHTML, like Gecko) " +
            "Chrome/18.0.1025.166 Mobile Safari/535.19");

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("mobileEmulation", mobileEmulation);

        // Important: pass options into ChromeDriver
        driver = new ChromeDriver(options);
    }

    @Test
    public void verifyMobileViewTitle() {
        driver.get("https://bonigarcia.dev/selenium-webdriver-java/");
        String title = driver.getTitle();
        System.out.println("📱 Page title in mobile view: " + title);

        // Example verification
        Assert.assertTrue(title.toLowerCase().contains("selenium"), "Title should contain 'selenium'");
    }

    @AfterMethod
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
