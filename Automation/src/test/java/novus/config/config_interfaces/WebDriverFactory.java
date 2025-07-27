package novus.config.config_interfaces;

import java.util.List;

import org.openqa.selenium.WebDriver;

import novus.config.models.DriverConfiguration;
import pom.pages.DriverCreationException;

/**
 * WebDriver factory interface for different driver types
 */
public interface WebDriverFactory {
    WebDriver createDriver(DriverConfiguration config) throws DriverCreationException;
    boolean supportsDriverType(String driverType);
    void configureDriver(WebDriver driver, DriverConfiguration config);
    List<String> getSupportedBrowsers();
}