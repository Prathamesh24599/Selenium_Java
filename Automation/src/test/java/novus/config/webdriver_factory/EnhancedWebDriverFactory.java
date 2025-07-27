package novus.config.webdriver_factory;

import novus.config.config_interfaces.ConfigurationLoader;
import novus.config.config_interfaces.WebDriverFactory;
import novus.config.config_interfaces.CustomExceptions.*;
import novus.config.models.DriverConfiguration;
import novus.config.browser_option.BrowserConfigHelper;
import novus.config.config_loader.ConfigurationPropertiesProvider;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openqa.selenium.Capabilities;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enhanced WebDriver factory with support for multiple browsers and remote
 * execution
 */
public class EnhancedWebDriverFactory implements WebDriverFactory {
	private static final Logger logger = LoggerFactory.getLogger(EnhancedWebDriverFactory.class);

	private final Map<String, WebDriverFactory> browserFactories;
	private final String remoteHubUrl;
	private final BrowserConfigHelper configHelper;

	public EnhancedWebDriverFactory(String remoteHubUrl) {
		this(remoteHubUrl, createDefaultConfigHelper());
	}

	public EnhancedWebDriverFactory(String remoteHubUrl, BrowserConfigHelper configHelper) {
		this.remoteHubUrl = remoteHubUrl;
		this.configHelper = configHelper;
		this.browserFactories = initializeBrowserFactories();
	}

	public EnhancedWebDriverFactory(String remoteHubUrl, ConfigurationPropertiesProvider configProvider) {
		this.remoteHubUrl = remoteHubUrl;
		this.configHelper = new BrowserConfigHelper(configProvider);
		this.browserFactories = initializeBrowserFactories();
	}

	@Override
	public WebDriver createDriver(DriverConfiguration config) throws DriverCreationException {
		long startTime = System.currentTimeMillis();
		String browserName = config.getBrowserName().toLowerCase();

		logger.info("Creating {} driver - Remote: {}, Headless: {}, Mobile: {}", browserName, config.isRemote(),
				config.isHeadless(), config.getMobileDevice());

		try {
			WebDriverFactory factory = getBrowserFactory(browserName);
			WebDriver driver = factory.createDriver(config);

			configureDriver(driver, config);

			long creationTime = System.currentTimeMillis() - startTime;
			logger.info("Driver created successfully in {}ms for thread: {}", creationTime,
					Thread.currentThread().threadId());

			return driver;

		} catch (Exception e) {
			logger.error("Failed to create WebDriver for browser: {}", browserName, e);
			throw new DriverCreationException("WebDriver creation failed for browser: " + browserName, e);
		}
	}

	@Override
	public boolean supportsDriverType(String driverType) {
		return browserFactories.containsKey(driverType.toLowerCase());
	}

	@Override
	public void configureDriver(WebDriver driver, DriverConfiguration config) {
		logger.debug("Configuring driver settings...");

		// Configure timeouts
		var timeouts = config.getTimeouts();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeouts.getImplicitTimeout()))
				.pageLoadTimeout(Duration.ofSeconds(timeouts.getPageLoadTimeout()))
				.scriptTimeout(Duration.ofSeconds(timeouts.getScriptTimeout()));

		// Maximize window if not headless and not mobile
		if (!config.isHeadless() && config.getMobileDevice().isEmpty()) {
			try {
				driver.manage().window().maximize();
			} catch (Exception e) {
				logger.warn("Failed to maximize window: {}", e.getMessage());
			}
		}

		logger.debug("Driver configuration completed");
	}

	@Override
	public List<String> getSupportedBrowsers() {
		return List.copyOf(browserFactories.keySet());
	}

	/**
	 * Initialize browser-specific factories
	 */
	private Map<String, WebDriverFactory> initializeBrowserFactories() {
		Map<String, WebDriverFactory> factories = new ConcurrentHashMap<>();

		factories.put("chrome", new ChromeDriverFactory(remoteHubUrl, configHelper));
		factories.put("firefox", new FirefoxDriverFactory(remoteHubUrl, configHelper));
		factories.put("edge", new EdgeDriverFactory(remoteHubUrl, configHelper));
		factories.put("safari", new SafariDriverFactory(remoteHubUrl, configHelper));

		logger.info("Initialized {} browser factories: {}", factories.size(), factories.keySet());
		return factories;
	}

	/**
	 * Get browser-specific factory
	 */
	private WebDriverFactory getBrowserFactory(String browserName) throws DriverCreationException {
		WebDriverFactory factory = browserFactories.get(browserName);
		if (factory == null) {
			throw new DriverCreationException("No factory found for browser: " + browserName);
		}
		return factory;
	}

	/**
	 * Create a default BrowserConfigHelper instance
	 */
	private static BrowserConfigHelper createDefaultConfigHelper() {
		try {
			// FIXME: This should not be used - instead pass ConfigurationPropertiesProvider 
			// from the calling code that already has it initialized
			logger.warn("Using default config helper - ConfigurationPropertiesProvider should be injected");
			return null;
			
		} catch (Exception e) {
			logger.error("Failed to create default BrowserConfigHelper: {}", e.getMessage(), e);
			return null;
		}
	}
}

/**
 * Abstract base class for browser-specific factories
 */
abstract class AbstractBrowserFactory implements WebDriverFactory {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected final String remoteHubUrl;
	protected final BrowserConfigHelper configHelper;

	protected AbstractBrowserFactory(String remoteHubUrl, BrowserConfigHelper configHelper) {
		this.remoteHubUrl = remoteHubUrl;
		this.configHelper = configHelper;
	}

	@Override
	public WebDriver createDriver(DriverConfiguration config) throws DriverCreationException {
		try {
			if (config.isRemote()) {
				return createRemoteDriver(config);
			} else {
				return createLocalDriver(config);
			}
		} catch (Exception e) {
			logger.error("Failed to create {} driver", getSupportedBrowser(), e);
			throw new DriverCreationException("Driver creation failed", e);
		}
	}

	@Override
	public void configureDriver(WebDriver driver, DriverConfiguration config) {
		// Default implementation - can be overridden by specific factories
	}

	/**
	 * Create local WebDriver instance
	 */
	protected abstract WebDriver createLocalDriver(DriverConfiguration config) throws Exception;

	/**
	 * Create remote WebDriver instance
	 */
	protected WebDriver createRemoteDriver(DriverConfiguration config) throws Exception {
		URI hubUri = new URI(remoteHubUrl);
		URL hubUrl = hubUri.toURL();
		Capabilities capabilities = (Capabilities) createCapabilities(config);
		RemoteWebDriver driver = new RemoteWebDriver(hubUrl, capabilities);

		configureRemoteDriver(driver, config);

		return driver;
	}

	/**
	 * Create browser-specific capabilities
	 */
	protected abstract Object createCapabilities(DriverConfiguration config);

	/**
	 * Configure remote driver specific settings
	 */
	protected void configureRemoteDriver(RemoteWebDriver driver, DriverConfiguration config) {
		// Default implementation - can be overridden
		logger.debug("Remote driver configured for session: {}", driver.getSessionId());
	}

	/**
	 * Get supported browser name
	 */
	protected abstract String getSupportedBrowser();
}