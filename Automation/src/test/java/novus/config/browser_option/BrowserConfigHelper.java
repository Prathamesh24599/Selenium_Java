package novus.config.browser_option;

import com.fasterxml.jackson.databind.JsonNode;

import novus.config.config_loader.ConfigurationPropertiesProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Simple browser configuration helper that uses existing configuration infrastructure
 */
public class BrowserConfigHelper {
    private static final Logger logger = LoggerFactory.getLogger(BrowserConfigHelper.class);
    
    private final ConfigurationPropertiesProvider configProvider;
    
    public BrowserConfigHelper(ConfigurationPropertiesProvider configProvider) {
        this.configProvider = configProvider;
    }
    
    /**
     * Get browser arguments list
     * Path: web.{browserName}.{optionsType}.args
     */
    public List<String> getBrowserArguments(String browserName, String optionsType) {
        String configPath = String.format("web.%s.%s.args", browserName, optionsType);
        
        JsonNode argsNode = configProvider.getConfigNode(configPath);
        List<String> args = new ArrayList<>();
        
        if (argsNode != null && argsNode.isArray()) {
            argsNode.forEach(arg -> {
                if (!arg.isNull()) {
                    args.add(arg.asText());
                }
            });
            logger.debug("📝 Loaded {} arguments for {}.{}", args.size(), browserName, optionsType);
        } else {
            logger.warn("⚠️ No arguments found for {}.{}", browserName, optionsType);
        }
        
        return args;
    }
    
    /**
     * Get browser preferences map
     * Path: web.{browserName}.{optionsType}.prefs
     */
    public Map<String, Object> getBrowserPreferences(String browserName, String optionsType) {
        String configPath = String.format("web.%s.%s.prefs", browserName, optionsType);
        
        JsonNode prefsNode = configProvider.getConfigNode(configPath); // Fixed variable name
        Map<String, Object> prefs = new HashMap<>();
        
        if (prefsNode != null && prefsNode.isObject()) { // Fixed: removed Optional, use direct null check
            prefsNode.fields().forEachRemaining(entry -> {
                String key = entry.getKey();
                JsonNode value = entry.getValue();
                prefs.put(key, convertJsonValue(value));
            });
            logger.debug("🔧 Loaded {} preferences for {}.{}", prefs.size(), browserName, optionsType);
        } else {
            logger.warn("⚠️  No preferences found for {}.{}", browserName, optionsType);
        }
        
        return prefs;
    }

    /**
     * Get experimental options
     * Path: web.{browserName}.{optionsType}.experimentalOptions
     */
    public Map<String, Object> getExperimentalOptions(String browserName, String optionsType) {
        String configPath = String.format("web.%s.%s.experimentalOptions", browserName, optionsType);
        
        JsonNode expNode = configProvider.getConfigNode(configPath); // Fixed: removed Optional wrapper
        Map<String, Object> options = new HashMap<>();
        
        if (expNode != null && expNode.isObject()) { // Fixed: direct null check
            expNode.fields().forEachRemaining(entry -> {
                String key = entry.getKey();
                JsonNode value = entry.getValue();
                options.put(key, convertJsonValue(value));
            });
            logger.debug("⚗️  Loaded {} experimental options for {}.{}", options.size(), browserName, optionsType);
        }
        
        return options;
    }

    /**
     * Get mobile device configuration
     * Path: web.chrome.mobileEmulation.devices.{deviceName} or web.chrome.mobileEmulation.customDevices.{deviceName}
     */
    public Optional<Map<String, Object>> getMobileDeviceConfig(String deviceName) {
        // Try predefined devices first
        String devicesPath = String.format("web.chrome.mobileEmulation.devices.%s", deviceName);
        JsonNode deviceNode = configProvider.getConfigNode(devicesPath); // Fixed: removed Optional wrapper
        
        // If not found, try custom devices
        if (deviceNode == null) { // Fixed: direct null check
            String customPath = String.format("web.chrome.mobileEmulation.customDevices.%s", deviceName);
            deviceNode = configProvider.getConfigNode(customPath);
        }
        
        if (deviceNode != null) { // Fixed: direct null check
            Map<String, Object> deviceConfig = convertJsonNodeToMap(deviceNode);
            logger.debug("📱 Loaded mobile device configuration for '{}'", deviceName);
            return Optional.of(deviceConfig);
        } else {
            logger.warn("⚠️  Mobile device '{}' not found", deviceName);
            return Optional.empty();
        }
    }

    /**
     * Get network conditions
     * Path: web.chrome.performance.networkConditions
     */
    public Map<String, Object> getNetworkConditions() {
        String configPath = "web.chrome.performance.networkConditions";
        
        JsonNode networkNode = configProvider.getConfigNode(configPath); // Fixed: removed Optional wrapper
        Map<String, Object> conditions = new HashMap<>();
        
        if (networkNode != null) { // Fixed: direct null check
            conditions = convertJsonNodeToMap(networkNode);
            logger.debug("🌐 Loaded network conditions");
        } else {
            logger.warn("⚠️  Network conditions not found");
        }
        
        return conditions;
    }

    /**
     * Check if network throttling is enabled
     * Path: web.chrome.performance.enableNetworkThrottling
     */
    public boolean isNetworkThrottlingEnabled() {
        return configProvider.getBooleanProperty("", "web.chrome.performance.enableNetworkThrottling", false);
    }

    /**
     * Get remote capabilities
     * Combines web.remoteCapabilities.common and web.remoteCapabilities.browserSpecific.{browserName}
     */
    public Map<String, Object> getRemoteCapabilities(String browserName) {
        Map<String, Object> capabilities = new HashMap<>();
        
        // Add common capabilities
        JsonNode commonNode = configProvider.getConfigNode("web.remoteCapabilities.common"); // Fixed: removed Optional wrapper
        if (commonNode != null) { // Fixed: direct null check
            capabilities.putAll(convertJsonNodeToMap(commonNode));
            logger.debug("🔗 Loaded common remote capabilities");
        }
        
        // Add browser-specific capabilities (overwrites common if same key exists)
        String browserPath = String.format("web.remoteCapabilities.browserSpecific.%s", browserName);
        JsonNode browserNode = configProvider.getConfigNode(browserPath); // Fixed: removed Optional wrapper
        if (browserNode != null) { // Fixed: direct null check
            capabilities.putAll(convertJsonNodeToMap(browserNode));
            logger.debug("🔗 Loaded {} remote capabilities", browserName);
        }
        
        return capabilities;
    }

    /**
     * Get entire browser configuration
     * Path: web.{browserName}
     */
    public Optional<JsonNode> getBrowserConfiguration(String browserName) {
        String configPath = String.format("web.%s", browserName);
        JsonNode node = configProvider.getConfigNode(configPath); // Fixed: get JsonNode directly
        return Optional.ofNullable(node); // Fixed: wrap in Optional if needed
    }

    /**
     * Check if browser configuration exists
     */
    public boolean hasBrowserConfiguration(String browserName) {
        // Fixed: Assuming you need to implement this method or use a different approach
        // Option 1: Check if the configuration node exists
        JsonNode node = configProvider.getConfigNode(String.format("web.%s", browserName));
        return node != null;
        
        // Option 2: If hasProperty method exists with different signature, use:
        // return configProvider.hasProperty("web." + browserName);
    }

    /**
     * Convert JsonNode value to appropriate Java type
     */
    private Object convertJsonValue(JsonNode value) {
        if (value.isBoolean()) {
            return value.asBoolean();
        } else if (value.isInt()) {
            return value.asInt();
        } else if (value.isLong()) {
            return value.asLong();
        } else if (value.isDouble()) {
            return value.asDouble();
        } else if (value.isArray()) {
            List<String> list = new ArrayList<>();
            value.forEach(item -> list.add(item.asText()));
            return list;
        } else if (value.isObject()) {
            return convertJsonNodeToMap(value);
        } else {
            return value.asText();
        }
    }

    /**
     * Convert JsonNode to Map recursively
     */
    private Map<String, Object> convertJsonNodeToMap(JsonNode node) {
        Map<String, Object> result = new HashMap<>();
        
        if (node != null && node.isObject()) {
            node.fields().forEachRemaining(entry -> {
                String key = entry.getKey();
                JsonNode value = entry.getValue();
                result.put(key, convertJsonValue(value));
            });
        }
        
        return result;
    }
}