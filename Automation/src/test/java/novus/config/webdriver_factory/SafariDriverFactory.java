package novus.config.webdriver_factory;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.safari.SafariOptions;

import novus.config.browser_option.BrowserConfigHelper;
import novus.config.browser_option.SafariOptionsBuilder;
import novus.config.models.DriverConfiguration;

/**
 * Safari-specific WebDriver factory
 */
public class SafariDriverFactory extends AbstractBrowserFactory {

	 public SafariDriverFactory(String remoteHubUrl, BrowserConfigHelper configHelper) {
	        super(remoteHubUrl, configHelper);
	    }

	@Override
	protected WebDriver createLocalDriver(DriverConfiguration config) throws Exception {
	    SafariOptionsBuilder builder = new SafariOptionsBuilder(configHelper);
	    SafariOptions safariOptions = builder.build(config.getBrowserName()); // Direct string
	    return new org.openqa.selenium.safari.SafariDriver(safariOptions);
	}

	@Override
	protected Object createCapabilities(DriverConfiguration config) {
	    SafariOptionsBuilder builder = new SafariOptionsBuilder(configHelper);
	    return builder.build(config.getBrowserName()); // Direct string
	}

	@Override
	public boolean supportsDriverType(String driverType) {
	    return "safari".equalsIgnoreCase(driverType);
	}

	@Override
	public List<String> getSupportedBrowsers() {
	    return List.of("safari");
	}

	@Override
	protected String getSupportedBrowser() {
	    return "safari";
	}
}