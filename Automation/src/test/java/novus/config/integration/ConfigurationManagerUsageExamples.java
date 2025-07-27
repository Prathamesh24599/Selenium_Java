package novus.config.integration;

//===============================
//USAGE EXAMPLE AND INTEGRATION GUIDE
//===============================


import novus.config.resource_manager.EnterpriseResourceManager;
import novus.config.config_interfaces.HealthCheck;
import novus.config.main_configuration.EnterpriseConfigurationManager;
import novus.config.models.HealthCheckResult;
import novus.config.models.HealthStatus;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Comprehensive usage examples for the Enterprise Configuration Manager
 * 
 * This class demonstrates: - Basic setup and initialization - WebDriver
 * creation with different configurations - Database connection management -
 * Health monitoring integration - Custom health checks - Resource cleanup best
 * practices - Multi-environment testing - Integration with test frameworks
 */
public class ConfigurationManagerUsageExamples {
	private static final Logger logger = LoggerFactory.getLogger(ConfigurationManagerUsageExamples.class);

	/**
	 * Basic usage example
	 */
	public static void basicUsageExample() {
		logger.info("=== Basic Usage Example ===");

		try {
			// Get configuration manager instance
			EnterpriseConfigurationManager configManager = EnterpriseConfigurationManager.getInstance();

			// Check if system is ready
			if (!configManager.isReadyForExecution()) {
				logger.error("System is not ready for test execution");
				return;
			}

			// Create WebDriver with default configuration
			WebDriver driver = configManager.createDriver();

			// Navigate to application
			driver.get(configManager.getBaseUrl());

			// Perform test operations
			logger.info("Current page title: {}", driver.getTitle());

			// Cleanup (automatic via resource manager)
			configManager.quitDriver();

			logger.info("Basic usage example completed successfully");

		} catch (Exception e) {
			logger.error("Error in basic usage example", e);
		}
	}

	/**
	 * Advanced WebDriver configuration example
	 */
	public static void advancedWebDriverExample() {
		logger.info("=== Advanced WebDriver Configuration Example ===");

		try {
			EnterpriseConfigurationManager configManager = EnterpriseConfigurationManager.getInstance();

			// Create custom capabilities
			Map<String, Object> customCapabilities = new HashMap<>();
			customCapabilities.put("acceptInsecureCerts", true);
			customCapabilities.put("pageLoadStrategy", "eager");

			// Create WebDriver with custom capabilities
			WebDriver driver = configManager.createDriver(customCapabilities);

			// Example of using different configurations
			logger.info("Browser: {}", configManager.getBrowserName());
			logger.info("Environment: {}", configManager.getEnvironment());
			logger.info("Base URL: {}", configManager.getBaseUrl());
			logger.info("Remote execution: {}", configManager.isRemoteExecution());
			logger.info("Headless mode: {}", configManager.isHeadlessMode());
			logger.info("Mobile device: {}", configManager.getMobileDevice());

			// Navigate and perform actions
			driver.get(configManager.getBaseUrl());

			// Take screenshot if enabled
			if (configManager.shouldTakeScreenshots()) {
				logger.info("Screenshots enabled, path: {}", configManager.getScreenshotPath());
			}

			// Cleanup
			configManager.quitDriver();

			logger.info("Advanced WebDriver example completed successfully");

		} catch (Exception e) {
			logger.error("Error in advanced WebDriver example", e);
		}
	}

	/**
	 * Database integration example
	 */
//	public static void databaseIntegrationExample() {
//		logger.info("=== Database Integration Example ===");
//
//		try {
//			EnterpriseConfigurationManager configManager = EnterpriseConfigurationManager.getInstance();
//
//			// Get database connection for current environment
//			try (Connection connection = configManager.getDatabaseConnection()) {
//
//				// Perform database operations
//				logger.info("Database connection established successfully");
//
//				// Example query execution
//				var statement = connection.createStatement();
//				var resultSet = statement.executeQuery("SELECT 1 as test_column");
//
//				if (resultSet.next()) {
//					logger.info("Database query successful, result: {}", resultSet.getInt("test_column"));
//				}
//
//				resultSet.close();
//				statement.close();
//
//			} catch (SQLException e) {
//				logger.error("Database operation failed", e);
//			}
//
//			// Connection is automatically closed due to try-with-resources
//			logger.info("Database integration example completed successfully");
//
//		} catch (Exception e) {
//			logger.error("Error in database integration example", e);
//		}
//	}

	/**
	 * Multi-environment testing example
	 */
	public static void multiEnvironmentExample() {
		logger.info("=== Multi-Environment Testing Example ===");

		try {
			EnterpriseConfigurationManager configManager = EnterpriseConfigurationManager.getInstance();

			// Get environment-specific configurations
			String currentEnvironment = configManager.getEnvironment();
			String baseUrl = configManager.getBaseUrl();
			String apiBaseUrl = configManager.getApiBaseUrl();

			logger.info("Testing in environment: {}", currentEnvironment);
			logger.info("Base URL: {}", baseUrl);
			logger.info("API Base URL: {}", apiBaseUrl);

			// Check feature flags for current environment
			boolean advancedFeatureEnabled = configManager.isFeatureEnabled("advancedFeature");
			boolean betaFeatureEnabled = configManager.isFeatureEnabled("betaFeature");

			logger.info("Advanced feature enabled: {}", advancedFeatureEnabled);
			logger.info("Beta feature enabled: {}", betaFeatureEnabled);

			// Get test users for environment
			Map<String, String> adminUser = configManager.getTestUser("admin");
			Map<String, String> regularUser = configManager.getTestUser("regular");

			logger.info("Admin user: {}", adminUser);
			logger.info("Regular user: {}", regularUser);

			// Get API endpoints
			String loginEndpoint = configManager.getEndpoint("auth", "login");
			String userEndpoint = configManager.getEndpoint("api", "users");

			logger.info("Login endpoint: {}", loginEndpoint);
			logger.info("Users endpoint: {}", userEndpoint);

			logger.info("Multi-environment example completed successfully");

		} catch (Exception e) {
			logger.error("Error in multi-environment example", e);
		}
	}

	/**
	 * Health monitoring and custom health checks example
	 */
	public static void healthMonitoringExample() {
		logger.info("=== Health Monitoring Example ===");

		try {
			EnterpriseConfigurationManager configManager = EnterpriseConfigurationManager.getInstance();

			// Register custom health check
			configManager.registerHealthCheck("custom-service", new CustomServiceHealthCheck());
//			configManager.registerHealthCheck("database-specific", new DatabaseSpecificHealthCheck());

			// Perform comprehensive health check
			HealthStatus healthStatus = configManager.performHealthCheck();

			logger.info("System health status: {}", healthStatus.isHealthy() ? "HEALTHY" : "UNHEALTHY");
			logger.info("Health check execution time: {}ms", healthStatus.getTotalExecutionTimeMs());

			if (!healthStatus.isHealthy()) {
				logger.warn("Health issues detected:");
				healthStatus.getIssues().forEach(issue -> logger.warn("  - {}", issue));
			}

			// Display individual health check results
			healthStatus.getResults().forEach(result -> {
				logger.info("Health check '{}': {} - {} ({}ms)", result.getName(), result.isHealthy() ? "PASS" : "FAIL",
						result.getMessage(), result.getExecutionTimeMs());
			});

			logger.info("Health monitoring example completed successfully");

		} catch (Exception e) {
			logger.error("Error in health monitoring example", e);
		}
	}

	/**
	 * Parallel execution example
	 */
	public static void parallelExecutionExample() {
		logger.info("=== Parallel Execution Example ===");

		try {
			EnterpriseConfigurationManager configManager = EnterpriseConfigurationManager.getInstance();

			if (!configManager.isParallelExecution()) {
				logger.info("Parallel execution is disabled");
				return;
			}

			int threadCount = configManager.getThreadCount();
			logger.info("Parallel execution enabled with {} threads", threadCount);

			// Simulate parallel test execution
			Thread[] threads = new Thread[threadCount];

			for (int i = 0; i < threadCount; i++) {
				final int threadIndex = i;
				threads[i] = new Thread(() -> {
					try {
						logger.info("Thread {} starting", threadIndex);

						// Each thread gets its own WebDriver
						WebDriver driver = configManager.createDriver();

						// Perform test operations
						driver.get(configManager.getBaseUrl());
						logger.info("Thread {} navigated to: {}", threadIndex, driver.getCurrentUrl());

						// Simulate test execution time
						Thread.sleep(2000);

						// Cleanup for this thread
						configManager.quitDriver();

						logger.info("Thread {} completed", threadIndex);

					} catch (Exception e) {
						logger.error("Error in thread {}", threadIndex, e);
					}
				});
			}

			// Start all threads
			for (Thread thread : threads) {
				thread.start();
			}

			// Wait for all threads to complete
			for (Thread thread : threads) {
				thread.join();
			}

			logger.info("Parallel execution example completed successfully");

		} catch (Exception e) {
			logger.error("Error in parallel execution example", e);
		}
	}

	/**
	 * Resource management and cleanup example
	 */
	public static void resourceManagementExample() {
		logger.info("=== Resource Management Example ===");

		try {
			EnterpriseConfigurationManager configManager = EnterpriseConfigurationManager.getInstance();

			// Get system information
			Map<String, Object> systemInfo = configManager.getSystemInfo();
			logger.info("System information: {}", systemInfo);

			// Create multiple resources
			WebDriver driver1 = configManager.createDriver();
			WebDriver driver2 = configManager.createDriver();

//			Connection dbConnection = configManager.getDatabaseConnection();

			// Perform operations
			driver1.get(configManager.getBaseUrl());
			driver2.get(configManager.getBaseUrl());

			// Check resource statistics
			logger.info("System info after resource creation: {}", configManager.getSystemInfo());

			// Cleanup specific resources
			configManager.quitDriver(); // Cleans up current thread's drivers
//			configManager.closeDatabaseConnection();

			// Or cleanup all resources
			configManager.quitAllDrivers();
//			configManager.closeAllDatabaseConnections();

			logger.info("Resource management example completed successfully");

		} catch (Exception e) {
			logger.error("Error in resource management example", e);
		}
	}

	/**
	 * Configuration reloading example
	 */
	public static void configurationReloadExample() {
		logger.info("=== Configuration Reloading Example ===");

		try {
			EnterpriseConfigurationManager configManager = EnterpriseConfigurationManager.getInstance();

			// Get initial configuration
			String initialBrowser = configManager.getBrowserName();
			String initialEnvironment = configManager.getEnvironment();

			logger.info("Initial configuration - Browser: {}, Environment: {}", initialBrowser, initialEnvironment);

			// Reload configurations (useful for dynamic configuration updates)
			configManager.reloadConfigurations();

			// Check if configuration changed
			String newBrowser = configManager.getBrowserName();
			String newEnvironment = configManager.getEnvironment();

			logger.info("After reload - Browser: {}, Environment: {}", newBrowser, newEnvironment);

			// Export configuration as JSON for external tools
			String configJson = configManager.getConfigurationAsJson();
			logger.info("Configuration JSON: {}", configJson);

			logger.info("Configuration reloading example completed successfully");

		} catch (Exception e) {
			logger.error("Error in configuration reloading example", e);
		}
	}

	/**
	 * Main method to run all examples
	 */
	public static void main(String[] args) {
		logger.info("Starting Configuration Manager Usage Examples...");

		try {
			// Run all examples
			basicUsageExample();
			advancedWebDriverExample();
//			databaseIntegrationExample();
			multiEnvironmentExample();
			healthMonitoringExample();
			parallelExecutionExample();
			resourceManagementExample();
			configurationReloadExample();

			logger.info("All examples completed successfully");

		} catch (Exception e) {
			logger.error("Error running examples", e);
		} finally {
			// Ensure proper cleanup
			try {
				EnterpriseConfigurationManager.getInstance().shutdown();
			} catch (Exception e) {
				logger.error("Error during shutdown", e);
			}
		}
	}

	/**
	 * Custom health check implementation example
	 */
	private static class CustomServiceHealthCheck implements HealthCheck {
		@Override
		public HealthCheckResult check() {
			try {
				// Simulate custom service health check
				Thread.sleep(100); // Simulate check time

				// Example check logic
				boolean serviceHealthy = Math.random() > 0.1; // 90% success rate

				if (serviceHealthy) {
					return HealthCheckResult.healthy("custom-service", "Service is responding normally");
				} else {
					return HealthCheckResult.unhealthy("custom-service", "Service is not responding");
				}

			} catch (Exception e) {
				return HealthCheckResult.unhealthy("custom-service", "Health check failed: " + e.getMessage());
			}
		}
	}

	/**
	 * Database-specific health check implementation example
	 */
//	private static class DatabaseSpecificHealthCheck implements HealthCheck {
//		@Override
//		public HealthCheckResult check() {
//			try {
//				// Simulate database-specific health check
//				EnterpriseConfigurationManager configManager = EnterpriseConfigurationManager.getInstance();
//
//				try (Connection connection = configManager.getDatabaseConnection()) {
//					// Perform a simple query to check database health
//					var statement = connection.createStatement();
//					var resultSet = statement.executeQuery("SELECT 1");
//
//					boolean hasResult = resultSet.next();
//
//					resultSet.close();
//					statement.close();
//
//					if (hasResult) {
//						return HealthCheckResult.healthy("database-specific", "Database connectivity verified");
//					} else {
//						return HealthCheckResult.unhealthy("database-specific", "Database query returned no results");
//					}
//
//				} catch (SQLException e) {
//					return HealthCheckResult.unhealthy("database-specific", "Database error: " + e.getMessage());
//				}
//
//			} catch (Exception e) {
//				return HealthCheckResult.unhealthy("database-specific", "Health check failed: " + e.getMessage());
//			}
//		}
//	}
//}

/**
 * TestNG integration example
 */
class TestNGIntegrationExample {
	private static final Logger logger = LoggerFactory.getLogger(TestNGIntegrationExample.class);
	private EnterpriseConfigurationManager configManager;
	private WebDriver driver;

	// @BeforeClass
	public void setUpClass() {
		logger.info("Setting up test class...");
		configManager = EnterpriseConfigurationManager.getInstance();

		// Verify system health before starting tests
		if (!configManager.isReadyForExecution()) {
			throw new RuntimeException("System is not ready for test execution");
		}

		logger.info("Test class setup completed");
	}

	// @BeforeMethod
	public void setUp() {
		logger.info("Setting up test method...");

		// Create WebDriver for each test method
		driver = configManager.createDriver();

		// Navigate to base URL
		driver.get(configManager.getBaseUrl());

		logger.info("Test method setup completed");
	}

	// @Test
	public void testExample() {
		logger.info("Running test example...");

		// Get test user
		Map<String, String> testUser = configManager.getTestUser("regular");

		// Perform test operations
		logger.info("Current URL: {}", driver.getCurrentUrl());
		logger.info("Page title: {}", driver.getTitle());

		// Use test data
		logger.info("Testing with user: {}", testUser.get("username"));

		logger.info("Test example completed");
	}

	// @AfterMethod
	public void tearDown() {
		logger.info("Tearing down test method...");

		// Take screenshot if test failed and screenshots are enabled
		if (configManager.shouldTakeScreenshots()) {
			// Implementation would take screenshot here
			logger.info("Screenshot would be taken to: {}", configManager.getScreenshotPath());
		}

		// Quit driver for current test
		configManager.quitDriver();

		logger.info("Test method teardown completed");
	}

	// @AfterClass
	public void tearDownClass() {
		logger.info("Tearing down test class...");

		// Cleanup all resources for this test class
		configManager.quitAllDrivers();
//		configManager.closeAllDatabaseConnections();

		logger.info("Test class teardown completed");
	}
}




/**
 * Best practices and recommendations
 */
class BestPracticesExample {
	private static final Logger logger = LoggerFactory.getLogger(BestPracticesExample.class);

	/**
	 * Demonstrate configuration best practices
	 */
	public static void demonstrateBestPractices() {
		logger.info("=== Configuration Manager Best Practices ===");

		EnterpriseConfigurationManager configManager = EnterpriseConfigurationManager.getInstance();

		// 1. Always check system health before starting tests
		if (!configManager.isReadyForExecution()) {
			logger.error("System not ready - aborting test execution");
			return;
		}

		// 2. Use try-with-resources for database connections
//		try (Connection connection = configManager.getDatabaseConnection()) {
//			logger.info("Database connection managed with try-with-resources");
//			// Database operations here
//		} catch (Exception e) {
//			logger.error("Database operation failed", e);
//		}

		// 3. Create WebDriver at test method level, not class level
		WebDriver driver = null;
		try {
			driver = configManager.createDriver();
			// Test operations here
		} finally {
			// 4. Always cleanup resources in finally block or use automatic cleanup
			if (driver != null) {
				configManager.quitDriver();
			}
		}

		// 5. Use environment-specific configurations
		String environment = configManager.getEnvironment();
		Map<String, String> envSpecificUser = configManager.getTestUser("admin");
		logger.info("Using admin user for environment {}: {}", environment, envSpecificUser);

		// 6. Register custom health checks for critical dependencies
		configManager.registerHealthCheck("external-api", () -> {
			// Check external API health
			return HealthCheckResult.healthy("external-api", "API is responding");
		});

		// 7. Monitor system health regularly
		HealthStatus health = configManager.performHealthCheck();
		if (!health.isHealthy()) {
			logger.warn("Health issues detected: {}", health.getIssues());
		}

		// 8. Use feature flags for environment-specific functionality
		if (configManager.isFeatureEnabled("advancedReporting")) {
			logger.info("Advanced reporting enabled for this environment");
		}

		// 9. Leverage parallel execution capabilities
		if (configManager.isParallelExecution()) {
			int threadCount = configManager.getThreadCount();
			logger.info("Parallel execution enabled with {} threads", threadCount);
		}

		// 10. Export configuration for external tools and debugging
		String configJson = configManager.getConfigurationAsJson();
		logger.info("Configuration exported for debugging: {}", configJson);

		logger.info("Best practices demonstration completed");
		}
	}
}
