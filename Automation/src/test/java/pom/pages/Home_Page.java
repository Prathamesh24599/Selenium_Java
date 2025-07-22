package pom.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;



public class Home_Page extends Base_Page{
	
	public Home_Page(WebDriver driver) {
		super(driver);
		if (!driver.getTitle().equals("Hands-On Selenium WebDriver with Java")) {
		      throw new IllegalStateException("This is not Sign In Page," +
		            " current page is: " + driver.getCurrentUrl());
		}
	}
	
	private By successMsg = By.id("success");
	
	
	
	//Actions

    public String getSuccessMessage() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(successMsg));
            return driver.findElement(successMsg).getText();
        } catch (TimeoutException | NoSuchElementException e) {
            return "";
        }
    }

}
