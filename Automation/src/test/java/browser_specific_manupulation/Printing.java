package browser_specific_manupulation;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import org.openqa.selenium.Pdf;
import org.openqa.selenium.PrintsPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.print.PrintOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class Printing {
	WebDriver driver;

	@BeforeMethod
	public void Setup() {
		driver = new ChromeDriver();
	}

	@AfterMethod
	public void TearDown() {
		
		driver.quit();;
	}

	@Test
	public void f() throws Exception {
		  driver.get("https://bonigarcia.dev/selenium-webdriver-java/");
	        PrintsPage pg = (PrintsPage) driver;
	        PrintOptions printOptions = new PrintOptions();
	        Pdf pdf = pg.print(printOptions);

	        String pdfBase64 = pdf.getContent();
	        assertThat(pdfBase64).contains("JVBER");

	        byte[] decodedImg = Base64.getDecoder()
	                .decode(pdfBase64.getBytes(StandardCharsets.UTF_8));
	        Path destinationFile = Paths.get("my-pdf.pdf");
	        Files.write(destinationFile, decodedImg);
	}
}
