package browser_specific_manupulation.devTools;

import static java.lang.invoke.MethodHandles.lookup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Optional;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v138.network.Network;
import org.openqa.selenium.devtools.v138.network.model.Headers;
import org.slf4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class CDP_Command {

    static final Logger log = getLogger(lookup().lookupClass());

    WebDriver driver;

    DevTools devTools;

    @BeforeMethod
    public void setup() {
        driver = WebDriverManager.chromedriver().create();
        devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSession();
    }

    @AfterMethod
    public void teardown() {
        devTools.close();
        driver.quit();
    }

    @Test
    public void testNetworkMonitoring() {
        devTools.send(Network.enable(Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty()));

        devTools.addListener(Network.requestWillBeSent(), request -> {
            log.debug("Request {}", request.getRequestId());
            log.debug("\t Method: {}", request.getRequest().getMethod());
            log.debug("\t URL: {}", request.getRequest().getUrl());
            logHeaders(request.getRequest().getHeaders());
        });

        devTools.addListener(Network.responseReceived(), response -> {
            log.debug("Response {}", response.getRequestId());
            log.debug("\t URL: {}", response.getResponse().getUrl());
            log.debug("\t Status: {}", response.getResponse().getStatus());
            logHeaders(response.getResponse().getHeaders());
        });

        driver.get("https://bonigarcia.dev/selenium-webdriver-java/");
        assertThat(driver.getTitle()).contains("Selenium WebDriver");
    }

    void logHeaders(Headers headers) {
        log.debug("\t Headers:");
        headers.toJson().forEach((k, v) -> log.debug("\t\t{}:{}", k, v));
    }

}