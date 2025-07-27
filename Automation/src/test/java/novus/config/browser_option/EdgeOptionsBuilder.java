package novus.config.browser_option;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import novus.config.config_interfaces.BrowserOptions;
import novus.config.models.DriverConfiguration;
import novus.config.models.ProxyConfiguration;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Edge options builder
 */
public class EdgeOptionsBuilder implements BrowserOptions<EdgeOptions> {
	private static final Logger logger = LoggerFactory.getLogger(EdgeOptionsBuilder.class);

	public static EdgeOptions build(DriverConfiguration config) {
		return new EdgeOptionsBuilder().buildOptions(config);
	}

	@Override
	public EdgeOptions buildOptions(DriverConfiguration config) {
		EdgeOptions options = new EdgeOptions();

		// Configure arguments (similar to Chrome)
		addArguments(options, config);

		// Configure preferences
		configurePreferences(options, config);

		// Configure proxy
		configureProxy(options, config);

		// Apply custom capabilities
		applyCustomCapabilities(options, config.getCustomCapabilities());

		logger.debug("Edge options configured successfully");
		return options;
	}

	@Override
	public EdgeOptions buildHeadlessOptions(DriverConfiguration config) {
		EdgeOptions options = buildOptions(config);
		options.addArguments("--headless=new");

		logger.debug("Edge headless options configured");
		return options;
	}

	@Override
	public EdgeOptions buildMobileOptions(DriverConfiguration config, String deviceName) {
		EdgeOptions options = config.isHeadless() ? buildHeadlessOptions(config) : buildOptions(config);

		// Edge supports mobile emulation similar to Chrome
		configureMobileEmulation(options, deviceName);

		logger.debug("Edge mobile options configured for device: {}", deviceName);
		return options;
	}

	@Override
	public void applyCustomCapabilities(EdgeOptions options, Map<String, Object> capabilities) {
		if (capabilities != null && !capabilities.isEmpty()) {
			capabilities.forEach((key, value) -> {
				options.setCapability(key, value);
				logger.debug("Applied custom Edge capability: {} = {}", key, value);
			});
		}
	}

	/**
	 * Add Edge arguments
	 */
	private void addArguments(EdgeOptions options, DriverConfiguration config) {
		List<String> arguments = new ArrayList<>();

		if (config.isHeadless()) {
			arguments.addAll(List.of("--headless=new", "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage"));
		}

		// Add common arguments
		arguments.addAll(List.of("--disable-blink-features=AutomationControlled", "--disable-extensions",
				"--disable-web-security", "--allow-running-insecure-content"));

		options.addArguments(arguments);
		logger.debug("Added {} Edge arguments", arguments.size());
	}

	/**
	 * Configure Edge preferences
	 */
	private void configurePreferences(EdgeOptions options, DriverConfiguration config) {
		Map<String, Object> preferences = new HashMap<>();

		// Download preferences
		preferences.put("download.default_directory", getDownloadDirectory());
		preferences.put("download.prompt_for_download", false);

		// Notification preferences
		preferences.put("profile.default_content_setting_values.notifications", 2);

		options.setExperimentalOption("prefs", preferences);
		logger.debug("Edge preferences configured");
	}

	/**
	 * Configure mobile emulation for Edge
	 */
	private void configureMobileEmulation(EdgeOptions options, String deviceName) {
		Map<String, Object> mobileEmulation = new HashMap<>();
		mobileEmulation.put("deviceName", deviceName);

		options.setExperimentalOption("mobileEmulation", mobileEmulation);
		logger.info("Edge mobile emulation configured for device: {}", deviceName);
	}

	/**
	 * Configure proxy for Edge
	 */
	private void configureProxy(EdgeOptions options, DriverConfiguration config) {
		ProxyConfiguration proxyConfig = config.getProxy();

		if (proxyConfig.isEnabled()) {
			Proxy proxy = new Proxy();
			proxy.setProxyType(Proxy.ProxyType.valueOf(proxyConfig.getProxyType()));
			proxy.setHttpProxy(proxyConfig.getHttpProxy());
			proxy.setSslProxy(proxyConfig.getSslProxy());

			options.setCapability(CapabilityType.PROXY, proxy);
			logger.info("Edge proxy configuration applied: {}", proxyConfig.getHttpProxy());
		}
	}

	/**
	 * Get download directory path
	 */
	private String getDownloadDirectory() {
		return System.getProperty("user.dir") + "/test-output/downloads";
	}
}
