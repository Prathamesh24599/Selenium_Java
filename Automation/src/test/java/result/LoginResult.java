package result;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pom.pages1.Home_Page1;
import pom.pages1.Login_Page1;

/**
 * Result wrapper class for login operations that supports both positive and negative test scenarios.
 * This class encapsulates the outcome of a login attempt, providing type-safe access to 
 * subsequent page objects based on the login result.
 * 
 * Usage Examples:
 * - Positive test: LoginResult result = loginPage.performLogin("user", "pass"); 
 *                  if(result.isSuccessful()) { HomePage home = result.getHomePage(); }
 * - Negative test: LoginResult result = loginPage.performLogin("invalid", "creds");
 *                  if(!result.isSuccessful()) { String error = result.getErrorMessage(); }
 */
public class LoginResult {
    
    private static final Logger logger = LoggerFactory.getLogger(LoginResult.class);
    
    private final boolean successful;
    private final String errorMessage;
    private final WebDriver driver;
    private final String username;
    private final long executionTimeMs;
    
    // Private constructor to enforce factory method usage
    private LoginResult(boolean successful, String errorMessage, WebDriver driver, 
                       String username, long executionTimeMs) {
        this.successful = successful;
        this.errorMessage = errorMessage;
        this.driver = driver;
        this.username = username;
        this.executionTimeMs = executionTimeMs;
    }
    
    /**
     * Creates a successful login result
     * @param driver WebDriver instance
     * @param username Username that was used for login
     * @param executionTimeMs Time taken for login operation in milliseconds
     * @return LoginResult indicating success
     */
    public static LoginResult success(WebDriver driver, String username, long executionTimeMs) {
        logger.debug("Creating successful login result for user: {}, execution time: {}ms", 
                    username, executionTimeMs);
        return new LoginResult(true, null, driver, username, executionTimeMs);
    }
    
    /**
     * Creates a failed login result
     * @param errorMessage Error message describing why login failed
     * @param driver WebDriver instance
     * @param username Username that was used for login attempt
     * @param executionTimeMs Time taken for login operation in milliseconds
     * @return LoginResult indicating failure
     */
    public static LoginResult failure(String errorMessage, WebDriver driver, 
                                    String username, long executionTimeMs) {
        logger.debug("Creating failed login result for user: {}, error: {}, execution time: {}ms", 
                    username, errorMessage, executionTimeMs);
        return new LoginResult(false, errorMessage, driver, username, executionTimeMs);
    }
    
    /**
     * Checks if the login operation was successful
     * @return true if login succeeded, false otherwise
     */
    public boolean isSuccessful() {
        return successful;
    }
    
    /**
     * Checks if the login operation failed
     * @return true if login failed, false otherwise
     */
    public boolean isFailed() {
        return !successful;
    }
    
    /**
     * Gets the error message if login failed
     * @return Error message string, or null if login was successful
     */
    public String getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * Gets the username that was used for the login attempt
     * @return Username string
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Gets the execution time of the login operation
     * @return Execution time in milliseconds
     */
    public long getExecutionTimeMs() {
        return executionTimeMs;
    }
    
    /**
     * Gets HomePage object if login was successful
     * @return HomePage instance
     * @throws IllegalStateException if login was not successful
     */
    public Home_Page1 getHomePage() {
        if (!successful) {
            String message = String.format("Cannot get HomePage - login failed for user '%s': %s", 
                                         username, errorMessage);
            logger.error(message);
            throw new IllegalStateException(message);
        }
        
        logger.debug("Returning HomePage for successful login of user: {}", username);
        return new Home_Page1(driver);
    }
    
    /**
     * Gets LoginPage object (useful for negative test scenarios and method chaining)
     * @return LoginPage instance
     */
    public Login_Page1 getLoginPage() {
        logger.debug("Returning LoginPage for user: {}", username);
        return new Login_Page1(driver);
    }
    
    /**
     * Validates that login was successful, throwing exception if not
     * @return HomePage instance if successful
     * @throws AssertionError if login failed
     */
    public Home_Page1 expectSuccess() {
        if (!successful) {
            String message = String.format("Expected login to succeed for user '%s' but it failed: %s", 
                                         username, errorMessage);
            logger.error(message);
            throw new AssertionError(message);
        }
        return getHomePage();
    }
    
    /**
     * Validates that login failed, throwing exception if it succeeded
     * @return LoginPage instance if failed as expected
     * @throws AssertionError if login succeeded when failure was expected
     */
    public Login_Page1 expectFailure() {
        if (successful) {
            String message = String.format("Expected login to fail for user '%s' but it succeeded", username);
            logger.error(message);
            throw new AssertionError(message);
        }
        return getLoginPage();
    }
    
    /**
     * Validates that login failed with a specific error message
     * @param expectedErrorMessage Expected error message or partial message
     * @return LoginPage instance if failed with expected error
     * @throws AssertionError if login succeeded or error message doesn't match
     */
    public Login_Page1 expectFailureWithMessage(String expectedErrorMessage) {
        if (successful) {
            String message = String.format("Expected login to fail for user '%s' but it succeeded", username);
            logger.error(message);
            throw new AssertionError(message);
        }
        
        if (errorMessage == null || !errorMessage.toLowerCase().contains(expectedErrorMessage.toLowerCase())) {
            String message = String.format("Expected error message containing '%s' but got '%s' for user '%s'", 
                                         expectedErrorMessage, errorMessage, username);
            logger.error(message);
            throw new AssertionError(message);
        }
        
        return getLoginPage();
    }
    
    /**
     * Executes different actions based on login result
     * @param onSuccess Function to execute if login succeeded (receives HomePage)
     * @param onFailure Function to execute if login failed (receives error message)
     */
    public void handle(java.util.function.Consumer<Home_Page1> onSuccess, 
                      java.util.function.Consumer<String> onFailure) {
        if (successful) {
            onSuccess.accept(getHomePage());
        } else {
            onFailure.accept(errorMessage);
        }
    }
    
    /**
     * Returns a detailed string representation of the login result
     * @return String with login result details
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("LoginResult{");
        sb.append("successful=").append(successful);
        sb.append(", username='").append(username).append('\'');
        sb.append(", executionTimeMs=").append(executionTimeMs);
        
        if (!successful) {
            sb.append(", errorMessage='").append(errorMessage).append('\'');
        }
        
        sb.append('}');
        return sb.toString();
    }
    
    /**
     * Checks if execution time exceeded a threshold (useful for performance testing)
     * @param thresholdMs Threshold in milliseconds
     * @return true if execution time exceeded threshold
     */
    public boolean isSlowExecution(long thresholdMs) {
        return executionTimeMs > thresholdMs;
    }
    
    /**
     * Gets a summary of the login attempt for logging/reporting
     * @return Summary string
     */
    public String getSummary() {
        if (successful) {
            return String.format("Login SUCCESS for user '%s' in %dms", username, executionTimeMs);
        } else {
            return String.format("Login FAILED for user '%s' in %dms - %s", 
                               username, executionTimeMs, errorMessage);
        }
    }
}
