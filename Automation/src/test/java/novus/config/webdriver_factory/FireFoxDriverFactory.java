package novus.config.webdriver_factory;

import java.util.List;

import org.openqa.selenium.WebDriver;

import novus.config.browser_option.FirefoxOptionsBuilder;
import novus.config.models.DriverConfiguration;

/**
 * Firefox-specific WebDriver factory
 */
class FirefoxDriverFactory extends AbstractBrowserFactory {

	public FirefoxDriverFactory(String remoteHubUrl) {
		super(remoteHubUrl);
	}

	@Override
	protected WebDriver createLocalDriver(DriverConfiguration config) throws Exception {
		var firefoxOptions = FirefoxOptionsBuilder.build(config);
		return new org.openqa.selenium.firefox.FirefoxDriver(firefoxOptions);
	}

	@Override
	protected Object createCapabilities(DriverConfiguration config) {
		return FirefoxOptionsBuilder.build(config);
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


