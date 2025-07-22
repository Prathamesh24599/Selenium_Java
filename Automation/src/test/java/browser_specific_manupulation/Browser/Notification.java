package browser_specific_manupulation.Browser;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class Notification {
	WebDriver driver;

	@BeforeMethod
	public void SetUp() {
		ChromeOptions options = new ChromeOptions();
		Map<String, Object> prefs = new HashMap<>();
		prefs.put("profile.default_content_setting_values.notifications", 1);
		options.setExperimentalOption("prefs", prefs);
		driver = new ChromeDriver(options);
	}
	
	@AfterMethod
	public void TearDown() {
		driver.quit();
	}

	@Test
	public void f() {
		driver.get(
                "https://bonigarcia.dev/selenium-webdriver-java/notifications.html");
        JavascriptExecutor js = (JavascriptExecutor) driver;

        String script = String.join("\n",
                "const callback = arguments[arguments.length - 1];",
                "const OldNotify = window.Notification;",
                "function newNotification(title, options) {",
                "    callback(title);",
                "    return new OldNotify(title, options);",
                "}",
                "newNotification.requestPermission = OldNotify.requestPermission.bind(OldNotify);",
                "Object.defineProperty(newNotification, 'permission', {",
                "    get: function() {",
                "        return OldNotify.permission;",
                "    }",
                "});",
                "window.Notification = newNotification;",
                "document.getElementById('notify-me').click();");
        System.out.println("Executing the following script asynchronously:\n "+script );

        Object notificationTitle = js.executeAsyncScript(script);
        assertThat(notificationTitle).isEqualTo("This is a notification");
	}
}
