package novus.config;

import com.fasterxml.jackson.databind.JsonNode;

import novus.config.config_interfaces.ConfigurationLoader;
import novus.config.config_loader.ConfigurationPropertiesProvider;
import novus.config.config_loader.DefaultConfigurationLoader;

public class DemoClass {

	private static ConfigurationLoader configLoader;

	public static JsonNode webConfig;

	public static void main(String[] args) {
		try {
			// Initialize the configuration loader
			configLoader = new DefaultConfigurationLoader();

			// Load the web configuration
			webConfig = configLoader.loadConfiguration(DefaultConfigurationLoader.WEB_CONFIG_PATH);

			System.out.println("Web Configuration loaded:");
			System.out.println(webConfig);

			// Optionally initialize properties provider if needed
			// propertiesProvider = new ConfigurationPropertiesProvider(webConfig, null,
			// null);

		} catch (Exception e) {
			System.err.println("Error loading configuration: " + e.getMessage());
			e.printStackTrace();
		}
	}
}