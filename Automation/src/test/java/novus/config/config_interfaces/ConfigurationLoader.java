package novus.config.config_interfaces;

import com.fasterxml.jackson.databind.JsonNode;

import novus.config.config_interfaces.CustomExceptions.ConfigurationException;

/**
 * Configuration loading interface for better testability and modularity
 */
public interface ConfigurationLoader {
    JsonNode loadConfiguration(String configPath) throws ConfigurationException;
    void validateConfiguration(JsonNode config) throws ConfigurationException;
    boolean isConfigurationValid(JsonNode config);
    void reloadConfiguration(String configPath) throws ConfigurationException;
}