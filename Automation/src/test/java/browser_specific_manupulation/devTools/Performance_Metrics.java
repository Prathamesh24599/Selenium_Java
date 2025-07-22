package browser_specific_manupulation.devTools;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v138.performance.Performance;
import org.openqa.selenium.devtools.v138.performance.model.Metric;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class Performance_Metrics {
	WebDriver driver;
	DevTools devTools;

	@BeforeMethod
	public void Setup() {
		driver = new ChromeDriver();
		devTools = ((ChromeDriver) driver).getDevTools();
		devTools.createSession();
	}
	
	@Test
	public void Test() {
		 devTools.send(Performance.enable(Optional.empty()));
	        driver.get("https://bonigarcia.dev/selenium-webdriver-java/");

	        List<Metric> metrics = devTools.send(Performance.getMetrics());
	        assertThat(metrics).isNotEmpty();
	        metrics.forEach(metric -> System.out.println(metric.getName() +" : "+metric.getValue()));
	}

	@AfterMethod
	public void TearDown() {
		devTools.close();
		driver.quit();
	}
}
