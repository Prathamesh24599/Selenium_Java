package config_reader;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import config_reader.ConfigManager;
import pom.constant.Constants;

import java.time.Duration;

/**
 * DriverManager class to handle WebDriver initialization and configuration
 * Supports multiple browsers and configuration options
 */
public class Driver_Manager {
    
    private static final Logger logger = LoggerFactory.getLogger(Driver_Manager.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static ConfigManager config = ConfigManager.getInstance();
    
    /**
     * Initialize WebDriver based on browser type
     */
    public static WebDriver initializeDriver(String browserName) {
        logger.debug("Initializing {} driver", browserName);
        
        WebDriver driver;
        String browser = (browserName != null) ? browserName.toLowerCase() : config.getDefaultBrowser();
        
        switch (browser) {
            case Constants.CHROME:
                driver = createChromeDriver();
                break;
            case Constants.FIREFOX:
                driver = createFirefoxDriver();
                break;
            case Constants.EDGE:
                driver = createEdgeDriver();
                break;
            default:
                logger.error("Unsupported browser: {}", browser);
                throw new IllegalArgumentException("Browser not supported: " + browser);
        }
        
        // Configure driver settings
        configureDriver(driver);
        
        // Store in ThreadLocal for parallel execution
        driverThreadLocal.set(driver);
        
        logger.info("{} driver initialized successfully", browser);
        return driver;
    }
    
    /**
     * Create Chrome WebDriver with options
     */
    private static WebDriver createChromeDriver() {
        ChromeOptions chromeOptions = new ChromeOptions();
        
        // Add Chrome arguments from constants
        for (String arg : Constants.CHROME_ARGS) {
            chromeOptions.addArguments(arg);
        }
        
        // Add configuration-based options
        if (config.isHeadless()) {
            chromeOptions.addArguments("--headless");
            logger.debug("Chrome running in headless mode");
        }
        
        // Additional Chrome-specific configurations
        if (config.getBooleanProperty("chrome.disable.automation", true)) {
            chromeOptions.setExperimentalOption("useAutomationExtension", false);
            chromeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        }
        
        return new ChromeDriver(chromeOptions);
    }
    
    /**
     * Create Firefox WebDriver with options
     */
    private static WebDriver createFirefoxDriver() {
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        
        if (config.isHeadless()) {
            firefoxOptions.addArguments("--headless");
            logger.debug("Firefox running in headless mode");
        }
        
        return new FirefoxDriver(firefoxOptions);
    }
    
    /**
     * Create Edge WebDriver with options
     */
    private static WebDriver createEdgeDriver() {
        EdgeOptions edgeOptions = new EdgeOptions();
        
        if (config.isHeadless()) {
            edgeOptions.addArguments("--headless");
            logger.debug("Edge running in headless mode");
        }
        
        return new EdgeDriver(edgeOptions);
    }
    
    /**
     * Configure common driver settings
     */
    private static void configureDriver(WebDriver driver) {
        // Maximize window if configured
        if (config.shouldMaximizeBrowser()) {
            driver.manage().window().maximize();
        }
        
        // Clear cookies if configured
        if (config.getBooleanProperty("browser.clear.cookies", true)) {
            driver.manage().deleteAllCookies();
        }
        
        // Set timeouts
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getImplicitTimeout()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeout()));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(config.getIntProperty("timeout.script", 30)));
        
        logger.debug("Driver configured - Implicit Timeout: {}s, Page Load Timeout: {}s", 
                    config.getImplicitTimeout(), config.getPageLoadTimeout());
    }
    
    /**
     * Get current thread's driver
     */
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }
    
    /**
     * Quit driver and clean up
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                logger.debug("WebDriver closed successfully");
            } catch (Exception e) {
                logger.warn("Error closing WebDriver: {}", e.getMessage());
            } finally {
                driverThreadLocal.remove();
            }
        }
    }
    
    /**
     * Navigate to base URL
     */
    public static void navigateToBaseUrl() {
        WebDriver driver = getDriver();
        if (driver != null) {
            String url = config.getEnvironmentUrl();
            driver.get(url);
            logger.info("Successfully navigated to: {}", url);
        }
    }
}
