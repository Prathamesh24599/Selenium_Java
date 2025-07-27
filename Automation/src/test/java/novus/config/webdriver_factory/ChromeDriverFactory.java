package novus.config.webdriver_factory;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import novus.config.browser_option.ChromeOptionsBuilder;
import novus.config.browser_option.BrowserConfigHelper;
import novus.config.models.DriverConfiguration;

/**
 * Chrome-specific WebDriver factory
 */
class ChromeDriverFactory extends AbstractBrowserFactory {

    public ChromeDriverFactory(String remoteHubUrl, BrowserConfigHelper configHelper) {
        super(remoteHubUrl, configHelper);
    }

    @Override
    protected WebDriver createLocalDriver(DriverConfiguration config) throws Exception {
        ChromeOptionsBuilder builder = new ChromeOptionsBuilder(configHelper);
        ChromeOptions chromeOptions = builder.build("defaultOptions");
        return new org.openqa.selenium.chrome.ChromeDriver(chromeOptions);
    }

    @Override
    protected Object createCapabilities(DriverConfiguration config) {
        ChromeOptionsBuilder builder = new ChromeOptionsBuilder(configHelper);
        return builder.build("defaultOptions");
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