package pom.pages1;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPage_PageFactory extends Base_Page1 {
	
	private static final Logger logger = LoggerFactory.getLogger(LoginPage_PageFactory.class);
	
	public LoginPage_PageFactory(WebDriver driver) {
		super(driver);
		validatePage();
		PageFactory.initElements(driver, this);
		logger.info("Login_Page1 initialized");
	}
	
	
	private static final String EXPECTED_TITLE = "Hands-On Selenium WebDriver with Java";
	
	@FindBy(id = "username")
	private WebElement USERNAME_FIELD;
	
	//@FindBys - All conditions must watch
	@FindBys({
			@FindBy(id = "password"),
			@FindBy(className = "form-control")
			})
	private WebElement PASSWORD_FIELD;
	
	@FindBy(css = "btn btn-outline-primary mt-2")
	private WebElement SUBMIT_BUTTON;
	
	/*@CacheLookup allows caching the web elements once they are located, improving
	the performance of the resulting tests. */
	@FindBy(id = "invalid")
	@CacheLookup
	private WebElement ERROR_MESSAGE;
	
	
	// Success indicator - checking for success message in home page
	private final By SUCCESS_MESSAGE = By.id("success");
	
	
	/**
	 * Validates that we're on the correct login page
	 */
	private void validatePage() {
		logger.info("Validating Login Page load...");

		try {
			// Wait for page to load with essential elements
			wait.until(ExpectedConditions.and(ExpectedConditions.titleIs(EXPECTED_TITLE)));

			logger.info("Login Page validated successfully - Title: '{}', URL: '{}'", driver.getTitle(),
					driver.getCurrentUrl());

		} catch (TimeoutException e) {
			logger.error("Login Page validation failed - Title: '{}', URL: '{}'", driver.getTitle(),
					driver.getCurrentUrl());

			throw new IllegalStateException(String.format("This is not Login Page. Expected: %s, Actual: %s, URL: %s",
					EXPECTED_TITLE, driver.getTitle(), driver.getCurrentUrl()));
		}
	}
	
	
	public void enterUserName(String username) {
		USERNAME_FIELD.sendKeys(username);
	}
	
}
