package pom.pages1;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Base_Page1 {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Actions actions;
    protected JavascriptExecutor jsExecutor;
    
    // Constructor
    public Base_Page1(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        this.actions = new Actions(driver);
        this.jsExecutor = (JavascriptExecutor) driver;
    }
    
    // Custom wait methods
    protected WebElement waitForElementToBeVisible(By locator, int timeoutSeconds) {
        WebDriverWait dynamicWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return dynamicWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    
    protected WebElement waitForElementToBeClickable(By locator, int timeoutSeconds) {
        WebDriverWait dynamicWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return dynamicWait.until(ExpectedConditions.elementToBeClickable(locator));
    }
    
    protected boolean waitForElementToBeInvisible(By locator, int timeoutSeconds) {
        WebDriverWait dynamicWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return dynamicWait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }
    
    protected List<WebElement> waitForElementsToBeVisible(By locator, int timeoutSeconds) {
        WebDriverWait dynamicWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return dynamicWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }
    
    // NEW: Added missing method used in Home_Page1
    protected WebElement waitForElementToBePresent(By locator, int timeoutSeconds) {
        WebDriverWait dynamicWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return dynamicWait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }
    
    // NEW: Added missing method used in Home_Page1
    protected boolean isElementPresent(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    // Common element interaction methods
    protected void clickElement(By locator) {
        try {
            WebElement element = waitForElementToBeClickable(locator, 10);
            element.click();
        } catch (TimeoutException e) {
            throw new RuntimeException("Element not clickable within timeout: " + locator.toString());
        }
    }
    
    protected void enterText(By locator, String text) {
        try {
            WebElement element = waitForElementToBeVisible(locator, 10);
            element.clear();
            element.sendKeys(text);
        } catch (TimeoutException e) {
            throw new RuntimeException("Element not found or not visible: " + locator.toString());
        }
    }
    
    protected String getText(By locator) {
    	try {
            WebElement element = waitForElementToBeVisible(locator, 10);
            String text = element.getText();
            if (text != null && !text.trim().isEmpty()) {
                return text.trim();
            }
            return "";
        } catch (TimeoutException e) {
            return "";
        }
    }
    
    protected String getAttribute(By locator, String attributeName) {
        try {
            WebElement element = waitForElementToBeVisible(locator, 10);
            return element.getAttribute(attributeName);
        } catch (TimeoutException e) {
            return "";
        }
    }
    
    // Element state checking methods - FIXED: Removed duplicate wait calls
    protected boolean isElementDisplayed(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            return element.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    protected boolean isElementEnabled(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            return element.isEnabled();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    protected boolean isElementSelected(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            return element.isSelected();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    // JavaScript executor methods
    protected void clickUsingJS(By locator) {
        WebElement element = driver.findElement(locator);
        jsExecutor.executeScript("arguments[0].click();", element);
    }
    
    protected void scrollToElement(By locator) {
        WebElement element = driver.findElement(locator);
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
    }
    
    protected void highlightElement(By locator) {
        WebElement element = driver.findElement(locator);
        jsExecutor.executeScript("arguments[0].style.border='3px solid red'", element);
    }
    
    // Dropdown handling
    protected void selectDropdownByText(By locator, String text) {
        WebElement dropdown = waitForElementToBeVisible(locator, 10);
        Select select = new Select(dropdown);
        select.selectByVisibleText(text);
    }
    
    protected void selectDropdownByValue(By locator, String value) {
        WebElement dropdown = waitForElementToBeVisible(locator, 10);
        Select select = new Select(dropdown);
        select.selectByValue(value);
    }
    
    protected void selectDropdownByIndex(By locator, int index) {
        WebElement dropdown = waitForElementToBeVisible(locator, 10);
        Select select = new Select(dropdown);
        select.selectByIndex(index);
    }
    
    // Mouse actions
    protected void hoverOverElement(By locator) {
        WebElement element = waitForElementToBeVisible(locator, 10);
        actions.moveToElement(element).perform();
    }
    
    protected void rightClickElement(By locator) {
        WebElement element = waitForElementToBeVisible(locator, 10);
        actions.contextClick(element).perform();
    }
    
    protected void doubleClickElement(By locator) {
        WebElement element = waitForElementToBeVisible(locator, 10);
        actions.doubleClick(element).perform();
    }
    
    // Frame handling
    protected void switchToFrame(By frameLocator) {
        WebElement frame = waitForElementToBeVisible(frameLocator, 10);
        driver.switchTo().frame(frame);
    }
    
    protected void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }
    
    // Window handling
    protected void switchToNewWindow() {
        String currentWindow = driver.getWindowHandle();
        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(currentWindow)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }
    }
    
    protected void closeCurrentWindowAndSwitchToMain() {
        String mainWindow = driver.getWindowHandles().iterator().next();
        driver.close();
        driver.switchTo().window(mainWindow);
    }
    
    // Alert handling
    protected void acceptAlert() {
        driver.switchTo().alert().accept();
    }
    
    protected void dismissAlert() {
        driver.switchTo().alert().dismiss();
    }
    
    protected String getAlertText() {
        return driver.switchTo().alert().getText();
    }
    
    // Page utilities
    protected void refreshPage() {
        driver.navigate().refresh();
    }
    
    protected void navigateBack() {
        driver.navigate().back();
    }
    
    protected void navigateForward() {
        driver.navigate().forward();
    }
    
    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
    
    protected String getPageTitle() {
        return driver.getTitle();
    }
    
    // Custom wait method for text to be present
    protected boolean waitForTextToBePresentInElement(By locator, String text, int timeoutSeconds) {
        WebDriverWait dynamicWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            return dynamicWait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
        } catch (TimeoutException e) {
            return false;
        }
    }
}