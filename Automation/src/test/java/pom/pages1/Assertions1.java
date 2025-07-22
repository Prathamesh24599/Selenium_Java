package pom.pages1;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class Assertions1 {

    // Constants
    private static final int DEFAULT_TIMEOUT = 10;
    
    // Logger (you might want to add proper logging)
    private static void logAssertion(String message) {
        System.out.println("[ASSERTION] " + message);
    }

    /**
     * Verify element is present and displayed on page
     */
    public static void verify_element_on_page(WebDriver driver, By selector, String elementDescription, String pageName) {
        try {
            WebElement element = driver.findElement(selector);
            Assert.assertTrue(element.isDisplayed(),
                    String.format("Element '%s' should be visible on the '%s' page, but it is not.", 
                            elementDescription, pageName));
            logAssertion(String.format("✓ Element '%s' is present on '%s' page", elementDescription, pageName));
        } catch (NoSuchElementException e) {
            String errorMessage = String.format("Element '%s' should be on the '%s' page, but it was not found.", 
                    elementDescription, pageName);
            logAssertion("✗ " + errorMessage);
            Assert.fail(errorMessage);
        }
    }

    /**
     * Verify element is not present on page
     */
    public static void verify_element_not_on_page(WebDriver driver, By selector, String elementDescription, String pageName) {
        try {
            WebElement element = driver.findElement(selector);
            if (element.isDisplayed()) {
                String errorMessage = String.format("Element '%s' should not be visible on the '%s' page, but it is.", 
                        elementDescription, pageName);
                logAssertion("✗ " + errorMessage);
                Assert.fail(errorMessage);
            }
        } catch (NoSuchElementException e) {
            // Element not found - this is expected
            logAssertion(String.format("✓ Element '%s' is not present on '%s' page", elementDescription, pageName));
            Assert.assertTrue(true);
        }
    }

    /**
     * Verify element appears within timeout period
     */
    public static void verify_element_appears_within_timeout(WebDriver driver, By selector, String elementDescription,
            String pageName, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(selector));
            logAssertion(String.format("✓ Element '%s' appeared within %d seconds on '%s' page", 
                    elementDescription, timeoutSeconds, pageName));
        } catch (TimeoutException e) {
            String errorMessage = String.format("Element '%s' should appear within %d seconds on the '%s' page, but it did not.", 
                    elementDescription, timeoutSeconds, pageName);
            logAssertion("✗ " + errorMessage);
            Assert.fail(errorMessage);
        }
    }

    /**
     * Verify element disappears within timeout period
     */
    public static void verify_element_disappears_within_timeout(WebDriver driver, By selector, String elementDescription,
            String pageName, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(selector));
            logAssertion(String.format("✓ Element '%s' disappeared within %d seconds on '%s' page", 
                    elementDescription, timeoutSeconds, pageName));
        } catch (TimeoutException e) {
            String errorMessage = String.format("Element '%s' should disappear within %d seconds on the '%s' page, but it did not.", 
                    elementDescription, timeoutSeconds, pageName);
            logAssertion("✗ " + errorMessage);
            Assert.fail(errorMessage);
        }
    }

    /**
     * Verify element selection status (for checkboxes, radio buttons)
     */
    public static void verify_selection_status(boolean actualStatus, String expectedStatus, String elementDescription) {
        boolean expectedBooleanStatus;
        
        switch (expectedStatus.toLowerCase().trim()) {
            case "selected":
            case "checked":
            case "true":
                expectedBooleanStatus = true;
                break;
            case "not selected":
            case "unchecked":
            case "false":
                expectedBooleanStatus = false;
                break;
            default:
                String errorMessage = String.format("Invalid expected selection status: '%s'. Expected values: 'selected', 'not selected', 'checked', 'unchecked', 'true', 'false'", expectedStatus);
                logAssertion("✗ " + errorMessage);
                Assert.fail(errorMessage);
                return;
        }
        
        Assert.assertEquals(actualStatus, expectedBooleanStatus, 
                String.format("Element '%s' selection status should be [%s] but was [%s]", 
                        elementDescription, expectedStatus, actualStatus ? "selected" : "not selected"));
        
        logAssertion(String.format("✓ Element '%s' selection status is correctly [%s]", 
                elementDescription, expectedStatus));
    }

    /**
     * Verify checkbox status with better error messages
     */
    public static void verify_checkbox_status(boolean actualStatus, boolean expectedStatus, String checkboxName) {
        Assert.assertEquals(actualStatus, expectedStatus, 
                String.format("Checkbox '%s' should be [%s] but is [%s].",
                        checkboxName, expectedStatus ? "checked" : "unchecked", 
                        actualStatus ? "checked" : "unchecked"));
        
        logAssertion(String.format("✓ Checkbox '%s' status is correctly [%s]", 
                checkboxName, expectedStatus ? "checked" : "unchecked"));
    }

    /**
     * Verify element is contained within a section
     */
    public static void verify_element_in_section(WebElement parentSection, WebElement childElement, 
            String elementDescription, String sectionName) {
        List<WebElement> childElements = parentSection.findElements(By.xpath(".//*"));
        boolean isElementInSection = childElements.contains(childElement);
        
        Assert.assertTrue(isElementInSection,
                String.format("Element '%s' should be in the '%s' section, but it is not.", 
                        elementDescription, sectionName));
        
        logAssertion(String.format("✓ Element '%s' is correctly located in '%s' section", 
                elementDescription, sectionName));
    }

    /**
     * Verify element is not contained within a section
     */
    public static void verify_element_not_in_section(WebElement parentSection, WebElement childElement, 
            String elementDescription, String sectionName) {
        List<WebElement> childElements = parentSection.findElements(By.xpath(".//*"));
        boolean isElementInSection = childElements.contains(childElement);
        
        Assert.assertFalse(isElementInSection,
                String.format("Element '%s' should not be in the '%s' section, but it is.", 
                        elementDescription, sectionName));
        
        logAssertion(String.format("✓ Element '%s' is correctly not in '%s' section", 
                elementDescription, sectionName));
    }

    /**
     * Verify object equality with null handling
     */
    public static void verify_object_equals(Object expected, Object actual, String attributeDescription) {
        if (expected == null && actual == null) {
            logAssertion(String.format("✓ %s: Both expected and actual are null", attributeDescription));
            return;
        }
        
        Assert.assertEquals(actual, expected,
                String.format("%s should be [%s], but was [%s]", 
                        attributeDescription, expected, actual));
        
        logAssertion(String.format("✓ %s is correctly [%s]", attributeDescription, expected));
    }

    /**
     * Verify string contains substring (case-insensitive)
     */
    public static void verify_object_contains(String expectedSubstring, String actualString, String attributeDescription) {
        if (actualString == null) {
            String errorMessage = String.format("%s is null, cannot check if it contains [%s]", 
                    attributeDescription, expectedSubstring);
            logAssertion("✗ " + errorMessage);
            Assert.fail(errorMessage);
            return;
        }
        
        boolean contains = actualString.toLowerCase().contains(expectedSubstring.toLowerCase());
        Assert.assertTrue(contains,
                String.format("%s [%s] should contain [%s], but it does not", 
                        attributeDescription, actualString, expectedSubstring));
        
        logAssertion(String.format("✓ %s correctly contains [%s]", attributeDescription, expectedSubstring));
    }

    /**
     * Verify string does not contain substring (case-insensitive)
     */
    public static void verify_object_not_contains(String unexpectedSubstring, String actualString, String attributeDescription) {
        if (actualString == null) {
            logAssertion(String.format("✓ %s is null, so it does not contain [%s]", 
                    attributeDescription, unexpectedSubstring));
            return;
        }
        
        boolean contains = actualString.toLowerCase().contains(unexpectedSubstring.toLowerCase());
        Assert.assertFalse(contains,
                String.format("%s [%s] should not contain [%s], but it does", 
                        attributeDescription, actualString, unexpectedSubstring));
        
        logAssertion(String.format("✓ %s correctly does not contain [%s]", 
                attributeDescription, unexpectedSubstring));
    }

    /**
     * Verify list includes object
     */
    public static void verify_list_includes(List<?> list, Object expectedObject, String listDescription) {
        if (list == null) {
            String errorMessage = String.format("%s is null, cannot check if it includes [%s]", 
                    listDescription, expectedObject);
            logAssertion("✗ " + errorMessage);
            Assert.fail(errorMessage);
            return;
        }
        
        Assert.assertTrue(list.contains(expectedObject),
                String.format("%s should include [%s] but does not. List contents: %s", 
                        listDescription, expectedObject, list));
        
        logAssertion(String.format("✓ %s correctly includes [%s]", listDescription, expectedObject));
    }

    /**
     * Verify list does not include object
     */
    public static void verify_list_not_includes(List<?> list, Object unexpectedObject, String listDescription) {
        if (list == null) {
            logAssertion(String.format("✓ %s is null, so it does not include [%s]", 
                    listDescription, unexpectedObject));
            return;
        }
        
        Assert.assertFalse(list.contains(unexpectedObject),
                String.format("%s should not include [%s] but does. List contents: %s", 
                        listDescription, unexpectedObject, list));
        
        logAssertion(String.format("✓ %s correctly does not include [%s]", listDescription, unexpectedObject));
    }

    /**
     * Verify boolean is true
     */
    public static void verify_is_true(boolean actual, String attributeDescription) {
        Assert.assertTrue(actual, String.format("%s should be true, but was [%s]", attributeDescription, actual));
        logAssertion(String.format("✓ %s is correctly true", attributeDescription));
    }

    /**
     * Verify boolean is false
     */
    public static void verify_is_false(boolean actual, String attributeDescription) {
        Assert.assertFalse(actual, String.format("%s should be false, but was [%s]", attributeDescription, actual));
        logAssertion(String.format("✓ %s is correctly false", attributeDescription));
    }

    /**
     * Verify number is greater than expected
     */
    public static void verify_greater_than(Number actual, Number expected, String attributeDescription) {
        if (actual == null || expected == null) {
            String errorMessage = String.format("Cannot compare null values. %s: actual=[%s], expected=[%s]", 
                    attributeDescription, actual, expected);
            logAssertion("✗ " + errorMessage);
            Assert.fail(errorMessage);
            return;
        }
        
        Assert.assertTrue(actual.doubleValue() > expected.doubleValue(),
                String.format("%s [%s] should be greater than [%s], but it is not", 
                        attributeDescription, actual, expected));
        
        logAssertion(String.format("✓ %s [%s] is correctly greater than [%s]", 
                attributeDescription, actual, expected));
    }

    /**
     * Verify number is less than expected
     */
    public static void verify_less_than(Number actual, Number expected, String attributeDescription) {
        if (actual == null || expected == null) {
            String errorMessage = String.format("Cannot compare null values. %s: actual=[%s], expected=[%s]", 
                    attributeDescription, actual, expected);
            logAssertion("✗ " + errorMessage);
            Assert.fail(errorMessage);
            return;
        }
        
        Assert.assertTrue(actual.doubleValue() < expected.doubleValue(),
                String.format("%s [%s] should be less than [%s], but it is not", 
                        attributeDescription, actual, expected));
        
        logAssertion(String.format("✓ %s [%s] is correctly less than [%s]", 
                attributeDescription, actual, expected));
    }

    /**
     * Verify arrays/lists match exactly
     */
    public static void verify_lists_match(List<?> expectedList, List<?> actualList, String attributeDescription) {
        Assert.assertEquals(actualList, expectedList,
                String.format("%s should match [%s] but was [%s]", 
                        attributeDescription, expectedList, actualList));
        
        logAssertion(String.format("✓ %s correctly matches expected list", attributeDescription));
    }

    /**
     * Verify string matches regex pattern
     */
    public static void verify_regex_match(String actualValue, String regexPattern, String attributeDescription) {
        if (actualValue == null) {
            String errorMessage = String.format("%s is null, cannot match against regex pattern [%s]", 
                    attributeDescription, regexPattern);
            logAssertion("✗ " + errorMessage);
            Assert.fail(errorMessage);
            return;
        }
        
        Assert.assertTrue(actualValue.matches(regexPattern),
                String.format("%s [%s] should match regex pattern [%s], but it does not", 
                        attributeDescription, actualValue, regexPattern));
        
        logAssertion(String.format("✓ %s [%s] correctly matches regex pattern [%s]", 
                attributeDescription, actualValue, regexPattern));
    }

    /**
     * Verify URL contains expected path or parameter
     */
    public static void verify_url_contains(WebDriver driver, String expectedUrlPart, String description) {
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains(expectedUrlPart),
                String.format("Current URL [%s] should contain [%s] for %s", 
                        currentUrl, expectedUrlPart, description));
        
        logAssertion(String.format("✓ Current URL correctly contains [%s] for %s", expectedUrlPart, description));
    }

    /**
     * Verify page title
     */
    public static void verify_page_title(WebDriver driver, String expectedTitle, String description) {
        String actualTitle = driver.getTitle();
        Assert.assertEquals(actualTitle, expectedTitle,
                String.format("Page title should be [%s] for %s, but was [%s]", 
                        expectedTitle, description, actualTitle));
        
        logAssertion(String.format("✓ Page title is correctly [%s] for %s", expectedTitle, description));
    }

    /**
     * Soft assertion that logs but doesn't fail immediately
     */
    public static void soft_verify_equals(Object expected, Object actual, String attributeDescription) {
        try {
            Assert.assertEquals(actual, expected);
            logAssertion(String.format("✓ SOFT: %s is correctly [%s]", attributeDescription, expected));
        } catch (AssertionError e) {
            logAssertion(String.format("✗ SOFT: %s should be [%s], but was [%s] - TEST CONTINUES", 
                    attributeDescription, expected, actual));
            // Don't throw the assertion error, just log it
        }
    }
}