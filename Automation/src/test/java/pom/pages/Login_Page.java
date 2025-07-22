package pom.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import pom.utils.Assertions;


public class Login_Page extends Base_Page{
	
	public Login_Page(WebDriver driver) {
		super(driver);
		if (!driver.getTitle().equals("Hands-On Selenium WebDriver with Java")) {
		      throw new IllegalStateException("This is not Sign In Page," +
		            " current page is: " + driver.getCurrentUrl());
		}
	}
	
	//Locators
	private By usernameField = By.id("username");
	private By passwordField = By.name("password");
	private By submitBtn = By.xpath("//button[text() = 'Submit']");
	private By errorMsg = By.id("invalid");
	
	
	//Actions Methods
	public void enterUserName(String userName) {
		wait.until(ExpectedConditions.elementToBeClickable(usernameField));
		driver.findElement(usernameField).clear();
		driver.findElement(usernameField).sendKeys(userName);
	}
	
	public void enterPassword(String password) {
		wait.until(ExpectedConditions.elementToBeClickable(passwordField));
		driver.findElement(passwordField).clear();
		driver.findElement(passwordField).sendKeys(password);
	}
	
	public void clickSubmit() {
		wait.until(ExpectedConditions.elementToBeClickable(submitBtn));
		driver.findElement(submitBtn).click();
	}
	
	public Home_Page Login(String username, String password) {
		enterUserName(username);
		enterPassword(password);
		clickSubmit();
		return new Home_Page(driver);
	}
	
	public Home_Page InvalidLogin(String username, String password) {
		enterUserName(username);
		enterPassword(password);
		clickSubmit();
		return new Home_Page(driver);
	}
	
	public void failedLogin() {
		wait.until(ExpectedConditions.visibilityOfElementLocated(errorMsg));
		String message = driver.findElement(errorMsg).getText();
		Assertions.verify_object_equals("Invalid credentials", message, "Login Status: ");
	}
	
	public String getErrorMessage() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(errorMsg));
            return driver.findElement(errorMsg).getText();
        } catch (TimeoutException | NoSuchElementException e) {
            return "";
        }
    }
	
}
