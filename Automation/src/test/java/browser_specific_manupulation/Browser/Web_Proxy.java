package browser_specific_manupulation.Browser;

import java.io.File;
import java.io.IOException;

import org.openqa.selenium.HasAuthentication;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.UsernameAndPassword;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Web_Proxy {
	WebDriver driver;
	
	@Test
	public void newTest() throws Exception {
		Proxy proxy = new Proxy();
		String proxyStr = "38.154.227.167:5868";
		proxy.setHttpProxy(proxyStr);
		proxy.setSslProxy(proxyStr);
		ChromeOptions options = new ChromeOptions();
		options.setAcceptInsecureCerts(true);
		options.setProxy(proxy);
		driver = WebDriverManager.chromedriver().capabilities(options).create();
		((HasAuthentication) driver)
        .register(() -> new UsernameAndPassword("lgzlswko", "m9s7add53nqz"));
		driver.get("https://whatismyipaddress.com/");
		Thread.sleep(15000);
	}
	
	@Test
	public void proxy() throws Exception {
        ChromeOptions options = new ChromeOptions();
        options.setAcceptInsecureCerts(true);
        options.addExtensions(new File("./src/test/resources/Extension/Extfile.zip"));
       
        driver = new ChromeDriver(options);
        
        driver.get("https://api.ipify.org/");
        Thread.sleep(3000);
	}

}
