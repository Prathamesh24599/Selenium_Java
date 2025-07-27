package novus.config.browser_option;


import org.openqa.selenium.safari.SafariOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Safari browser options builder
 */
public class SafariOptionsBuilder {
    private static final Logger logger = LoggerFactory.getLogger(SafariOptionsBuilder.class);
    
    private final BrowserConfigHelper configHelper;
    private final String browserName = "safari";

    public SafariOptionsBuilder(BrowserConfigHelper configHelper) {
        this.configHelper = configHelper;
    }

    public SafariOptions build() {
        return build("defaultOptions");
    }

    public SafariOptions build(String optionsType) {
        SafariOptions options = new SafariOptions();

        try {
            // Safari has limited configuration options compared to other browsers
            
            // Apply experimental options as capabilities
            Map<String, Object> expOptions = configHelper.getExperimentalOptions(browserName, optionsType);
            expOptions.forEach(options::setCapability);
            if (!expOptions.isEmpty()) {
                logger.debug("Applied {} experimental options to Safari", expOptions.size());
            }

            // Note: Safari doesn't support arguments or preferences like Chrome/Edge/Firefox
            // Most configuration is done through system preferences
            
        } catch (Exception e) {
            logger.error("Error building Safari options for {}: {}", optionsType, e.getMessage(), e);
        }

        return options;
    }
}