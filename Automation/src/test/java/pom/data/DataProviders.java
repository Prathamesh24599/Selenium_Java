package pom.data;

import org.testng.annotations.DataProvider;
/**
 * DataProvider class to supply test data for login-related test cases.
 * Follows best practices for modularity and reusability.
 */
public class DataProviders {

    // Constants for test data
    private static final String VALID_USERNAME = "user";
    private static final String VALID_PASSWORD = "user";
    private static final String EXPECTED_ERROR_MESSAGE = "Invalid credentials";

    /**
     * Provides valid login credentials for successful login tests.
     * @return 2D array of Object[][] containing username and password.
     */
    @DataProvider(name = "validLoginData")
    public static Object[][] getValidLoginData() {
        return new Object[][] {
            { VALID_USERNAME, VALID_PASSWORD }
        };
    }

    /**
     * Provides invalid login credentials for failed login tests.
     * @return 2D array of Object[][] containing username, password, and expected error message.
     */
    @DataProvider(name = "invalidLoginData")
    public static Object[][] getInvalidLoginData() {
        return new Object[][] {
            { "invalid", "user", EXPECTED_ERROR_MESSAGE },
            { "user", "invalid", EXPECTED_ERROR_MESSAGE },
            { "", "", EXPECTED_ERROR_MESSAGE },
            { "admin", "admin", EXPECTED_ERROR_MESSAGE },
            { "wronguser", "wrongpass", EXPECTED_ERROR_MESSAGE }
        };
    }

    /**
     * Provides empty or invalid credential combinations for boundary testing.
     * @return 2D array of Object[][] containing username and password.
     */
    @DataProvider(name = "emptyCredentialsData")
    public static Object[][] getEmptyCredentialsData() {
        return new Object[][] {
            { "", "" },
            { "", "password" },
            { "username", "" },
            { " ", " " }
        };
    }
}