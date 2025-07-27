package novus.config.browser_option;


import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Firefox browser options builder
 */
public class FirefoxOptionsBuilder {
    private static final Logger logger = LoggerFactory.getLogger(FirefoxOptionsBuilder.class);
    
    private final BrowserConfigHelper configHelper;
    private final String browserName = "firefox";

    public FirefoxOptionsBuilder(BrowserConfigHelper configHelper) {
        this.configHelper = configHelper;
    }

    public FirefoxOptions build() {
        return build("defaultOptions");
    }

    public FirefoxOptions build(String optionsType) {
        FirefoxOptions options = new FirefoxOptions();

        try {
            // Apply arguments
            List<String> arguments = configHelper.getBrowserArguments(browserName, optionsType);
            if (!arguments.isEmpty()) {
                options.addArguments(arguments);
                logger.debug("Applied {} arguments to Firefox", arguments.size());
            }

            // Apply preferences
            Map<String, Object> preferences = configHelper.getBrowserPreferences(browserName, optionsType);
            preferences.forEach(options::addPreference);
            if (!preferences.isEmpty()) {
                logger.debug("Applied {} preferences to Firefox", preferences.size());
            }

            // Apply experimental options (if supported by Firefox)
            Map<String, Object> expOptions = configHelper.getExperimentalOptions(browserName, optionsType);
            // Firefox doesn't have experimental options like Chrome/Edge, but we can set capabilities
            expOptions.forEach(options::setCapability);
            if (!expOptions.isEmpty()) {
                logger.debug("Applied {} experimental options to Firefox", expOptions.size());
            }

        } catch (Exception e) {
            logger.error("Error building Firefox options for {}: {}", optionsType, e.getMessage(), e);
        }

        return options;
    }
}