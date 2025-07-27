package novus.config.main_configuration;

//===============================
//MAIN CONFIGURATION MANAGER
//===============================

import com.fasterxml.jackson.databind.JsonNode;

import novus.config.config_interfaces.ConfigurationLoader;
import novus.config.config_interfaces.ConfigurationProperties;
import novus.config.config_interfaces.HealthCheck;
import novus.config.config_interfaces.SystemHealthChecker;
import novus.config.config_interfaces.WebDriverFactory;
import novus.config.config_interfaces.ResourceManager;
import novus.config.config_interfaces.CustomExceptions.ConfigurationException;
import novus.config.config_interfaces.CustomExceptions.DriverCreationException;
import novus.config.config_loader.ConfigurationPropertiesProvider;
import novus.config.config_loader.DefaultConfigurationLoader;
import novus.config.health_checker.ComprehensiveHealthChecker;
import novus.config.models.DriverConfiguration;
import novus.config.models.HealthStatus;
import novus.config.models.PerformanceConfiguration;
import novus.config.models.ProxyConfiguration;
import novus.config.models.SecurityConfiguration;
import novus.config.models.TimeoutConfiguration;
import novus.config.resource_manager.EnterpriseResourceManager;
import novus.config.webdriver_factory.EnhancedWebDriverFactory;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enterprise Configuration Manager - Main facade class that orchestrates all
 * components
 * 
 * This class follows the Facade pattern to provide a simplified interface to
 * the complex subsystem of configuration management, WebDriver creation,
 * database management, and health monitoring.
 * 
 * Features: - Thread-safe singleton with proper initialization - Modular design
 * with separated concerns - Comprehensive error handling and recovery -
 * Performance monitoring and health checks - Automatic resource management and
 * cleanup - Support for multiple environments and configurations
 * 
 * @author Enterprise Automation Team
 * @version 4.0
 * @since 2025
 */
public class EnterpriseConfigurationManager implements ConfigurationProperties {
	private static final Logger logger = LoggerFactory.getLogger(EnterpriseConfigurationManager.class);

	// Singleton implementation
	private static volatile EnterpriseConfigurationManager instance;
	private static final Object INITIALIZATION_LOCK = new Object();

	// Core components
	private final ConfigurationLoader configLoader;
	private final WebDriverFactory webDriverFactory;
//	private final DatabaseManager databaseManager;
	private final SystemHealthChecker healthChecker;
	private final ResourceManager resourceManager;
	
	private final ConfigurationPropertiesProvider configProvider;

	// Configuration holders
	private volatile JsonNode webConfig;
	private volatile JsonNode runConfig;
	private volatile JsonNode applicationConfig;

	// Runtime state
	private final long initializationStartTime;
	private volatile boolean isHealthy = true;
	private final Map<String, Object> runtimeMetrics = new ConcurrentHashMap<>();

	/**
	 * Private constructor implementing secure Singleton pattern
	 */
	private EnterpriseConfigurationManager() {
		this.initializationStartTime = System.currentTimeMillis();

		logger.info("Initializing Enterprise Configuration Manager...");

		try {
			// Initialize core components
			this.configLoader = new DefaultConfigurationLoader();
			this.resourceManager = new EnterpriseResourceManager();
			this.resourceManager.registerShutdownHook();

			// Load configurations
			loadConfigurations();

			// Initialize components with configurations
			this.configProvider = new ConfigurationPropertiesProvider(webConfig, runConfig, applicationConfig);
//			this.propertiesProvider = this.configProvider;
			this.webDriverFactory = new EnhancedWebDriverFactory(getRemoteWebDriverUrl());
//			this.databaseManager = new EnhancedDatabaseManager(applicationConfig.path("database"));
			this.healthChecker = new ComprehensiveHealthChecker();

			// Perform initial health check
			performInitialHealthCheck();

			// Initialize runtime metrics
			initializeRuntimeMetrics();

			long initTime = System.currentTimeMillis() - initializationStartTime;
			logger.info("Enterprise Configuration Manager initialized successfully in {}ms", initTime);

		} catch (Exception e) {
			logger.error("Critical failure during Configuration Manager initialization", e);
			this.isHealthy = false;
			throw new ConfigurationException("Failed to initialize Configuration Manager", e);
		}
	}

	/**
	 * Get singleton instance with thread safety and health validation
	 */
	public static EnterpriseConfigurationManager getInstance() {
		if (instance == null) {
			synchronized (INITIALIZATION_LOCK) {
				if (instance == null) {
					instance = new EnterpriseConfigurationManager();
				}
			}
		}

		if (!instance.isHealthy) {
			throw new ConfigurationException("Configuration Manager is in unhealthy state");
		}

		return instance;
	}

	// ===============================
	// WEBDRIVER MANAGEMENT
	// ===============================

	/**
	 * Create WebDriver with default configuration
	 */
	public WebDriver createDriver() {
		return createDriver(null);
	}

	/**
	 * Create WebDriver with custom capabilities
	 */
	public WebDriver createDriver(Map<String, Object> customCapabilities) {
	    long startTime = System.currentTimeMillis();

	    try {
	        DriverConfiguration config = buildDriverConfiguration(customCapabilities);
	        WebDriver driver = webDriverFactory.createDriver(config);

	        // Register driver for automatic cleanup
	        String resourceKey = "webdriver-" + Thread.currentThread().threadId() + "-" + System.currentTimeMillis();
	        
	        // Handle WebDriver registration with proper AutoCloseable handling
	        if (driver instanceof AutoCloseable) {
	            resourceManager.registerResource(resourceKey, (AutoCloseable) driver);
	        } else {
	            // Create wrapper for cases where WebDriver doesn't properly implement AutoCloseable
	            AutoCloseable driverWrapper = () -> {
	                if (driver != null) {
	                    driver.quit();
	                }
	            };
	            resourceManager.registerResource(resourceKey, driverWrapper);
	        }

	        long creationTime = System.currentTimeMillis() - startTime;
	        updateRuntimeMetric("lastDriverCreationTimeMs", creationTime);
	        updateRuntimeMetric("totalDriversCreated", getRuntimeMetric("totalDriversCreated", 0L) + 1);

	        logger.info("WebDriver created successfully in {}ms for thread: {}", creationTime,
	                Thread.currentThread().threadId());

	        return driver;

	    } catch (Exception e) {
	        logger.error("Failed to create WebDriver", e);
	        throw new DriverCreationException("WebDriver creation failed", e);
	    }
	}

	/**
	 * Quit driver for current thread
	 */
	public void quitDriver() {
		// This would need to be enhanced to track drivers per thread
		// For now, we'll clean up WebDriver resources
		resourceManager.cleanupResourcesByType("WebDriver");
		logger.info("WebDriver cleanup completed for thread: {}", Thread.currentThread().threadId());
	}

	/**
	 * Quit all active drivers
	 */
	public void quitAllDrivers() {
		resourceManager.cleanupResourcesByType("WebDriver");
		logger.info("All WebDrivers cleaned up successfully");
	}

	

	// ===============================
	// HEALTH MONITORING
	// ===============================

	/**
	 * Perform comprehensive health check
	 */
	public HealthStatus performHealthCheck() {
		return healthChecker.performHealthCheck();
	}

	/**
	 * Check if system is ready for test execution
	 */
	public boolean isReadyForExecution() {
		return isHealthy && webConfig != null && runConfig != null && applicationConfig != null
				&& healthChecker.isSystemHealthy();
	}

	/**
	 * Register custom health check
	 */
	public void registerHealthCheck(String name, HealthCheck check) {
		healthChecker.registerHealthCheck(name, check);
	}

	// ===============================
	// CONFIGURATION PROPERTIES IMPLEMENTATION
	// ===============================

	@Override
	public String getBrowserName() {
		return configProvider.getStringProperty("browser", "run.browser", "edge");
	}

	@Override
	public String getEnvironment() {
		return configProvider.getStringProperty("environment", "run.environment", "staging");
	}

	@Override
	public String getBaseUrl() {
		String environment = getEnvironment();
//		String baseUrl = configProvider.getStringProperty(environment, "baseUrl", "https://www.google.co.in");
		String baseUrl = configProvider.getEnvironmentProperty(environment, "baseUrl", "https://www.google.co.in");

		if (baseUrl.isEmpty()) {
			throw new ConfigurationException("Base URL not configured for environment: " + environment);
		}

		return baseUrl;
	}

	@Override
	public boolean isRemoteExecution() {
		return configProvider.getBooleanProperty("remote.execution", "run.grid.remoteExecution", false);
	}

	@Override
	public boolean isHeadlessMode() {
		return configProvider.getBooleanProperty("headless", "run.headless", false);
	}

	@Override
	public String getMobileDevice() {
		return configProvider.getStringProperty("mobile.device", "run.mobileDevice", "");
	}

	@Override
	public int getThreadCount() {
		return configProvider.getIntProperty("thread.count", "run.execution.threadCount", 4);
	}

	@Override
	public TimeoutConfiguration getTimeouts() {
		return new TimeoutConfiguration.Builder()
				.implicitTimeout(configProvider.getIntProperty("timeout.implicit", "web.timeouts.implicit", 10))
				.pageLoadTimeout(configProvider.getIntProperty("timeout.pageLoad", "web.timeouts.pageLoad", 30))
				.scriptTimeout(configProvider.getIntProperty("timeout.script", "web.timeouts.script", 20))
				.pollingInterval(
						configProvider.getIntProperty("timeout.polling", "web.timeouts.pollingInterval", 500))
				.build();
	}

	@Override
	public ProxyConfiguration getProxy() {
		boolean enabled = configProvider.getBooleanProperty("proxy.enabled", "web.proxy.enabled", false);

		if (!enabled) {
			return ProxyConfiguration.createDisabled();
		}

		return new ProxyConfiguration.Builder().enabled(true)
				.httpProxy(configProvider.getStringProperty("proxy.http", "web.proxy.httpProxy", ""))
				.sslProxy(configProvider.getStringProperty("proxy.ssl", "web.proxy.sslProxy", ""))
				.ftpProxy(configProvider.getStringProperty("proxy.ftp", "web.proxy.ftpProxy", ""))
				.noProxy(configProvider.getStringProperty("proxy.no", "web.proxy.noProxy", ""))
				.proxyType(configProvider.getStringProperty("proxy.type", "web.proxy.type", "MANUAL")).build();
	}

	// ===============================
	// ADDITIONAL CONFIGURATION METHODS
	// ===============================

	/**
	 * Get API base URL for current environment
	 */
	public String getApiBaseUrl() {
		String environment = getEnvironment();
		return configProvider.getEnvironmentProperty(environment, "apiBaseUrl", "");
	}

	/**
	 * Get remote WebDriver hub URL
	 */
	public String getRemoteWebDriverUrl() {
		String hubHost = configProvider.getStringProperty("hub.host", "run.grid.hubHost", "localhost");
		int hubPort = configProvider.getIntProperty("hub.port", "run.grid.hubPort", 4444);
		return String.format("http://%s:%d/wd/hub", hubHost, hubPort);
	}

	/**
	 * Check if parallel execution is enabled
	 */
	public boolean isParallelExecution() {
		return configProvider.getBooleanProperty("parallel", "run.execution.parallel", false);
	}

	/**
	 * Get maximum retry attempts
	 */
	public int getMaxRetries() {
		return configProvider.getIntProperty("max.retries", "run.execution.maxRetries", 2);
	}

	/**
	 * Check if screenshots should be taken
	 */
	public boolean shouldTakeScreenshots() {
		return configProvider.getBooleanProperty("screenshots", "run.screenshots.takeScreenshots", true);
	}

	/**
	 * Get screenshot output path
	 */
	public String getScreenshotPath() {
		return configProvider.getStringProperty("screenshot.path", "run.screenshots.screenshotPath",
				"./test-output/screenshots");
	}

	/**
	 * Check if video recording is enabled
	 */
	public boolean isVideoRecordingEnabled() {
		return configProvider.getBooleanProperty("video.recording", "run.video.videoRecording", false);
	}

	/**
	 * Get test user configuration
	 */
	public Map<String, String> getTestUser(String userType) {
		JsonNode userNode = configProvider.getTestUser(userType);
		Map<String, String> user = new ConcurrentHashMap<>();

		if (!userNode.isMissingNode()) {
			userNode.fields().forEachRemaining(entry -> {
				user.put(entry.getKey(), entry.getValue().asText());
			});
		}

		return user;
	}

	/**
	 * Get endpoint URL
	 */
	public String getEndpoint(String category, String endpointName) {
		return configProvider.getEndpoint(category, endpointName);
	}

	/**
	 * Check if feature is enabled for current environment
	 */
	public boolean isFeatureEnabled(String featureName) {
		String environment = getEnvironment();
		return configProvider.isFeatureEnabled(environment, featureName);
	}

	// ===============================
	// RESOURCE AND LIFECYCLE MANAGEMENT
	// ===============================

	/**
	 * Reload all configurations
	 */
	public synchronized void reloadConfigurations() {
		logger.info("Reloading configurations...");

		try {
			loadConfigurations();
			performInitialHealthCheck();
			logger.info("Configuration reload completed successfully");

		} catch (Exception e) {
			logger.error("Configuration reload failed", e);
			this.isHealthy = false;
			throw new ConfigurationException("Configuration reload failed", e);
		}
	}

	/**
	 * Get comprehensive system information
	 */
	public Map<String, Object> getSystemInfo() {
		Map<String, Object> systemInfo = new ConcurrentHashMap<>();

		// JVM Information
		systemInfo.put("javaVersion", System.getProperty("java.version"));
		systemInfo.put("javaVendor", System.getProperty("java.vendor"));
		systemInfo.put("jvmMaxMemory", Runtime.getRuntime().maxMemory() / (1024 * 1024) + " MB");
		systemInfo.put("availableProcessors", Runtime.getRuntime().availableProcessors());

		// OS Information
		systemInfo.put("osName", System.getProperty("os.name"));
		systemInfo.put("osVersion", System.getProperty("os.version"));
		systemInfo.put("osArch", System.getProperty("os.arch"));

		// Configuration Information
		systemInfo.put("configurationHealth", isHealthy);
		systemInfo.put("initializationTime", System.currentTimeMillis() - initializationStartTime);
		systemInfo.put("runtimeMetrics", new ConcurrentHashMap<>(runtimeMetrics));

		// Component Statistics
		systemInfo.put("resourceStatistics", resourceManager.getResourceStatistics());
		systemInfo.put("healthCheckStatistics",
				healthChecker instanceof ComprehensiveHealthChecker
						? ((ComprehensiveHealthChecker) healthChecker).getHealthCheckStatistics()
						: null);

		return systemInfo;
	}

	/**
	 * Get configuration as JSON string for external integrations
	 */
	public String getConfigurationAsJson() {
		try {
			Map<String, Object> config = new ConcurrentHashMap<>();
			config.put("environment", getEnvironment());
			config.put("browser", getBrowserName());
			config.put("baseUrl", getBaseUrl());
			config.put("remoteExecution", isRemoteExecution());
			config.put("parallelExecution", isParallelExecution());
			config.put("threadCount", getThreadCount());
			config.put("systemInfo", getSystemInfo());

			// Use a simple JSON representation (in real implementation, use Jackson)
			return config.toString();

		} catch (Exception e) {
			logger.error("Failed to serialize configuration", e);
			return "{}";
		}
	}

	/**
	 * Shutdown the configuration manager and release all resources
	 */
	public void shutdown() {
		logger.info("Shutting down Enterprise Configuration Manager...");

		try {
			quitAllDrivers();
//			closeAllDatabaseConnections();
			resourceManager.releaseAllResources();

			this.isHealthy = false;
			logger.info("Enterprise Configuration Manager shutdown completed");

		} catch (Exception e) {
			logger.error("Error during shutdown", e);
		}
	}

	// ===============================
	// PRIVATE HELPER METHODS
	// ===============================

	/**
	 * Load all configuration files
	 */
	private void loadConfigurations() throws ConfigurationException {
		try {
			logger.debug("Loading configuration files...");

			webConfig = configLoader.loadConfiguration(DefaultConfigurationLoader.WEB_CONFIG_PATH);
			runConfig = configLoader.loadConfiguration(DefaultConfigurationLoader.RUN_CONFIG_PATH);
			applicationConfig = configLoader.loadConfiguration(DefaultConfigurationLoader.APPLICATION_CONFIG_PATH);

			logger.info("All configurations loaded successfully");

		} catch (Exception e) {
			logger.error("Configuration loading failed", e);
			throw new ConfigurationException("Unable to load required configurations", e);
		}
	}

	/**
	 * Build driver configuration from current settings
	 */
	private DriverConfiguration buildDriverConfiguration(Map<String, Object> customCapabilities) {
		DriverConfiguration.Builder builder = new DriverConfiguration.Builder().browserName(getBrowserName())
				.headless(isHeadlessMode()).remote(isRemoteExecution()).mobileDevice(getMobileDevice())
				.timeouts(getTimeouts()).proxy(getProxy());

		// Add performance configuration
		PerformanceConfiguration performance = new PerformanceConfiguration.Builder()
				.loggingEnabled(
						configProvider.getBooleanProperty("perf.logging", "web.logging.performance.enabled", false))
				.build();
		builder.performance(performance);

		// Add security configuration
		SecurityConfiguration security = new SecurityConfiguration.Builder()
				.certificateValidationEnabled(configProvider.getBooleanProperty("security.cert.validation",
						"web.security.certificateValidation", true))
				.build();
		builder.security(security);

		// Add custom capabilities
		if (customCapabilities != null) {
			builder.customCapabilities(customCapabilities);
		}

		return builder.build();
	}

	/**
	 * Perform initial health check
	 */
	private void performInitialHealthCheck() {
		HealthStatus healthStatus = healthChecker.performHealthCheck();
		if (!healthStatus.isHealthy()) {
			logger.warn("Initial health check detected issues: {}", healthStatus.getIssues());
			// Don't fail initialization for health issues, just log them
		}
	}

	/**
	 * Initialize runtime metrics
	 */
	private void initializeRuntimeMetrics() {
		runtimeMetrics.put("startTime", initializationStartTime);
		runtimeMetrics.put("initializationDuration", System.currentTimeMillis() - initializationStartTime);
		runtimeMetrics.put("totalDriversCreated", 0L);
		runtimeMetrics.put("lastDriverCreationTimeMs", 0L);

		logger.debug("Runtime metrics initialized");
	}

	/**
	 * Update runtime metric
	 */
	private void updateRuntimeMetric(String key, Object value) {
		runtimeMetrics.put(key, value);
	}

	/**
	 * Get runtime metric
	 */
	private <T> T getRuntimeMetric(String key, T defaultValue) {
		@SuppressWarnings("unchecked")
		T value = (T) runtimeMetrics.get(key);
		return value != null ? value : defaultValue;
	}
}