package pom.pages1;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pom.data.DataProviders;
import pom.utils.Assertions;
import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.ITestResult;

/**
 * LoginTest class for testing login functionality Supports multiple browsers
 * and comprehensive test scenarios
 */
public class LoginTest {

	private static final Logger logger = LoggerFactory.getLogger(LoginTest.class);

	// Test configuration constants
	private static final String BASE_URL = "https://bonigarcia.dev/selenium-webdriver-java/login-form.html";
	private static final String DEFAULT_BROWSER = "chrome";
	private static final int DEFAULT_TIMEOUT = 10;
	private static final int PAGE_LOAD_TIMEOUT = 30;

	// Test data constants
	private static final String VALID_USERNAME = "user";
	private static final String VALID_PASSWORD = "user";
	private static final String EXPECTED_SUCCESS_MESSAGE = "Login successful";
	private static final String EXPECTED_ERROR_MESSAGE = "Invalid credentials";

	private WebDriver driver;
	private String currentBrowser;

	@BeforeClass
	public void setUpClass() {
		logger.info("=== Starting LoginTest Suite ===");
		logger.info("Base URL: {}", BASE_URL);
		logger.info("Default Browser: {}", DEFAULT_BROWSER);
	}

	@BeforeMethod
	@Parameters({ "browser" })
	public void setUp(@org.testng.annotations.Optional(DEFAULT_BROWSER) String browser) {
		logger.info("Setting up test with browser: {}", browser);

		currentBrowser = browser;

		try {
			// Initialize WebDriver
			driver = initializeDriver(browser);

			// Configure driver settings
			configureDriver();

			// Navigate to application
			driver.get(BASE_URL);
			logger.info("Successfully navigated to: {}", BASE_URL);

		} catch (Exception e) {
			logger.error("Failed to set up test environment: {}", e.getMessage());
			throw new RuntimeException("Test setup failed", e);
		}
	}

	@AfterMethod
	public void tearDown(ITestResult result) {
		String testName = result.getMethod().getMethodName();

		if (ITestResult.FAILURE == result.getStatus()) {
			logger.error("Test FAILED: {} - {}", testName, result.getThrowable().getMessage());
			// TODO: Implement screenshot capture here if needed
		} else if (ITestResult.SUCCESS == result.getStatus()) {
			logger.info("Test PASSED: {}", testName);
		} else if (ITestResult.SKIP == result.getStatus()) {
			logger.warn("Test SKIPPED: {}", testName);
		}

		// Clean up WebDriver
		if (driver != null) {
			try {
				driver.quit();
				logger.debug("WebDriver closed successfully");
			} catch (Exception e) {
				logger.warn("Error closing WebDriver: {}", e.getMessage());
			}
		}
	}

	@AfterClass
	public void tearDownClass() {
		logger.info("=== LoginTest Suite Completed ===");
	}

	/**
	 * Initialize WebDriver based on browser type
	 */
	private WebDriver initializeDriver(String browserName) {
		logger.debug("Initializing {} driver", browserName);

		switch (browserName.toLowerCase()) {
		case "chrome":
			ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
			chromeOptions.addArguments("--disable-extensions");
			chromeOptions.addArguments("--no-sandbox");
			chromeOptions.addArguments("--disable-dev-shm-usage");
			return new ChromeDriver(chromeOptions);

		case "firefox":
			return new FirefoxDriver();

		case "edge":
			return new EdgeDriver();

		default:
			logger.error("Unsupported browser: {}", browserName);
			throw new IllegalArgumentException("Browser not supported: " + browserName);
		}
	}

	/**
	 * Configure common driver settings
	 */
	private void configureDriver() {
		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(DEFAULT_TIMEOUT));
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(PAGE_LOAD_TIMEOUT));

		logger.debug("Driver configured - Timeout: {}s, Page Load Timeout: {}s", DEFAULT_TIMEOUT, PAGE_LOAD_TIMEOUT);
	}

	
	// Test Methods
	@Test(dataProvider = "validLoginData", dataProviderClass = DataProviders.class,
			priority = 1, 
			groups = { "smoke", "regression","positive" }, 
			description = "Test successful login with valid credentials")
	public void testSuccessfulLogin(String username, String password) {
		logger.info("Testing successful login with username: {}", username);

		// Test execution
		Login_Page1 loginPage = new Login_Page1(driver);
		Home_Page1 homePage = loginPage.performValidLogin(username, password);

		// Assertions
		homePage.verifySuccessMessage();

	}

	@Test(dataProvider = "invalidLoginData", dataProviderClass = DataProviders.class,
			priority = 2, groups = { "negative","regression" }, 
			description = "Test failed login with invalid credentials")
	public void testFailedLogin(String username, String password, String expectedErrorMessage) {
		logger.info("Testing failed login with username: '{}', password: '{}'", username, "***");

		// Test execution
		Login_Page1 loginPage = new Login_Page1(driver);
		Login_Page1 loginPageAfterFailure = loginPage.performInvalidLogin(username, password);

		// Assertions
		loginPageAfterFailure.verifyErrorMessage();

	}

	@Test(dataProvider = "emptyCredentialsData", dataProviderClass = DataProviders.class,
			priority = 3, groups = { "boundary","negative" }, 
			description = "Test login with empty credentials")
	public void testLoginWithEmptyCredentials(String username, String password) {
		logger.info("Testing login with empty/blank credentials");

		// Test execution
		Login_Page1 loginPage = new Login_Page1(driver);
		loginPage.performInvalidLogin(username, password);

		// Verification - should either show error or remain on login page
		loginPage.verifyErrorMessage();

	}

	@Test(priority = 4, groups = { "ui", "smoke" }, 
			description = "Test login page UI elements are present")
	public void testLoginPageUIElements() {
		logger.info("Testing login page UI elements presence");

		Login_Page1 loginPage = new Login_Page1(driver);

		// Check if all required elements are present
		loginPage.verifyUserNameUiDisplayed();

	}

	@Test(priority = 5, groups = { "security" }, 
			description = "Test password field masks input")
	public void testPasswordFieldMasking() {
		logger.info("Testing password field masking");

		Login_Page1 loginPage = new Login_Page1(driver);

		// Enter password and verify it's masked
		loginPage.enterPassword("testpassword");
		loginPage.verifyPasswordFieldIsMask();
	}
	
	@Test
	public void PageFactoryTest() throws Exception {
		LoginPage_PageFactory P1 = new LoginPage_PageFactory(driver);
		P1.enterUserName("User");
		Thread.sleep(2000);
	}
}