package novus.config.browser_option;


import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Chrome browser options builder
 */
public class ChromeOptionsBuilder {
    private static final Logger logger = LoggerFactory.getLogger(ChromeOptionsBuilder.class);
    
    private final BrowserConfigHelper configHelper;
    private final String browserName = "chrome";

    public ChromeOptionsBuilder(BrowserConfigHelper configHelper) {
        this.configHelper = configHelper;
    }

    public ChromeOptions build() {
        return build("defaultOptions");
    }

    public ChromeOptions build(String optionsType) {
        ChromeOptions options = new ChromeOptions();

        try {
            // Apply arguments
            List<String> arguments = configHelper.getBrowserArguments(browserName, optionsType);
            if (!arguments.isEmpty()) {
                options.addArguments(arguments);
                logger.debug("Applied {} arguments to Chrome", arguments.size());
            }

            // Apply preferences
            Map<String, Object> preferences = configHelper.getBrowserPreferences(browserName, optionsType);
            if (!preferences.isEmpty()) {
                options.setExperimentalOption("prefs", preferences);
                logger.debug("Applied {} preferences to Chrome", preferences.size());
            }

            // Apply experimental options
            Map<String, Object> expOptions = configHelper.getExperimentalOptions(browserName, optionsType);
            expOptions.forEach(options::setExperimentalOption);
            if (!expOptions.isEmpty()) {
                logger.debug("Applied {} experimental options to Chrome", expOptions.size());
            }

        } catch (Exception e) {
            logger.error("Error building Chrome options for {}: {}", optionsType, e.getMessage(), e);
        }

        return options;
    }

    /**
     * Build Chrome options with mobile emulation
     */
    public ChromeOptions buildWithMobileEmulation(String deviceName) {
        ChromeOptions options = build();

        Optional<Map<String, Object>> deviceConfig = configHelper.getMobileDeviceConfig(deviceName);
        if (deviceConfig.isPresent()) {
            options.setExperimentalOption("mobileEmulation", deviceConfig.get());
            logger.debug("Applied mobile emulation for device: {}", deviceName);
        } else {
            logger.warn("Mobile device configuration not found: {}", deviceName);
        }

        return options;
    }

    /**
     * Build Chrome options with network throttling
     */
    public ChromeOptions buildWithNetworkThrottling() {
        ChromeOptions options = build();

        if (configHelper.isNetworkThrottlingEnabled()) {
            Map<String, Object> networkConditions = configHelper.getNetworkConditions();
            if (!networkConditions.isEmpty()) {
                options.setExperimentalOption("networkConditions", networkConditions);
                logger.debug("Applied network throttling conditions");
            }
        }

        return options;
    }
}