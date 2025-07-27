package novus.config.webdriver_factory;

import java.util.List;

import org.openqa.selenium.WebDriver;

import novus.config.browser_option.ChromeOptionsBuilder;
import novus.config.models.DriverConfiguration;

/**
 * Chrome-specific WebDriver factory
 */
class ChromeDriverFactory extends AbstractBrowserFactory {

	public ChromeDriverFactory(String remoteHubUrl) {
		super(remoteHubUrl);
	}

	@Override
	protected WebDriver createLocalDriver(DriverConfiguration config) throws Exception {
		var chromeOptions = ChromeOptionsBuilder.build(config);
		return new org.openqa.selenium.chrome.ChromeDriver(chromeOptions);
	}

	@Override
	protected Object createCapabilities(DriverConfiguration config) {
		return ChromeOptionsBuilder.build(config);
	}

	@Override
	public boolean supportsDriverType(String driverType) {
		return "chrome".equalsIgnoreCase(driverType);
	}

	@Override
	public List<String> getSupportedBrowsers() {
		return List.of("chrome");
	}

	@Override
	protected String getSupportedBrowser() {
		return "chrome";
	}
}
