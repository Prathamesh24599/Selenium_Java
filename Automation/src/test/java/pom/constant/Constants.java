package pom.constant;

/**
 * Constants class to store all application-wide constants
 * This helps maintain consistency and makes changes easier
 */
public class Constants {
    
    // Application URLs
    public static final String BASE_URL = "https://bonigarcia.dev/selenium-webdriver-java/login-form.html";
    public static final String HOME_PAGE_URL = BASE_URL + "/home";
    
    // Browser Configuration
    public static final String DEFAULT_BROWSER = "chrome";
    public static final String CHROME = "chrome";
    public static final String FIREFOX = "firefox";
    public static final String EDGE = "edge";
    
    // Timeout Configuration (in seconds)
    public static final int DEFAULT_TIMEOUT = 10;
    public static final int PAGE_LOAD_TIMEOUT = 30;
    public static final int EXPLICIT_WAIT_TIMEOUT = 15;
    public static final int IMPLICIT_WAIT_TIMEOUT = 10;
    
    // Test Data Constants
    public static final String VALID_USERNAME = "user";
    public static final String VALID_PASSWORD = "user";
    
    // Expected Messages
    public static final String EXPECTED_SUCCESS_MESSAGE = "Login successful";
    public static final String EXPECTED_ERROR_MESSAGE = "Invalid credentials";
    public static final String EXPECTED_EMPTY_USERNAME_ERROR = "Please fill out this field.";
    public static final String EXPECTED_EMPTY_PASSWORD_ERROR = "Please fill out this field.";
    
    // Test Groups
    public static final String SMOKE_GROUP = "smoke";
    public static final String REGRESSION_GROUP = "regression";
    public static final String POSITIVE_GROUP = "positive";
    public static final String NEGATIVE_GROUP = "negative";
    public static final String BOUNDARY_GROUP = "boundary";
    public static final String UI_GROUP = "ui";
    public static final String SECURITY_GROUP = "security";
    
    // File Paths
    public static final String SCREENSHOTS_PATH = "screenshots/";
    public static final String REPORTS_PATH = "reports/";
    public static final String TEST_DATA_PATH = "src/test/resources/testdata/";
    public static final String CONFIG_FILE_PATH = "src/test/resources/config.properties";
    
    // Chrome Options Arguments
    public static final String[] CHROME_ARGS = {
        "--disable-blink-features=AutomationControlled",
        "--disable-extensions",
        "--no-sandbox",
        "--disable-dev-shm-usage",
        "--disable-web-security",
        "--allow-running-insecure-content"
    };
    
    // Private constructor to prevent instantiation
    private Constants() {
        throw new IllegalStateException("Constants class cannot be instantiated");
    }
}