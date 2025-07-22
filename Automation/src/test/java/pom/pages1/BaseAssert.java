package pom.pages1;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import java.time.Duration;
import org.openqa.selenium.support.ui.Select;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public abstract class BaseAssert {
    private static final Logger logger = LoggerFactory.getLogger(BaseAssert.class);
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected SoftAssert softAssert;
    private static final int DEFAULT_TIMEOUT = 10;
    
    public BaseAssert(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        this.softAssert = new SoftAssert();
    }
    
    public BaseAssert(WebDriver driver, int timeoutSeconds) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        this.softAssert = new SoftAssert();
    }
    
    /**
     * Verifies element is displayed with wait
     * @param element WebElement to verify
     * @param elementName Name for logging and error messages
     */
    protected void verifyElementDisplayed(WebElement element, String elementName) {
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
            Assert.assertTrue(element.isDisplayed(), 
                String.format("%s should be displayed", elementName));
            logger.info("✓ {} is displayed", elementName);
        } catch (Exception e) {
            logger.error("✗ {} is not displayed: {}", elementName, e.getMessage());
            throw new AssertionError(String.format("%s is not displayed", elementName), e);
        }
    }
    
    /**
     * Verifies element is not displayed
     * @param element WebElement to verify
     * @param elementName Name for logging and error messages
     */
    protected void verifyElementNotDisplayed(WebElement element, String elementName) {
        try {
            Assert.assertFalse(element.isDisplayed(), 
                String.format("%s should not be displayed", elementName));
            logger.info("✓ {} is not displayed", elementName);
        } catch (Exception e) {
            logger.info("✓ {} is not displayed (element not found)", elementName);
        }
    }
    
    /**
     * Verifies element is enabled
     * @param element WebElement to verify
     * @param elementName Name for logging and error messages
     */
    protected void verifyElementEnabled(WebElement element, String elementName) {
        try {
            Assert.assertTrue(element.isEnabled(), 
                String.format("%s should be enabled", elementName));
            logger.info("✓ {} is enabled", elementName);
        } catch (Exception e) {
            logger.error("✗ {} is not enabled: {}", elementName, e.getMessage());
            throw new AssertionError(String.format("%s is not enabled", elementName), e);
        }
    }
    
    /**
     * Verifies element text exactly matches expected value
     * @param element WebElement to verify
     * @param expectedText Expected text value
     * @param elementName Name for logging and error messages
     */
    protected void verifyElementText(WebElement element, String expectedText, String elementName) {
        try {
            String actualText = element.getText().trim();
            Assert.assertEquals(actualText, expectedText, 
                String.format("%s text mismatch. Expected: '%s', Actual: '%s'", 
                    elementName, expectedText, actualText));
            logger.info("✓ {} text matches expected: '{}'", elementName, expectedText);
        } catch (Exception e) {
            logger.error("✗ {} text verification failed: {}", elementName, e.getMessage());
            throw e;
        }
    }
    
    /**
     * Verifies element text contains expected substring
     * @param element WebElement to verify
     * @param expectedText Expected substring
     * @param elementName Name for logging and error messages
     */
    protected void verifyElementTextContains(WebElement element, String expectedText, String elementName) {
        try {
            String actualText = element.getText().trim();
            Assert.assertTrue(actualText.contains(expectedText), 
                String.format("%s should contain text '%s'. Actual text: '%s'", 
                    elementName, expectedText, actualText));
            logger.info("✓ {} contains expected text: '{}'", elementName, expectedText);
        } catch (Exception e) {
            logger.error("✗ {} text contains verification failed: {}", elementName, e.getMessage());
            throw e;
        }
    }
    
    /**
     * Soft assertion for element display
     * @param element WebElement to verify
     * @param elementName Name for logging and error messages
     */
    protected void softVerifyElementDisplayed(WebElement element, String elementName) {
        try {
            softAssert.assertTrue(element.isDisplayed(), 
                String.format("%s should be displayed", elementName));
            logger.info("✓ Soft assertion: {} is displayed", elementName);
        } catch (Exception e) {
            softAssert.assertTrue(false, String.format("%s is not displayed", elementName));
            logger.warn("⚠ Soft assertion failed: {} is not displayed", elementName);
        }
    }
    
    /**
     * Assert all soft assertions
     */
    public void assertAll() {
        softAssert.assertAll();
    }
    
    /**
     * Reset soft assertions
     */
    public void resetSoftAssert() {
        this.softAssert = new SoftAssert();
    }
    
    // ========================================================================
    // URL and Navigation Assertions
    // ========================================================================
    
    public static class Navigation {
        
        /**
         * Verifies current URL matches expected URL exactly
         * @param driver WebDriver instance
         * @param expectedUrl Expected URL
         */
        public static void verifyUrl(WebDriver driver, String expectedUrl) {
            String actualUrl = driver.getCurrentUrl();
            Assert.assertEquals(actualUrl, expectedUrl, 
                String.format("URL mismatch. Expected: '%s', Actual: '%s'", 
                    expectedUrl, actualUrl));
            logger.info("✓ URL matches expected: {}", expectedUrl);
        }
        
        /**
         * Verifies current URL contains expected substring
         * @param driver WebDriver instance
         * @param expectedUrlPart Expected URL substring
         */
        public static void verifyUrlContains(WebDriver driver, String expectedUrlPart) {
            String actualUrl = driver.getCurrentUrl();
            Assert.assertTrue(actualUrl.contains(expectedUrlPart), 
                String.format("URL should contain '%s'. Actual URL: '%s'", 
                    expectedUrlPart, actualUrl));
            logger.info("✓ URL contains expected part: {}", expectedUrlPart);
        }
        
        /**
         * Verifies page title matches expected title exactly
         * @param driver WebDriver instance
         * @param expectedTitle Expected page title
         */
        public static void verifyPageTitle(WebDriver driver, String expectedTitle) {
            String actualTitle = driver.getTitle();
            Assert.assertEquals(actualTitle, expectedTitle, 
                String.format("Page title mismatch. Expected: '%s', Actual: '%s'", 
                    expectedTitle, actualTitle));
            logger.info("✓ Page title matches expected: {}", expectedTitle);
        }
        
        /**
         * Verifies page title contains expected substring
         * @param driver WebDriver instance
         * @param expectedTitlePart Expected title substring
         */
        public static void verifyPageTitleContains(WebDriver driver, String expectedTitlePart) {
            String actualTitle = driver.getTitle();
            Assert.assertTrue(actualTitle.contains(expectedTitlePart), 
                String.format("Page title should contain '%s'. Actual title: '%s'", 
                    expectedTitlePart, actualTitle));
            logger.info("✓ Page title contains expected part: {}", expectedTitlePart);
        }
    }
    
    // ========================================================================
    // Element State Assertions
    // ========================================================================
    
    public static class ElementState {
        
        /**
         * Verifies element is present and visible with explicit wait
         * @param driver WebDriver instance
         * @param element WebElement to verify
         * @param elementName Name for logging
         * @param timeoutSeconds Timeout in seconds
         */
        public static void verifyElementVisible(WebDriver driver, WebElement element, 
                String elementName, int timeoutSeconds) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
                wait.until(ExpectedConditions.visibilityOf(element));
                logger.info("✓ {} is visible", elementName);
            } catch (Exception e) {
                logger.error("✗ {} is not visible within {} seconds", elementName, timeoutSeconds);
                throw new AssertionError(
                    String.format("%s is not visible within %d seconds", elementName, timeoutSeconds), e);
            }
        }
        
        /**
         * Verifies element is present and visible with default timeout
         * @param driver WebDriver instance
         * @param element WebElement to verify
         * @param elementName Name for logging
         */
        public static void verifyElementVisible(WebDriver driver, WebElement element, String elementName) {
            verifyElementVisible(driver, element, elementName, DEFAULT_TIMEOUT);
        }
        
        /**
         * Verifies element is clickable
         * @param driver WebDriver instance
         * @param element WebElement to verify
         * @param elementName Name for logging
         */
        public static void verifyElementClickable(WebDriver driver, WebElement element, String elementName) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
                wait.until(ExpectedConditions.elementToBeClickable(element));
                logger.info("✓ {} is clickable", elementName);
            } catch (Exception e) {
                logger.error("✗ {} is not clickable", elementName);
                throw new AssertionError(String.format("%s is not clickable", elementName), e);
            }
        }
        
        /**
         * Verifies element attribute value
         * @param element WebElement to verify
         * @param attributeName Attribute name
         * @param expectedValue Expected attribute value
         * @param elementName Name for logging
         */
        public static void verifyElementAttribute(WebElement element, String attributeName, 
                String expectedValue, String elementName) {
            String actualValue = element.getAttribute(attributeName);
            Assert.assertEquals(actualValue, expectedValue, 
                String.format("%s attribute '%s' mismatch. Expected: '%s', Actual: '%s'", 
                    elementName, attributeName, expectedValue, actualValue));
            logger.info("✓ {} attribute '{}' matches expected: '{}'", 
                elementName, attributeName, expectedValue);
        }
        
        /**
         * Verifies element CSS property value
         * @param element WebElement to verify
         * @param propertyName CSS property name
         * @param expectedValue Expected CSS property value
         * @param elementName Name for logging
         */
        public static void verifyCssProperty(WebElement element, String propertyName, 
                String expectedValue, String elementName) {
            String actualValue = element.getCssValue(propertyName);
            Assert.assertEquals(actualValue, expectedValue, 
                String.format("%s CSS property '%s' mismatch. Expected: '%s', Actual: '%s'", 
                    elementName, propertyName, expectedValue, actualValue));
            logger.info("✓ {} CSS property '{}' matches expected: '{}'", 
                elementName, propertyName, expectedValue);
        }
    }
    
    // ========================================================================
    // Text and Content Assertions
    // ========================================================================
    
    public static class TextContent {
        
        /**
         * Verifies text matches regex pattern
         * @param actualText Actual text to verify
         * @param pattern Regex pattern
         * @param description Description for logging
         */
        public static void verifyTextMatchesPattern(String actualText, String pattern, String description) {
            Assert.assertTrue(Pattern.matches(pattern, actualText), 
                String.format("%s should match pattern '%s'. Actual text: '%s'", 
                    description, pattern, actualText));
            logger.info("✓ {} matches expected pattern: {}", description, pattern);
        }
        
        /**
         * Verifies text is not empty or null
         * @param actualText Text to verify
         * @param description Description for logging
         */
        public static void verifyTextNotEmpty(String actualText, String description) {
            Assert.assertNotNull(actualText, String.format("%s should not be null", description));
            Assert.assertFalse(actualText.trim().isEmpty(), 
                String.format("%s should not be empty", description));
            logger.info("✓ {} is not empty: '{}'", description, actualText);
        }
        
        /**
         * Verifies element text ignoring case
         * @param element WebElement to verify
         * @param expectedText Expected text (case insensitive)
         * @param elementName Name for logging
         */
        public static void verifyElementTextIgnoreCase(WebElement element, String expectedText, String elementName) {
            String actualText = element.getText().trim();
            Assert.assertEquals(actualText.toLowerCase(), expectedText.toLowerCase(), 
                String.format("%s text mismatch (case insensitive). Expected: '%s', Actual: '%s'", 
                    elementName, expectedText, actualText));
            logger.info("✓ {} text matches expected (case insensitive): '{}'", elementName, expectedText);
        }
    }
    
    // ========================================================================
    // List and Collection Assertions
    // ========================================================================
    
    public static class Collections {
        
        /**
         * Verifies list size matches expected count
         * @param elements List of WebElements
         * @param expectedCount Expected count
         * @param listDescription Description of the list
         */
        public static void verifyListSize(List<WebElement> elements, int expectedCount, String listDescription) {
            int actualCount = elements.size();
            Assert.assertEquals(actualCount, expectedCount, 
                String.format("%s count mismatch. Expected: %d, Actual: %d", 
                    listDescription, expectedCount, actualCount));
            logger.info("✓ {} count matches expected: {}", listDescription, expectedCount);
        }
        
        /**
         * Verifies list is not empty
         * @param elements List of WebElements
         * @param listDescription Description of the list
         */
        public static void verifyListNotEmpty(List<WebElement> elements, String listDescription) {
            Assert.assertFalse(elements.isEmpty(), 
                String.format("%s should not be empty", listDescription));
            logger.info("✓ {} is not empty, contains {} items", listDescription, elements.size());
        }
        
        /**
         * Verifies list contains element with specific text
         * @param elements List of WebElements
         * @param expectedText Text to search for
         * @param listDescription Description of the list
         */
        public static void verifyListContainsText(List<WebElement> elements, String expectedText, String listDescription) {
            boolean found = elements.stream()
                .anyMatch(element -> element.getText().trim().equals(expectedText));
            Assert.assertTrue(found, 
                String.format("%s should contain element with text '%s'", listDescription, expectedText));
            logger.info("✓ {} contains element with text: '{}'", listDescription, expectedText);
        }
    }
    
    // ========================================================================
    // Form Element Assertions
    // ========================================================================
    
    public static class FormElements {
        
        /**
         * Verifies dropdown selected value
         * @param selectElement Select WebElement
         * @param expectedValue Expected selected value
         * @param dropdownName Name for logging
         */
        public static void verifyDropdownSelectedValue(WebElement selectElement, String expectedValue, String dropdownName) {
            Select select = new Select(selectElement);
            String actualValue = select.getFirstSelectedOption().getText();
            Assert.assertEquals(actualValue, expectedValue, 
                String.format("%s selected value mismatch. Expected: '%s', Actual: '%s'", 
                    dropdownName, expectedValue, actualValue));
            logger.info("✓ {} selected value matches expected: '{}'", dropdownName, expectedValue);
        }
        
        /**
         * Verifies checkbox is checked
         * @param checkboxElement Checkbox WebElement
         * @param checkboxName Name for logging
         */
        public static void verifyCheckboxChecked(WebElement checkboxElement, String checkboxName) {
            Assert.assertTrue(checkboxElement.isSelected(), 
                String.format("%s should be checked", checkboxName));
            logger.info("✓ {} is checked", checkboxName);
        }
        
        /**
         * Verifies checkbox is unchecked
         * @param checkboxElement Checkbox WebElement
         * @param checkboxName Name for logging
         */
        public static void verifyCheckboxUnchecked(WebElement checkboxElement, String checkboxName) {
            Assert.assertFalse(checkboxElement.isSelected(), 
                String.format("%s should be unchecked", checkboxName));
            logger.info("✓ {} is unchecked", checkboxName);
        }
        
        /**
         * Verifies input field value
         * @param inputElement Input WebElement
         * @param expectedValue Expected input value
         * @param fieldName Name for logging
         */
        public static void verifyInputValue(WebElement inputElement, String expectedValue, String fieldName) {
            String actualValue = inputElement.getAttribute("value");
            Assert.assertEquals(actualValue, expectedValue, 
                String.format("%s value mismatch. Expected: '%s', Actual: '%s'", 
                    fieldName, expectedValue, actualValue));
            logger.info("✓ {} value matches expected: '{}'", fieldName, expectedValue);
        }
    }
    
    // ========================================================================
    // Browser and Window Assertions
    // ========================================================================
    
    public static class Browser {
        
        /**
         * Verifies number of browser windows/tabs
         * @param driver WebDriver instance
         * @param expectedCount Expected window count
         */
        public static void verifyWindowCount(WebDriver driver, int expectedCount) {
            Set<String> windowHandles = driver.getWindowHandles();
            int actualCount = windowHandles.size();
            Assert.assertEquals(actualCount, expectedCount, 
                String.format("Window count mismatch. Expected: %d, Actual: %d", 
                    expectedCount, actualCount));
            logger.info("✓ Window count matches expected: {}", expectedCount);
        }
        
        /**
         * Verifies alert is present
         * @param driver WebDriver instance
         */
        public static void verifyAlertPresent(WebDriver driver) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
                wait.until(ExpectedConditions.alertIsPresent());
                logger.info("✓ Alert is present");
            } catch (Exception e) {
                logger.error("✗ Alert is not present");
                throw new AssertionError("Alert should be present", e);
            }
        }
        
        /**
         * Verifies alert text
         * @param driver WebDriver instance
         * @param expectedAlertText Expected alert text
         */
        public static void verifyAlertText(WebDriver driver, String expectedAlertText) {
            try {
                String actualAlertText = driver.switchTo().alert().getText();
                Assert.assertEquals(actualAlertText, expectedAlertText, 
                    String.format("Alert text mismatch. Expected: '%s', Actual: '%s'", 
                        expectedAlertText, actualAlertText));
                logger.info("✓ Alert text matches expected: '{}'", expectedAlertText);
            } catch (Exception e) {
                logger.error("✗ Failed to verify alert text");
                throw new AssertionError("Failed to verify alert text", e);
            }
        }
    }
}

