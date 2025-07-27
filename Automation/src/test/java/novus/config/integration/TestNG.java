package novus.config.integration;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import novus.config.main_configuration.EnterpriseConfigurationManager;
import pom.data.DataProviders;
import pom.pages1.Home_Page1;
import pom.pages1.Login_Page1;

/**
 * TestNG integration example
 */
public class TestNG {
	private static final Logger logger = LoggerFactory.getLogger(TestNG.class);
	private EnterpriseConfigurationManager configManager;
	private WebDriver driver;

	 @BeforeClass
	public void setUpClass() {
		logger.info("Setting up test class...");
		configManager = EnterpriseConfigurationManager.getInstance();

		// Verify system health before starting tests
		if (!configManager.isReadyForExecution()) {
			throw new RuntimeException("System is not ready for test execution");
		}

		logger.info("Test class setup completed");
	}

	 @BeforeMethod
	public void setUp() {
		logger.info("Setting up test method...");

		// Create WebDriver for each test method
		driver = configManager.createDriver();

		// Navigate to base URL
		driver.get(configManager.getBaseUrl());

		logger.info("Test method setup completed");
	}

	 @Test(dataProvider = "validLoginData", dataProviderClass = DataProviders.class,
				priority = 1, 
				groups = { "smoke", "regression","positive" }, 
				description = "Test successful login with valid credentials")
		public void testSuccessfulLogin(String username, String password) {
//			logger.info("Testing successful login with username: {}", username);

			// Test execution
			Login_Page1 loginPage = new Login_Page1(driver);
		Home_Page1 homePage = loginPage.performValidLogin(username, password);
		
			// Assertions
			homePage.verifySuccessMessage();

		}

	 @AfterMethod
	public void tearDown() {
		logger.info("Tearing down test method...");

		// Take screenshot if test failed and screenshots are enabled
		if (configManager.shouldTakeScreenshots()) {
			// Implementation would take screenshot here
			logger.info("Screenshot would be taken to: {}", configManager.getScreenshotPath());
		}

		// Quit driver for current test
		configManager.quitDriver();

		logger.info("Test method teardown completed");
	}

	 @AfterClass
	public void tearDownClass() {
		logger.info("Tearing down test class...");

		// Cleanup all resources for this test class
		configManager.quitAllDrivers();
//		configManager.closeAllDatabaseConnections();

		logger.info("Test class teardown completed");
	}
}
