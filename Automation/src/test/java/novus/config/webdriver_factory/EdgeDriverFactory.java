package novus.config.webdriver_factory;

import java.util.List;

import org.openqa.selenium.WebDriver;

import novus.config.browser_option.EdgeOptionsBuilder;
import novus.config.models.DriverConfiguration;

/**
 * Edge-specific WebDriver factory
 */
class EdgeDriverFactory extends AbstractBrowserFactory {

	public EdgeDriverFactory(String remoteHubUrl) {
		super(remoteHubUrl);
	}

	@Override
	protected WebDriver createLocalDriver(DriverConfiguration config) throws Exception {
		var edgeOptions = EdgeOptionsBuilder.build(config);
		return new org.openqa.selenium.edge.EdgeDriver(edgeOptions);
	}

	@Override
	protected Object createCapabilities(DriverConfiguration config) {
		return EdgeOptionsBuilder.build(config);
	}

	@Override
	public boolean supportsDriverType(String driverType) {
		return "edge".equalsIgnoreCase(driverType);
	}

	@Override
	public List<String> getSupportedBrowsers() {
		return List.of("edge");
	}

	@Override
	protected String getSupportedBrowser() {
		return "edge";
	}
}
