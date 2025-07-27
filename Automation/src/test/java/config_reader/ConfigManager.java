package config_reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pom.constant.Constants;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * ConfigManager class to read and manage configuration properties
 * Singleton pattern implementation for configuration management
 */
public class ConfigManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static ConfigManager instance;
    private Properties properties;
    
    // Private constructor for Singleton pattern
    private ConfigManager() {
        loadProperties();
    }
    
    /**
     * Get singleton instance of ConfigManager
     */
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    /**
     * Load properties from config file
     */
    private void loadProperties() {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(Constants.CONFIG_FILE_PATH)) {
            properties.load(fis);
            logger.info("Configuration properties loaded successfully from: {}", Constants.CONFIG_FILE_PATH);
        } catch (IOException e) {
            logger.error("Failed to load configuration properties: {}", e.getMessage());
            // Load default properties if file not found
            loadDefaultProperties();
        }
    }
    
    /**
     * Load default properties if config file is not available
     */
    private void loadDefaultProperties() {
        logger.info("Loading default configuration properties");
        properties.setProperty("app.base.url", Constants.BASE_URL);
        properties.setProperty("browser.default", Constants.DEFAULT_BROWSER);
        properties.setProperty("timeout.implicit", String.valueOf(Constants.IMPLICIT_WAIT_TIMEOUT));
        properties.setProperty("timeout.explicit", String.valueOf(Constants.EXPLICIT_WAIT_TIMEOUT));
        properties.setProperty("timeout.page.load", String.valueOf(Constants.PAGE_LOAD_TIMEOUT));
        properties.setProperty("testdata.valid.username", Constants.VALID_USERNAME);
        properties.setProperty("testdata.valid.password", Constants.VALID_PASSWORD);
    }
    
    /**
     * Get property value by key
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    /**
     * Get property value by key with default value
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Get integer property value
     */
    public int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer value for property {}, using default: {}", key, defaultValue);
            return defaultValue;
        }
    }
    
    /**
     * Get boolean property value
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        return Boolean.parseBoolean(getProperty(key, String.valueOf(defaultValue)));
    }
    
    // Convenience methods for commonly used properties
    public String getBaseUrl() {
        return getProperty("app.base.url", Constants.BASE_URL);
    }
    
    public String getDefaultBrowser() {
        return getProperty("browser.default", Constants.DEFAULT_BROWSER);
    }
    
    public int getImplicitTimeout() {
        return getIntProperty("timeout.implicit", Constants.IMPLICIT_WAIT_TIMEOUT);
    }
    
    public int getExplicitTimeout() {
        return getIntProperty("timeout.explicit", Constants.EXPLICIT_WAIT_TIMEOUT);
    }
    
    public int getPageLoadTimeout() {
        return getIntProperty("timeout.page.load", Constants.PAGE_LOAD_TIMEOUT);
    }
    
    public String getValidUsername() {
        return getProperty("testdata.valid.username", Constants.VALID_USERNAME);
    }
    
    public String getValidPassword() {
        return getProperty("testdata.valid.password", Constants.VALID_PASSWORD);
    }
    
    public boolean isHeadless() {
        return getBooleanProperty("browser.headless", false);
    }
    
    public boolean shouldMaximizeBrowser() {
        return getBooleanProperty("browser.maximize", true);
    }
    
    public boolean shouldTakeScreenshotOnFailure() {
        return getBooleanProperty("test.screenshot.on.failure", true);
    }
    
    public String getScreenshotPath() {
        return getProperty("report.screenshot.path", Constants.SCREENSHOTS_PATH);
    }
    
    public String getReportPath() {
        return getProperty("report.extent.path", Constants.REPORTS_PATH);
    }
    
    public String getEnvironment() {
        return getProperty("environment", "QA");
    }
    
    /**
     * Get environment specific URL
     */
    public String getEnvironmentUrl() {
        String environment = getEnvironment().toLowerCase();
        return getProperty(environment + ".base.url", getBaseUrl());
    }

	
}