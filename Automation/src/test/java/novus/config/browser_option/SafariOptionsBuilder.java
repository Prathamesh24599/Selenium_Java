package novus.config.browser_option;

import java.util.Map;

import novus.config.config_interfaces.BrowserOptions;
import novus.config.models.DriverConfiguration;

import org.openqa.selenium.safari.SafariOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Safari options builder
 */
public class SafariOptionsBuilder implements BrowserOptions<SafariOptions> {
	private static final Logger logger = LoggerFactory.getLogger(SafariOptionsBuilder.class);

	public static SafariOptions build(DriverConfiguration config) {
		return new SafariOptionsBuilder().buildOptions(config);
	}

	@Override
	public SafariOptions buildOptions(DriverConfiguration config) {
		SafariOptions options = new SafariOptions();

		// Configure Safari-specific options
		configureBasicOptions(options, config);

		// Apply custom capabilities
		applyCustomCapabilities(options, config.getCustomCapabilities());

		logger.debug("Safari options configured successfully");
		return options;
	}

	@Override
	public SafariOptions buildHeadlessOptions(DriverConfiguration config) {
		// Safari doesn't support headless mode
		logger.warn("Headless mode not supported for Safari, using regular options");
		return buildOptions(config);
	}

	@Override
	public SafariOptions buildMobileOptions(DriverConfiguration config, String deviceName) {
		// Safari doesn't support mobile emulation
		logger.warn("Mobile emulation not supported for Safari, using regular options");
		return buildOptions(config);
	}

	@Override
	public void applyCustomCapabilities(SafariOptions options, Map<String, Object> capabilities) {
		if (capabilities != null && !capabilities.isEmpty()) {
			capabilities.forEach((key, value) -> {
				options.setCapability(key, value);
				logger.debug("Applied custom Safari capability: {} = {}", key, value);
			});
		}
	}

	/**
	 * Configure basic Safari options
	 */
	private void configureBasicOptions(SafariOptions options, DriverConfiguration config) {
		// Enable automatic inspection if needed
		if (config.getCustomCapabilities().containsKey("automaticInspection")) {
			boolean automaticInspection = (Boolean) config.getCustomCapabilities().get("automaticInspection");
			options.setAutomaticInspection(automaticInspection);
		}

		// Enable automatic profiling if needed
		if (config.getCustomCapabilities().containsKey("automaticProfiling")) {
			boolean automaticProfiling = (Boolean) config.getCustomCapabilities().get("automaticProfiling");
			options.setAutomaticProfiling(automaticProfiling);
		}

		logger.debug("Safari basic options configured");
	}
}
