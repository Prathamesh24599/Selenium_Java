package webDriver_fundamentals;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class WebTable {
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
	public void f() {
		driver.get("https://testautomationpractice.blogspot.com/#");
		WebElement table = driver.findElement(By.name("BookTable"));
		List<WebElement> rows = table.findElements(By.tagName("tr"));
		for(int i = 0; i < rows.size(); i++) {
			if(i == 0) {
				List<WebElement> cols = rows.get(i).findElements(By.tagName("th"));
				for(int j = 0; j < cols.size(); j++) {
					String colValue = cols.get(j).getText();
					System.out.print(colValue +"\t");
				}
				System.out.println();
			}else {
				List<WebElement> cols = rows.get(i).findElements(By.tagName("td"));
				for(int j = 0; j < cols.size(); j++) {
					String colValue = cols.get(j).getText();
					System.out.print(colValue +"\t");
				}
				System.out.println();
			}
		}
	}
	
	@Test
	public void spec_col() {
		driver.get("https://testautomationpractice.blogspot.com/#");
		WebElement table = driver.findElement(By.name("BookTable"));
		List<WebElement> rows = table.findElements(By.tagName("tr"));		
		List<WebElement> header = rows.get(0).findElements(By.tagName("th"));
		int columnNum = -1;
		for(int i = 0; i < header.size()-1; i++) {
			String headerVal = header.get(i).getText();
			if(headerVal.equals("Author")) {
				System.out.println(headerVal);
				columnNum = i;
			}
		}
		System.out.println(columnNum);
		for (int j = 1; j < rows.size(); j++) {
			List<WebElement> cols = rows.get(j).findElements(By.tagName("td"));
			System.out.println(cols.get(columnNum).getText());
		}
	}
	
	@Test
	public void dynamic_test() throws Exception {
		driver.get("https://testautomationpractice.blogspot.com/#");
		for(int i = 0; i < 5; i++ ) {
			System.out.println(driver.findElement(By.xpath("//*[@class = 'chrome-cpu']")).getText());
			driver.navigate().refresh();
			Thread.sleep(3000);
		}
		
	}
	
	@Test
	public void Table_links() throws Exception {
		driver.get("https://demoqa.com/webtables");
		List<WebElement> rows = driver.findElements(By.cssSelector(".rt-tbody .rt-tr-group"));
		System.out.println(rows.size());
		for (WebElement row : rows) {
		    List<WebElement> cells = row.findElements(By.cssSelector(".rt-td"));
		    if (cells.size() > 0) {
		        String firstName = cells.get(0).getText();  // First column is First Name
		        if (firstName.equals("Alden")) {
		            WebElement actionCell = cells.get(cells.size() - 1); // Last column has action buttons
		            WebElement editBtn = actionCell.findElement(By.cssSelector("span[id^='edit-record']"));
		            Thread.sleep(3000);
		            editBtn.click(); // Click edit
		            Thread.sleep(3000);
		            break;
		            
		        }
		    }
		}	
	}
	
	@Test
	public void Pagination() throws Exception {
		driver.get("https://blazedemo.com/");
		driver.findElement(By.xpath("//input[@class='btn btn-primary']")).click();
		WebElement table = driver.findElement(By.className("table"));
		List<WebElement> rows = table.findElements(By.tagName("tr"));
		System.out.println(rows.size());
		for (int i = 1; i < rows.size(); i++) {
			List<WebElement> cells = rows.get(i).findElements(By.tagName("td"));
			String Airline = cells.get(2).getText();
			if(Airline.equals("United Airlines")){
				WebElement flight_but = cells.get(0);
				WebElement choose_but = flight_but.findElement(By.tagName("input"));
				System.out.println(choose_but.getAttribute("value"));
				choose_but.click();
				Thread.sleep(3000);
				break;
			}
		}
		Thread.sleep(2000);
	}
	
	@Test
	public void Pagination1() {
		driver.get("https://testautomationpractice.blogspot.com/#");
		List<WebElement> list_Options = driver.findElements(By.xpath("//*[@id='pagination']//li"));
		System.out.println(list_Options.size());
		for (int i = 0; i < list_Options.size(); i++) {
			WebElement page_link = list_Options.get(i).findElement(By.tagName("a"));
			page_link.click();
			WebElement table = driver.findElement(By.id("productTable"));
			List<WebElement> rows = table.findElements(By.tagName("tr"));
			for(int j = 0; j < rows.size(); j++) {
				if(j == 0) {
					List<WebElement> cols = rows.get(j).findElements(By.tagName("th"));
					for(int k = 0; k < cols.size(); k++) {
						String colValue = cols.get(k).getText();
						System.out.print(colValue +"\t");
					}
					System.out.println();
				}else {
					List<WebElement> cols = rows.get(j).findElements(By.tagName("td"));
					for(int k = 0; k < cols.size(); k++) {
						String colValue = cols.get(k).getText();
						System.out.print(colValue +"\t");
					}
					System.out.println();
				}
			}
		}
		
	}
}
