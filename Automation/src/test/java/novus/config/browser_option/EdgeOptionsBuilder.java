package novus.config.browser_option;


import org.openqa.selenium.edge.EdgeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Edge browser options builder
 */
public class EdgeOptionsBuilder {
    private static final Logger logger = LoggerFactory.getLogger(EdgeOptionsBuilder.class);
    
    private final BrowserConfigHelper configHelper;
    private final String browserName = "edge";

    public EdgeOptionsBuilder(BrowserConfigHelper configHelper) {
        this.configHelper = configHelper;
    }

    public EdgeOptions build() {
        return build("defaultOptions");
    }

    public EdgeOptions build(String optionsType) {
        EdgeOptions options = new EdgeOptions();

        try {
            // Apply arguments
            List<String> arguments = configHelper.getBrowserArguments(browserName, optionsType);
            if (!arguments.isEmpty()) {
                options.addArguments(arguments);
                logger.debug("Applied {} arguments to Edge", arguments.size());
            }

            // Apply preferences
            Map<String, Object> preferences = configHelper.getBrowserPreferences(browserName, optionsType);
            if (!preferences.isEmpty()) {
                options.setExperimentalOption("prefs", preferences);
                logger.debug("Applied {} preferences to Edge", preferences.size());
            }

            // Apply experimental options
            Map<String, Object> expOptions = configHelper.getExperimentalOptions(browserName, optionsType);
            expOptions.forEach(options::setExperimentalOption);
            if (!expOptions.isEmpty()) {
                logger.debug("Applied {} experimental options to Edge", expOptions.size());
            }

        } catch (Exception e) {
            logger.error("Error building Edge options for {}: {}", optionsType, e.getMessage(), e);
        }

        return options;
    }
}