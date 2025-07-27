package novus.config.browser_option;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import novus.config.config_interfaces.BrowserOptions;
import novus.config.models.DriverConfiguration;

import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Firefox options builder
 */
public class FirefoxOptionsBuilder implements BrowserOptions<FirefoxOptions> {
	private static final Logger logger = LoggerFactory.getLogger(FirefoxOptionsBuilder.class);

	public static FirefoxOptions build(DriverConfiguration config) {
		return new FirefoxOptionsBuilder().buildOptions(config);
	}

	@Override
	public FirefoxOptions buildOptions(DriverConfiguration config) {
		FirefoxOptions options = new FirefoxOptions();

		// Configure arguments
		addArguments(options, config);

		// Configure profile
		FirefoxProfile profile = createFirefoxProfile(config);
		options.setProfile(profile);

		// Configure logging
		configureLogging(options, config);

		// Apply custom capabilities
		applyCustomCapabilities(options, config.getCustomCapabilities());

		logger.debug("Firefox options configured successfully");
		return options;
	}

	@Override
	public FirefoxOptions buildHeadlessOptions(DriverConfiguration config) {
		FirefoxOptions options = buildOptions(config);
		options.addArguments("--headless");

		logger.debug("Firefox headless options configured");
		return options;
	}

	@Override
	public FirefoxOptions buildMobileOptions(DriverConfiguration config, String deviceName) {
		// Firefox doesn't support mobile emulation like Chrome
		logger.warn("Mobile emulation not supported for Firefox, using regular options");
		return config.isHeadless() ? buildHeadlessOptions(config) : buildOptions(config);
	}

	@Override
	public void applyCustomCapabilities(FirefoxOptions options, Map<String, Object> capabilities) {
		if (capabilities != null && !capabilities.isEmpty()) {
			capabilities.forEach((key, value) -> {
				options.setCapability(key, value);
				logger.debug("Applied custom Firefox capability: {} = {}", key, value);
			});
		}
	}

	/**
	 * Add Firefox arguments
	 */
	private void addArguments(FirefoxOptions options, DriverConfiguration config) {
		List<String> arguments = new ArrayList<>();

		if (config.isHeadless()) {
			arguments.add("--headless");
		}

		// Add common arguments
		arguments.addAll(List.of("--disable-blink-features=AutomationControlled", "--disable-extensions",
				"--no-sandbox", "--disable-dev-shm-usage"));

		options.addArguments(arguments);
		logger.debug("Added {} Firefox arguments", arguments.size());
	}

	/**
	 * Create Firefox profile with preferences
	 */
	private FirefoxProfile createFirefoxProfile(DriverConfiguration config) {
		FirefoxProfile profile = new FirefoxProfile();

		// Download preferences
		profile.setPreference("browser.download.dir", getDownloadDirectory());
		profile.setPreference("browser.download.folderList", 2);
		profile.setPreference("browser.download.useDownloadDir", true);
		profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
				"application/pdf,application/octet-stream,application/x-winexe,application/x-pdf,application/x-gzip");

		// Security preferences
		profile.setPreference("security.tls.insecure_fallback_hosts", "localhost");
		profile.setPreference("security.tls.unrestricted_rc4_fallback", true);

		// Performance preferences
		profile.setPreference("dom.webnotifications.enabled", false);
		profile.setPreference("media.volume_scale", "0.0");

		logger.debug("Firefox profile configured");
		return profile;
	}

	/**
	 * Configure Firefox logging
	 */
	private void configureLogging(FirefoxOptions options, DriverConfiguration config) {
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(LogType.DRIVER, Level.INFO);
		options.setCapability("goog:loggingPrefs", logPrefs);

		logger.debug("Firefox logging configured");
	}

	/**
	 * Get download directory path
	 */
	private String getDownloadDirectory() {
		return System.getProperty("user.dir") + "/test-output/downloads";
	}
}

