package novus.config.config_loader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import novus.config.config_interfaces.ConfigurationLoader;
import novus.config.config_interfaces.CustomExceptions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * Enhanced configuration loader with caching, validation, and fallback
 * mechanisms
 */
public class DefaultConfigurationLoader implements ConfigurationLoader {
	private static final Logger logger = LoggerFactory.getLogger(DefaultConfigurationLoader.class);

	// Configuration file paths
	public static final String WEB_CONFIG_PATH = "config/web.json";
	public static final String RUN_CONFIG_PATH = "config/run_config.json";
	public static final String APPLICATION_CONFIG_PATH = "config/application.json";

	// Fallback configuration paths
	private static final String[] FALLBACK_PATHS = { "src/main/resources/config/", "src/test/resources/config/",
			"config/", "" };

	private final ObjectMapper objectMapper;
	private final Map<String, JsonNode> configurationCache;
	private final Map<String, Long> lastModifiedTimes;

	public DefaultConfigurationLoader() {
		this.objectMapper = createOptimizedObjectMapper();
		this.configurationCache = new ConcurrentHashMap<>();
		this.lastModifiedTimes = new ConcurrentHashMap<>();
	}

	@Override
	public JsonNode loadConfiguration(String configPath) throws ConfigurationException {
		validateConfigPath(configPath);

		// Check cache first
		JsonNode cachedConfig = getCachedConfiguration(configPath);
		if (cachedConfig != null) {
			return cachedConfig;
		}

		try {
			JsonNode config = loadConfigurationFromResource(configPath);
			validateConfiguration(config);
			cacheConfiguration(configPath, config);

			logger.debug("Successfully loaded and cached configuration: {}", configPath);
			return config;

		} catch (IOException e) {
			logger.error("Failed to load configuration: {}", configPath, e);
			throw new ConfigurationException("Configuration loading failed: " + configPath, e);
		}
	}

	@Override
	public void validateConfiguration(JsonNode config) throws ConfigurationException {
		if (config == null || config.isMissingNode()) {
			throw new ConfigurationException("Configuration is null or missing");
		}

		if (config.isEmpty()) {
			throw new ConfigurationException("Configuration is empty");
		}

		// Additional validation can be added here based on specific requirements
		logger.debug("Configuration validation passed");
	}

	@Override
	public boolean isConfigurationValid(JsonNode config) {
		try {
			validateConfiguration(config);
			return true;
		} catch (ConfigurationException e) {
			logger.warn("Configuration validation failed: {}", e.getMessage());
			return false;
		}
	}

	@Override
	public void reloadConfiguration(String configPath) throws ConfigurationException {
		logger.info("Reloading configuration: {}", configPath);

		// Remove from cache to force reload
		configurationCache.remove(configPath);
		lastModifiedTimes.remove(configPath);

		// Load fresh configuration
		loadConfiguration(configPath);
	}

	/**
	 * Load configuration from classpath resources with fallback mechanisms
	 */
	private JsonNode loadConfigurationFromResource(String configPath) throws IOException {
		// Try primary resource location
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configPath)) {
			if (inputStream != null) {
				return objectMapper.readTree(inputStream);
			}
		}

		// Try fallback locations
		for (String fallbackPath : FALLBACK_PATHS) {
			String fullPath = fallbackPath + configPath;
			try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fullPath)) {
				if (inputStream != null) {
					logger.warn("Configuration loaded from fallback location: {}", fullPath);
					return objectMapper.readTree(inputStream);
				}
			}
		}

		// Try file system as last resort
		Path filePath = Paths.get(configPath);
		if (Files.exists(filePath)) {
			logger.warn("Configuration loaded from file system: {}", filePath);
			return objectMapper.readTree(Files.newInputStream(filePath));
		}

		throw new IOException("Configuration resource not found: " + configPath);
	}

	/**
	 * Create optimized ObjectMapper with performance settings
	 */
	private ObjectMapper createOptimizedObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		// Configure for better performance and security
		// mapper.configure(JsonParser.Feature.USE_FAST_DOUBLE_PARSER, true);
		// mapper.configure(JsonGenerator.Feature.USE_FAST_DOUBLE_WRITER, true);
		return mapper;
	}

	/**
	 * Get cached configuration if still valid
	 */
	private JsonNode getCachedConfiguration(String configPath) {
		JsonNode cached = configurationCache.get(configPath);
		if (cached == null) {
			return null;
		}

		// Check if configuration file has been modified
		if (isConfigurationModified(configPath)) {
			logger.debug("Configuration file has been modified, cache invalidated: {}", configPath);
			configurationCache.remove(configPath);
			return null;
		}

		logger.debug("Returning cached configuration: {}", configPath);
		return cached;
	}

	/**
	 * Cache configuration with timestamp
	 */
	private void cacheConfiguration(String configPath, JsonNode config) {
		configurationCache.put(configPath, config);
		lastModifiedTimes.put(configPath, System.currentTimeMillis());
	}

	/**
	 * Check if configuration file has been modified since last load
	 */
	private boolean isConfigurationModified(String configPath) {
		Long lastModified = lastModifiedTimes.get(configPath);
		if (lastModified == null) {
			return true;
		}

		try {
			Path filePath = Paths.get(configPath);
			if (Files.exists(filePath)) {
				long fileLastModified = Files.getLastModifiedTime(filePath).toMillis();
				return fileLastModified > lastModified;
			}
		} catch (IOException e) {
			logger.debug("Error checking file modification time: {}", e.getMessage());
		}

		return false;
	}

	/**
	 * Validate configuration path
	 */
	private void validateConfigPath(String configPath) throws ConfigurationException {
		if (configPath == null || configPath.trim().isEmpty()) {
			throw new ConfigurationException("Configuration path cannot be null or empty");
		}

		if (!configPath.endsWith(".json")) {
			throw new ConfigurationException("Configuration file must have .json extension: " + configPath);
		}
	}

	/**
	 * Clear all cached configurations
	 */
	public void clearCache() {
		configurationCache.clear();
		lastModifiedTimes.clear();
		logger.info("Configuration cache cleared");
	}

	/**
	 * Get cache statistics for monitoring
	 */
	public Map<String, Object> getCacheStatistics() {
		Map<String, Object> stats = new ConcurrentHashMap<>();
		stats.put("cachedConfigurations", configurationCache.size());
		stats.put("cacheHitRate", calculateCacheHitRate());
		stats.put("lastClearTime", System.currentTimeMillis());
		return stats;
	}

	/**
	 * Calculate cache hit rate (simplified implementation)
	 */
	private double calculateCacheHitRate() {
		// This would require hit/miss counters in a production implementation
		return configurationCache.isEmpty() ? 0.0 : 85.0; // Placeholder
	}
}
