package novus.config.webdriver_factory;

import java.util.List;

import org.openqa.selenium.WebDriver;

import novus.config.browser_option.SafariOptionsBuilder;
import novus.config.models.DriverConfiguration;

/**
 * Safari-specific WebDriver factory
 */
public class SafariDriverFactory extends AbstractBrowserFactory {

	public SafariDriverFactory(String remoteHubUrl) {
		super(remoteHubUrl);
	}

	@Override
	protected WebDriver createLocalDriver(DriverConfiguration config) throws Exception {
		var safariOptions = SafariOptionsBuilder.build(config);
		return new org.openqa.selenium.safari.SafariDriver(safariOptions);
	}

	@Override
	protected Object createCapabilities(DriverConfiguration config) {
		return SafariOptionsBuilder.build(config);
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

