package novus.config.webdriver_factory;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import novus.config.browser_option.BrowserConfigHelper;
import novus.config.browser_option.FirefoxOptionsBuilder;
import novus.config.models.DriverConfiguration;

/**
 * Firefox-specific WebDriver factory
 */
class FirefoxDriverFactory extends AbstractBrowserFactory {

	public FirefoxDriverFactory(String remoteHubUrl, BrowserConfigHelper configHelper) {
        super(remoteHubUrl, configHelper);
    }

	@Override
	protected WebDriver createLocalDriver(DriverConfiguration config) throws Exception {
		// Create instance of FirefoxOptionsBuilder and pass the browser type as String
		FirefoxOptionsBuilder builder = new FirefoxOptionsBuilder(configHelper);
		FirefoxOptions firefoxOptions = builder.build(config.getBrowserName()); // or just "firefox"
		return new org.openqa.selenium.firefox.FirefoxDriver(firefoxOptions);
	}

	@Override
	protected Object createCapabilities(DriverConfiguration config) {
		// Create instance and pass browser type as String
		FirefoxOptionsBuilder builder = new FirefoxOptionsBuilder(configHelper);
		return builder.build(config.getBrowserName()); // or just "firefox"
	}

	@Override
	public boolean supportsDriverType(String driverType) {
		return "firefox".equalsIgnoreCase(driverType);
	}

	@Override
	public List<String> getSupportedBrowsers() {
		return List.of("firefox");
	}

	@Override
	protected String getSupportedBrowser() {
	    return "firefox";
	}
}