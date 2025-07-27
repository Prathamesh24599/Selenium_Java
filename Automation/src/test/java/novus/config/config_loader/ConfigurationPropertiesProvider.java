package novus.config.config_loader;

import com.fasterxml.jackson.databind.JsonNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Configuration properties provider with type-safe access and system property override
 */
public class ConfigurationPropertiesProvider{
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationPropertiesProvider.class);
    
    private final JsonNode webConfig;
    private final JsonNode runConfig;
    private final JsonNode applicationConfig;
    
    public ConfigurationPropertiesProvider(JsonNode webConfig, JsonNode runConfig, JsonNode applicationConfig) {
        this.webConfig = webConfig;
        this.runConfig = runConfig;
        this.applicationConfig = applicationConfig;
    }
    
    /**
     * Get string property with system property override support
     */
    public String getStringProperty(String systemProperty, String configPath, String defaultValue) {
        // Check system property first
        String systemValue = System.getProperty(systemProperty);
        if (systemValue != null && !systemValue.trim().isEmpty()) {
            logger.debug("Using system property value for {}: {}", systemProperty, systemValue);
            return systemValue;
        }
        
        // Check configuration file
        String configValue = getConfigValue(configPath, defaultValue);
        logger.debug("Using configuration value for {}: {}", configPath, configValue);
        return configValue;
    }
    
    /**
     * Get boolean property with system property override support
     */
    public boolean getBooleanProperty(String systemProperty, String configPath, boolean defaultValue) {
        String systemValue = System.getProperty(systemProperty);
        if (systemValue != null) {
            return Boolean.parseBoolean(systemValue);
        }
        
        return getConfigNode(configPath).asBoolean(defaultValue);
    }
    
    /**
     * Get integer property with system property override support
     */
    public int getIntProperty(String systemProperty, String configPath, int defaultValue) {
        String systemValue = System.getProperty(systemProperty);
        if (systemValue != null) {
            try {
                return Integer.parseInt(systemValue);
            } catch (NumberFormatException e) {
                logger.warn("Invalid integer value in system property {}: {}", systemProperty, systemValue);
            }
        }
        
        return getConfigNode(configPath).asInt(defaultValue);
    }
    
    /**
     * Get configuration value by path
     */
    private String getConfigValue(String configPath, String defaultValue) {
        JsonNode node = getConfigNode(configPath);
        return node.asText(defaultValue);
    }
    
    /**
     * Get configuration node by path
     */
    private JsonNode getConfigNode(String configPath) {
        String[] pathParts = configPath.split("\\.");
        JsonNode currentNode = null;
        
        // Determine which config to use based on path
        if (pathParts[0].equals("web")) {
            currentNode = webConfig;
        } else if (pathParts[0].equals("run")) {
            currentNode = runConfig;
        } else if (pathParts[0].equals("application")) {
            currentNode = applicationConfig;
        } else {
            // Try all configs
            currentNode = webConfig.path(pathParts[0]);
            if (currentNode.isMissingNode()) {
                currentNode = runConfig.path(pathParts[0]);
            }
            if (currentNode.isMissingNode()) {
                currentNode = applicationConfig.path(pathParts[0]);
            }
            // Start from index 0 if no config prefix found
            for (int i = 0; i < pathParts.length; i++) {
                currentNode = currentNode.path(pathParts[i]);
            }
            return currentNode;
        }
        
        // Navigate through the path (skip first element as it's the config type)
        for (int i = 1; i < pathParts.length; i++) {
            currentNode = currentNode.path(pathParts[i]);
        }
        
        return currentNode;
    }
    
    /**
     * Get environment-specific configuration
     */
    public String getEnvironmentProperty(String environment, String property, String defaultValue) {
        return applicationConfig.path("environments")
                              .path(environment)
                              .path(property)
                              .asText(defaultValue);
    }
    
    /**
     * Get test user configuration
     */
    public JsonNode getTestUser(String userType) {
        return applicationConfig.path("testUsers").path(userType);
    }
    
    /**
     * Get endpoint configuration
     */
    public String getEndpoint(String category, String endpointName) {
        return applicationConfig.path("endpoints")
                              .path(category)
                              .path(endpointName)
                              .asText("");
    }
    
    /**
     * Check if feature is enabled for environment
     */
    public boolean isFeatureEnabled(String environment, String featureName) {
        return applicationConfig.path("environments")
                              .path(environment)
                              .path("features")
                              .path(featureName)
                              .asBoolean(false);
    }
    
//    @Override
//    public String getBrowserName() {
//        return getStringProperty("browser", "run.browser", "chrome");
//    }
//
//    @Override
//    public String getEnvironment() {
//        return getStringProperty("environment", "run.environment", "staging");
//    }
//
//    @Override
//    public String getBaseUrl() {
//        String environment = getEnvironment();
//        String baseUrl = getEnvironmentProperty(environment, "baseUrl", "");
//        
//        if (baseUrl.isEmpty()) {
//            throw new ConfigurationException("Base URL not configured for environment: " + environment);
//        }
//        
//        return baseUrl;
//    }
//
//    @Override
//    public boolean isRemoteExecution() {
//        return getBooleanProperty("remote.execution", "run.grid.remoteExecution", false);
//    }
//
//    @Override
//    public boolean isHeadlessMode() {
//        return getBooleanProperty("headless", "run.headless", false);
//    }
//
//    @Override
//    public String getMobileDevice() {
//        return getStringProperty("mobile.device", "run.mobileDevice", "");
//    }
//
//    @Override
//    public int getThreadCount() {
//        return getIntProperty("thread.count", "run.execution.threadCount", 4);
//    }
//
//    @Override
//    public TimeoutConfiguration getTimeouts() {
//        return new TimeoutConfiguration.Builder()
//                .implicitTimeout(getIntProperty("timeout.implicit", "web.timeouts.implicit", 10))
//                .pageLoadTimeout(getIntProperty("timeout.pageLoad", "web.timeouts.pageLoad", 30))
//                .scriptTimeout(getIntProperty("timeout.script", "web.timeouts.script", 20))
//                .pollingInterval(getIntProperty("timeout.polling", "web.timeouts.pollingInterval", 500))
//                .build();
//    }
//
//    @Override
//    public ProxyConfiguration getProxy() {
//        boolean enabled = getBooleanProperty("proxy.enabled", "web.proxy.enabled", false);
//        
//        if (!enabled) {
//            return ProxyConfiguration.createDisabled();
//        }
//        
//        return new ProxyConfiguration.Builder()
//                .enabled(true)
//                .httpProxy(getStringProperty("proxy.http", "web.proxy.httpProxy", ""))
//                .sslProxy(getStringProperty("proxy.ssl", "web.proxy.sslProxy", ""))
//                .ftpProxy(getStringProperty("proxy.ftp", "web.proxy.ftpProxy", ""))
//                .noProxy(getStringProperty("proxy.no", "web.proxy.noProxy", ""))
//                .proxyType(getStringProperty("proxy.type", "web.proxy.type", "MANUAL"))
//                .build();
//    }
}
