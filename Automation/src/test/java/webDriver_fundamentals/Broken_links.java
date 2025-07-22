package webDriver_fundamentals;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class Broken_links {

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

    @Test
    public void f() throws Exception {
        driver.get("https://demoqa.com/broken");

        int broken_links = 0;
        List<WebElement> links = driver.findElements(By.tagName("a"));
        System.out.println("Total links found: " + links.size());

        for (WebElement webElement : links) {
            String hrefAttr = webElement.getAttribute("href");
            if (hrefAttr == null || hrefAttr.isEmpty()) {
                System.out.println("Empty or null href => Broken Link");
                broken_links++;
                continue;
            }

            try {
                URL linkURL = new URL(hrefAttr);
                HttpURLConnection connection = (HttpURLConnection) linkURL.openConnection();
                connection.setConnectTimeout(5000);
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode >= 400) {
                    System.out.println(hrefAttr + " => Broken Link, Response Code: " + responseCode);
                    broken_links++;
                } else {
                    System.out.println(hrefAttr + " => OK, Response Code: " + responseCode);
                }

            } catch (MalformedURLException e) {
                System.out.println("Invalid URL: " + hrefAttr);
                broken_links++;
            } catch (IOException e) {
                System.out.println("Connection failed: " + hrefAttr);
                broken_links++;
            }
        }

        System.out.println("Total Broken Links: " + broken_links);
    }
}
