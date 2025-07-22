package pom.pages1;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pom.utils.Assertions;

/**
 * Home Page class using Page Object Model Represents the page displayed after
 * successful login
 */
public class Home_Page1 extends Base_Page1 {

	private static final Logger logger = LoggerFactory.getLogger(Home_Page1.class);

	// Page constants
	private static final String EXPECTED_TITLE = "Hands-On Selenium WebDriver with Java";

	// Locators
	private final By SUCCESS_MESSAGE = By.id("success");

	public Home_Page1(WebDriver driver) {
		super(driver);
		validatePage();
		logger.info("Home_Page1 initialized");
	}

	/**
	 * Validates that we're on the correct home page
	 */
	private void validatePage() {
		logger.info("Validating Home Page load...");

		try {
			// Wait for success message to ensure we're on the right page
			// If the login was successful, there should be a success message
			wait.until(ExpectedConditions.or(ExpectedConditions.visibilityOfElementLocated(SUCCESS_MESSAGE),
					ExpectedConditions.titleContains(EXPECTED_TITLE)));

			logger.info("Home Page validated successfully - Title: '{}', URL: '{}'", driver.getTitle(),
					driver.getCurrentUrl());

		} catch (TimeoutException e) {
			logger.error("Home Page validation failed - Title: '{}', URL: '{}'", driver.getTitle(),
					driver.getCurrentUrl());
			throw new IllegalStateException(String.format("This is not Login Page. Expected: %s, Actual: %s, URL: %s",
					EXPECTED_TITLE, driver.getTitle(), driver.getCurrentUrl()));
		}

	}

	/**
	 * Gets the success message displayed on the home page
	 * 
	 * @return Success message text
	 */
	public String getSuccessMessage() {
		logger.info("Fetching success message");
		return getText(SUCCESS_MESSAGE);
	}

	/**
	 * Checks if success message is displayed
	 * 
	 * @return true if success message is visible
	 */
	public boolean isSuccessMessageDisplayed() {
		return isElementDisplayed(SUCCESS_MESSAGE);
	}

	/**
	 * Verifies that the success message matches expected text
	 * 
	 * @param expectedMessage Expected success message
	 * @return true if message matches exactly
	 */
	public void verifySuccessMessage() {
		String successMsg = getSuccessMessage();
		Assertions.verify_object_equals(successMsg, "Login successful", successMsg);
	}

	/**
	 * Gets the current page URL
	 * 
	 * @return Current page URL
	 */
	public String getPageUrl() {
		return getCurrentUrl();
	}

	/**
	 * Gets the current page title
	 * 
	 * @return Current page title
	 */
	public String getPageTitle() {
		return driver.getTitle();
	}
}