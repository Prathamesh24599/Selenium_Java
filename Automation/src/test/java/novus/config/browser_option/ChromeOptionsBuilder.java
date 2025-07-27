// ===============================
// BROWSER OPTIONS BUILDERS
// ===============================

package novus.config.browser_option;

import novus.config.config_interfaces.BrowserOptions;
import novus.config.models.DriverConfiguration;
import novus.config.models.PerformanceConfiguration;
import novus.config.models.ProxyConfiguration;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.safari.SafariOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Chrome options builder with comprehensive configuration support
 */
public class ChromeOptionsBuilder implements BrowserOptions<ChromeOptions> {
	private static final Logger logger = LoggerFactory.getLogger(ChromeOptionsBuilder.class);

	// Chrome-specific constants
	private static final String USER_DATA_DIR_PREFIX = "--user-data-dir=";
	private static final String DOWNLOAD_DIR_PREF = "download.default_directory";
	private static final String DISABLE_NOTIFICATIONS_PREF = "profile.default_content_setting_values.notifications";

	public static ChromeOptions build(DriverConfiguration config) {
		return new ChromeOptionsBuilder().buildOptions(config);
	}

	@Override
	public ChromeOptions buildOptions(DriverConfiguration config) {
		ChromeOptions options = new ChromeOptions();

		// Configure arguments
		addArguments(options, config);

		// Configure preferences
		configurePreferences(options, config);

		// Configure experimental options
		configureExperimentalOptions(options, config);

		// Configure mobile emulation if specified
		configureMobileEmulation(options, config);

		// Configure performance monitoring
		configurePerformanceMonitoring(options, config);

		// Configure logging
		configureLogging(options, config);

		// Configure proxy
		configureProxy(options, config);

		// Apply custom capabilities
		applyCustomCapabilities(options, config.getCustomCapabilities());

		logger.debug("Chrome options configured successfully");
		return options;
	}

	@Override
	public ChromeOptions buildHeadlessOptions(DriverConfiguration config) {
		ChromeOptions options = buildOptions(config);

		// Add headless-specific arguments
		options.addArguments("--headless=new", "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");

		logger.debug("Chrome headless options configured");
		return options;
	}

	@Override
	public ChromeOptions buildMobileOptions(DriverConfiguration config, String deviceName) {
		ChromeOptions options = config.isHeadless() ? buildHeadlessOptions(config) : buildOptions(config);

		configureMobileDevice(options, deviceName);

		logger.debug("Chrome mobile options configured for device: {}", deviceName);
		return options;
	}

	@Override
	public void applyCustomCapabilities(ChromeOptions options, Map<String, Object> capabilities) {
		if (capabilities != null && !capabilities.isEmpty()) {
			capabilities.forEach((key, value) -> {
				options.setCapability(key, value);
				logger.debug("Applied custom capability: {} = {}", key, value);
			});
		}
	}

	/**
	 * Add Chrome arguments based on configuration
	 */
	private void addArguments(ChromeOptions options, DriverConfiguration config) {
		List<String> arguments = new ArrayList<>();

		if (config.isHeadless()) {
			arguments.addAll(getHeadlessArguments());
		} else {
			arguments.addAll(getDefaultArguments());
		}

		// Add performance arguments
		arguments.addAll(getPerformanceArguments());

		// Add security arguments
		arguments.addAll(getSecurityArguments(config));

		options.addArguments(arguments);
		logger.debug("Added {} Chrome arguments", arguments.size());
	}

	/**
	 * Get default Chrome arguments
	 */
	private List<String> getDefaultArguments() {
		return List.of("--disable-blink-features=AutomationControlled", "--disable-extensions-file-access-check",
				"--disable-extensions-http-throttling", "--disable-extensions-https-enforced", "--disable-web-security",
				"--allow-running-insecure-content", "--disable-features=TranslateUI",
				"--disable-ipc-flooding-protection", "--disable-renderer-backgrounding",
				"--disable-backgrounding-occluded-windows", "--disable-field-trial-config",
				"--disable-back-forward-cache");
	}

	/**
	 * Get headless-specific arguments
	 */
	private List<String> getHeadlessArguments() {
		List<String> args = new ArrayList<>(getDefaultArguments());
		args.addAll(List.of("--headless=new", "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage",
				"--remote-debugging-port=0", "--disable-software-rasterizer"));
		return args;
	}

	/**
	 * Get performance optimization arguments
	 */
	private List<String> getPerformanceArguments() {
		return List.of("--aggressive-cache-discard", "--memory-pressure-off", "--max_old_space_size=4096",
				"--disable-background-timer-throttling", "--disable-renderer-backgrounding",
				"--disable-backgrounding-occluded-windows");
	}

	/**
	 * Get security-related arguments
	 */
	private List<String> getSecurityArguments(DriverConfiguration config) {
		List<String> args = new ArrayList<>();

		if (!config.getSecurity().isCertificateValidationEnabled()) {
			args.addAll(List.of("--ignore-certificate-errors", "--ignore-ssl-errors",
					"--ignore-certificate-errors-spki-list", "--ignore-ssl-errors-ignore-cert-errors"));
		}

		if (config.getSecurity().isInsecureCertsAllowed()) {
			args.add("--allow-running-insecure-content");
		}

		return args;
	}

	/**
	 * Configure Chrome preferences
	 */
	private void configurePreferences(ChromeOptions options, DriverConfiguration config) {
		Map<String, Object> preferences = new HashMap<>();

		// Download preferences
		preferences.put(DOWNLOAD_DIR_PREF, getDownloadDirectory());
		preferences.put("download.prompt_for_download", false);
		preferences.put("download.directory_upgrade", true);
		preferences.put("safebrowsing.enabled", false);

		// Notification preferences
		preferences.put(DISABLE_NOTIFICATIONS_PREF, 2);
		preferences.put("profile.default_content_settings.popups", 0);

		// Security preferences
		preferences.put("profile.password_manager_enabled", false);
		preferences.put("credentials_enable_service", false);

		// Performance preferences
		preferences.put("profile.default_content_setting_values.media_stream", 1);

		options.setExperimentalOption("prefs", preferences);
		logger.debug("Chrome preferences configured");
	}

	/**
	 * Configure experimental options
	 */
	private void configureExperimentalOptions(ChromeOptions options, DriverConfiguration config) {
		// Exclude automation switches
		options.setExperimentalOption("excludeSwitches", List.of("enable-automation", "enable-logging"));

		// Add automation extension
		options.setExperimentalOption("useAutomationExtension", false);

		// Configure local state
		Map<String, Object> localState = new HashMap<>();
		localState.put("browser.enabled_labs_experiments",
				List.of("same-site-by-default-cookies@2", "cookies-without-same-site-must-be-secure@2"));
		options.setExperimentalOption("localState", localState);

		logger.debug("Chrome experimental options configured");
	}

	/**
	 * Configure mobile emulation
	 */
	private void configureMobileEmulation(ChromeOptions options, DriverConfiguration config) {
		String mobileDevice = config.getMobileDevice();
		if (mobileDevice != null && !mobileDevice.isEmpty()) {
			configureMobileDevice(options, mobileDevice);
		}
	}

	/**
	 * Configure specific mobile device
	 */
	private void configureMobileDevice(ChromeOptions options, String deviceName) {
		Map<String, Object> mobileEmulation = new HashMap<>();

		// Predefined devices
		switch (deviceName.toLowerCase()) {
		case "iphone12":
			configureIPhone12(mobileEmulation);
			break;
		case "pixel5":
			configurePixel5(mobileEmulation);
			break;
		case "ipadpro":
			configureIPadPro(mobileEmulation);
			break;
		default:
			// Use device name directly
			mobileEmulation.put("deviceName", deviceName);
		}

		options.setExperimentalOption("mobileEmulation", mobileEmulation);
		logger.info("Mobile emulation configured for device: {}", deviceName);
	}

	/**
	 * Configure iPhone 12 emulation
	 */
	private void configureIPhone12(Map<String, Object> mobileEmulation) {
		Map<String, Object> deviceMetrics = new HashMap<>();
		deviceMetrics.put("width", 390);
		deviceMetrics.put("height", 844);
		deviceMetrics.put("pixelRatio", 3.0);

		mobileEmulation.put("deviceMetrics", deviceMetrics);
		mobileEmulation.put("userAgent", "Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15");
	}

	/**
	 * Configure Google Pixel 5 emulation
	 */
	private void configurePixel5(Map<String, Object> mobileEmulation) {
		Map<String, Object> deviceMetrics = new HashMap<>();
		deviceMetrics.put("width", 393);
		deviceMetrics.put("height", 851);
		deviceMetrics.put("pixelRatio", 2.75);

		mobileEmulation.put("deviceMetrics", deviceMetrics);
		mobileEmulation.put("userAgent", "Mozilla/5.0 (Linux; Android 11; Pixel 5) AppleWebKit/537.36");
	}

	/**
	 * Configure iPad Pro emulation
	 */
	private void configureIPadPro(Map<String, Object> mobileEmulation) {
		Map<String, Object> deviceMetrics = new HashMap<>();
		deviceMetrics.put("width", 1024);
		deviceMetrics.put("height", 1366);
		deviceMetrics.put("pixelRatio", 2.0);

		mobileEmulation.put("deviceMetrics", deviceMetrics);
		mobileEmulation.put("userAgent", "Mozilla/5.0 (iPad; CPU OS 15_0 like Mac OS X) AppleWebKit/605.1.15");
	}

	/**
	 * Configure performance monitoring
	 */
	private void configurePerformanceMonitoring(ChromeOptions options, DriverConfiguration config) {
		PerformanceConfiguration perfConfig = config.getPerformance();

		if (perfConfig.isNetworkThrottlingEnabled()) {
			Map<String, Object> networkConditions = perfConfig.getNetworkConditions();
			if (!networkConditions.isEmpty()) {
				options.setExperimentalOption("networkConditions", networkConditions);
				logger.debug("Network throttling configured");
			}
		}
	}

	/**
	 * Configure logging preferences
	 */
	private void configureLogging(ChromeOptions options, DriverConfiguration config) {
		LoggingPreferences logPrefs = new LoggingPreferences();

		// Browser logs
		logPrefs.enable(LogType.BROWSER, Level.INFO);

		// Driver logs
		logPrefs.enable(LogType.DRIVER, Level.INFO);

		// Performance logs if enabled
		if (config.getPerformance().isLoggingEnabled()) {
			logPrefs.enable(LogType.PERFORMANCE, Level.INFO);
		}

		options.setCapability("goog:loggingPrefs", logPrefs);
		logger.debug("Chrome logging configured");
	}

	/**
	 * Configure proxy settings
	 */
	private void configureProxy(ChromeOptions options, DriverConfiguration config) {
		ProxyConfiguration proxyConfig = config.getProxy();

		if (proxyConfig.isEnabled()) {
			Proxy proxy = new Proxy();
			proxy.setProxyType(Proxy.ProxyType.valueOf(proxyConfig.getProxyType()));
			proxy.setHttpProxy(proxyConfig.getHttpProxy());
			proxy.setSslProxy(proxyConfig.getSslProxy());
			proxy.setFtpProxy(proxyConfig.getFtpProxy());
			proxy.setNoProxy(proxyConfig.getNoProxy());

			options.setCapability(CapabilityType.PROXY, proxy);
			logger.info("Proxy configuration applied: {}", proxyConfig.getHttpProxy());
		}
	}

	/**
	 * Get download directory path
	 */
	private String getDownloadDirectory() {
		return System.getProperty("user.dir") + "/test-output/downloads";
	}
}
