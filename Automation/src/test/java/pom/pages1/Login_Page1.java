package pom.pages1;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pom.utils.Assertions;
import result.LoginResult;

/**
 * Login Page class using Page Object Model with LoginResult for better test
 * scenario handling Supports both positive and negative login test scenarios
 */
public class Login_Page1 extends Base_Page1 {

	private static final Logger logger = LoggerFactory.getLogger(Login_Page1.class);

	// Page constants
	private static final String EXPECTED_TITLE = "Hands-On Selenium WebDriver with Java";
	
	public Login_Page1(WebDriver driver) {
		super(driver);
		validatePage();
		logger.info("Login_Page1 initialized");
	}
	
	// Locators - Using better naming convention
	
	private final By USERNAME_FIELD = By.id("username");
	private final By PASSWORD_FIELD = By.name("password");
	private final By SUBMIT_BUTTON = By.xpath("//button[text()='Submit']");
	private final By ERROR_MESSAGE = By.id("invalid");
	

	
	
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

	/**
	 * Enters username into the username field
	 * 
	 * @param userName Username to enter
	 * @return Current LoginPage instance for method chaining
	 */
	public Login_Page1 enterUserName(String userName) {
		if (userName == null) {
			userName = ""; // Handle null gracefully
		}

		try {
			logger.debug("Entering username: {}", userName);
			wait.until(ExpectedConditions.elementToBeClickable(USERNAME_FIELD));
			enterText(USERNAME_FIELD, userName);
			logger.debug("Username entered successfully");

		} catch (Exception e) {
			logger.error("Failed to enter username: {}", e.getMessage());
			throw new RuntimeException("Failed to enter username: " + e.getMessage(), e);
		}
		return this;
	}

	/**
	 * Enters password into the password field
	 * 
	 * @param password Password to enter
	 * @return Current LoginPage instance for method chaining
	 */
	public Login_Page1 enterPassword(String password) {
		if (password == null) {
			password = ""; // Handle null gracefully
		}

		try {
			logger.debug("Entering password");
			wait.until(ExpectedConditions.elementToBeClickable(PASSWORD_FIELD));
			enterText(PASSWORD_FIELD, password);
			logger.debug("Password entered successfully");

		} catch (Exception e) {
			logger.error("Failed to enter password: {}", e.getMessage());
			throw new RuntimeException("Failed to enter password: " + e.getMessage(), e);
		}
		return this;
	}

	/**
	 * Clicks the submit button
	 * 
	 * @return Current LoginPage instance for method chaining
	 */
	public Login_Page1 clickSubmit() {
		try {
			logger.debug("Clicking submit button");
			wait.until(ExpectedConditions.elementToBeClickable(SUBMIT_BUTTON));
			clickElement(SUBMIT_BUTTON);
			logger.debug("Submit button clicked successfully");

		} catch (Exception e) {
			logger.error("Failed to click submit button: {}", e.getMessage());
			throw new RuntimeException("Failed to click submit button: " + e.getMessage(), e);
		}
		return this;
	}

	/**
	 * Main login method that returns LoginResult for both positive and negative
	 * scenarios
	 * 
	 * @param username Username for login
	 * @param password Password for login
	 * @return LoginResult containing the outcome of login attempt
	 */
	public LoginResult performLogin(String username, String password) {
		long startTime = System.currentTimeMillis();
		logger.info("Attempting login with username: {}", username);

		try {
			// Perform login steps
			enterUserName(username);
			enterPassword(password);
			clickSubmit();

			// Wait for either success or failure indicators
			wait.until(ExpectedConditions.or(
					// Success indicator
					ExpectedConditions.visibilityOfElementLocated(SUCCESS_MESSAGE),
					// Failure indicator
					ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE)));

			long executionTime = System.currentTimeMillis() - startTime;

			// Check login outcome
			if (isLoginSuccessful()) {
				logger.info("Login successful for user: {} in {}ms", username, executionTime);
				return LoginResult.success(driver, username, executionTime);
			} else {
				String errorMessage = getErrorMessage();
				logger.info("Login failed for user: {} in {}ms - Error: {}", username, executionTime, errorMessage);
				return LoginResult.failure(errorMessage, driver, username, executionTime);
			}

		} catch (TimeoutException e) {
			long executionTime = System.currentTimeMillis() - startTime;
			String error = "Login page did not respond within timeout period";

			logger.error("{} for user: {} after {}ms", error, username, executionTime);
			return LoginResult.failure(error, driver, username, executionTime);

		} catch (Exception e) {
			long executionTime = System.currentTimeMillis() - startTime;
			String error = "Unexpected error during login: " + e.getMessage();

			logger.error("Unexpected error during login for user: {} after {}ms", username, executionTime, e);
			return LoginResult.failure(error, driver, username, executionTime);
		}
	}

	/**
	 * Convenience method for tests that expect successful login
	 * 
	 * @param username Username for login
	 * @param password Password for login
	 * @return Home_Page1 instance
	 * @throws AssertionError if login fails
	 */
	public Home_Page1 performValidLogin(String username, String password) {
		LoginResult result = performLogin(username, password);
		return result.expectSuccess();
	}

	/**
	 * Convenience method for tests that expect login failure
	 * 
	 * @param username Username for login
	 * @param password Password for login
	 * @return Current Login_Page1 instance
	 * @throws AssertionError if login succeeds
	 */
	public Login_Page1 performInvalidLogin(String username, String password) {
		LoginResult result = performLogin(username, password);
		return result.expectFailure();
	}

	/**
	 * Convenience method for tests that expect specific error message
	 * 
	 * @param username      Username for login
	 * @param password      Password for login
	 * @param expectedError Expected error message (partial match)
	 * @return Current Login_Page1 instance
	 * @throws AssertionError if login succeeds or error doesn't match
	 */
	public Login_Page1 performInvalidLoginWithError(String username, String password, String expectedError) {
		LoginResult result = performLogin(username, password);
		return result.expectFailureWithMessage(expectedError);
	}

	/**
	 * Checks if login was successful by looking for success indicators
	 * 
	 * @return true if login succeeded, false otherwise
	 */
	private boolean isLoginSuccessful() {
		try {
			// Check for error message first (faster check)
			if (isElementDisplayed(ERROR_MESSAGE)) {
				logger.debug("Error message is displayed - login failed");
				return false;
			}

			// Check for success indicator
			boolean successVisible = isElementDisplayed(SUCCESS_MESSAGE);
			logger.debug("Login success check - Success message visible: {}", successVisible);

			return successVisible;

		} catch (Exception e) {
			logger.debug("Exception during login success check: {}", e.getMessage());
			return false;
		}
	}

	/**
	 * Gets the error message displayed on login failure
	 * 
	 * @return Error message text, or generic message if none found
	 */
	public String getErrorMessage() {
		logger.info("Fetching error message");
		try {
			String errorText = getText(ERROR_MESSAGE);
			return errorText;

		} catch (Exception e) {
			logger.debug("Could not extract error message: {}", e.getMessage());
			return "Invalid credentials";
		}
	}

	/**
	 * Checks if error message is currently displayed
	 * 
	 * @return true if error message is visible
	 */
	public boolean isErrorMessageDisplayed() {
		return isElementDisplayed(ERROR_MESSAGE);
	}

	/**
	 * Checks if we're currently on the login page
	 * 
	 * @return true if on login page
	 */
	public boolean isOnLoginPage() {
		try {
			return driver.getTitle().equals(EXPECTED_TITLE) && isElementPresent(USERNAME_FIELD)
					&& isElementPresent(PASSWORD_FIELD) && isElementPresent(SUBMIT_BUTTON);
		} catch (Exception e) {
			return false;
		}
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

	
	
	
	//Verification Methods
	/**
	 * Verifies that the displayed error message matches expected text using
	 * Assertions utility
	 * 
	 * @param expectedMessage Expected error message
	 */
	public void verifyErrorMessage() {
		String actualMessage = getErrorMessage();
		Assertions1.verify_object_equals(actualMessage, "Invalid credentials", actualMessage);
	}
	
	public void verifyUserNameUiDisplayed() {
		boolean status = isElementDisplayed(USERNAME_FIELD);
		Assertions.verify_truthy(status, "Status true");
		
	}
	
	public void verifyPasswordFieldIsMask() {
		String passwordFieldType = getAttribute(PASSWORD_FIELD, "type");
		Assertions1.verify_object_equals(passwordFieldType, "password", "Password field should be of type 'password' to mask input");
	}
}