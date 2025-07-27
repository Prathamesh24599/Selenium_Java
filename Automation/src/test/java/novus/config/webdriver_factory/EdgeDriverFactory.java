package novus.config.webdriver_factory;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeOptions;

import novus.config.browser_option.BrowserConfigHelper;
import novus.config.browser_option.EdgeOptionsBuilder;
import novus.config.models.DriverConfiguration;

/**
 * Edge-specific WebDriver factory
 */
class EdgeDriverFactory extends AbstractBrowserFactory {

	public EdgeDriverFactory(String remoteHubUrl, BrowserConfigHelper configHelper) {
        super(remoteHubUrl, configHelper);
    }

	@Override
	protected WebDriver createLocalDriver(DriverConfiguration config) throws Exception {
	    EdgeOptionsBuilder builder = new EdgeOptionsBuilder(configHelper);
	    EdgeOptions edgeOptions = builder.build(config.getBrowserName()); // Direct string
	    return new org.openqa.selenium.edge.EdgeDriver(edgeOptions);
	}

	@Override
	protected Object createCapabilities(DriverConfiguration config) {
	    EdgeOptionsBuilder builder = new EdgeOptionsBuilder(configHelper);
	    return builder.build(config.getBrowserName()); // Direct string
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