package novus.config.resource_manager;

//===============================
//RESOURCE MANAGER IMPLEMENTATION
//===============================

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import novus.config.config_interfaces.ResourceManager;

import java.io.FileInputStream;
import java.net.Socket;
import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Enterprise resource manager for automatic cleanup and monitoring
 */
public class EnterpriseResourceManager implements ResourceManager {
	private static final Logger logger = LoggerFactory.getLogger(EnterpriseResourceManager.class);

	// Resource registry
	private final Map<String, AutoCloseable> resources = new ConcurrentHashMap<>();
	private final Map<String, Long> resourceCreationTime = new ConcurrentHashMap<>();
	private final Map<String, String> resourceTypes = new ConcurrentHashMap<>();

	// Monitoring
	private final ScheduledExecutorService monitor = Executors.newSingleThreadScheduledExecutor(r -> {
		Thread t = new Thread(r, "ResourceManager-Monitor");
		t.setDaemon(true);
		return t;
	});

	// Configuration
	private static final long MONITORING_INTERVAL_MS = 30000; // 30 seconds
	private static final long MAX_RESOURCE_AGE_MS = 1800000; // 30 minutes
	private volatile boolean shutdownHookRegistered = false;

	public EnterpriseResourceManager() {
		startMonitoring();
		logger.info("Enterprise Resource Manager initialized");
	}

	@Override
	public void registerResource(String key, AutoCloseable resource) {
	    if (key == null || key.trim().isEmpty()) {
	        throw new IllegalArgumentException("Resource key cannot be null or empty");
	    }
	    if (resource == null) {
	        throw new IllegalArgumentException("Resource cannot be null");
	    }

	    // Close existing resource if present
	    AutoCloseable existing = resources.get(key);
	    if (existing != null) {
	        logger.warn("Replacing existing resource with key: {}", key);
	        closeResourceSafely(key, existing);
	    }

	    resources.put(key, resource);
	    resourceCreationTime.put(key, System.currentTimeMillis());
	    resourceTypes.put(key, determineResourceType(resource));

	    logger.debug("Registered resource: {} (type: {})", key, resourceTypes.get(key));
	}

	// Helper method to determine resource type
	private String determineResourceType(AutoCloseable resource) {
	    if (resource instanceof WebDriver) {
	        return "WebDriver";
	    } else if (resource instanceof Connection) { // java.sql.Connection
	        return "DatabaseConnection";
	    } else if (resource instanceof FileInputStream) {
	        return "FileInputStream";
	    } else if (resource instanceof Socket) {
	        return "Socket";
	    } else {
	        return resource.getClass().getSimpleName();
	    }
	}

	// Convenience method for WebDriver registration (optional)
	public void registerWebDriver(String key, WebDriver driver) {
	    // Handle the case where WebDriver might not implement AutoCloseable properly
	    if (driver instanceof AutoCloseable) {
	        registerResource(key, (AutoCloseable) driver);
	    } else {
	        // Create wrapper for older Selenium versions
	        AutoCloseable wrapper = () -> {
	            if (driver != null) {
	                driver.quit();
	            }
	        };
	        registerResource(key, wrapper);
	    }
	}

	@Override
	public void releaseResource(String key) {
		AutoCloseable resource = resources.remove(key);
		if (resource != null) {
			closeResourceSafely(key, resource);
			resourceCreationTime.remove(key);
			resourceTypes.remove(key);
			logger.info("Released resource: {}", key);
		} else {
			logger.debug("Resource not found for key: {}", key);
		}
	}

	@Override
	public void releaseAllResources() {
		logger.info("Releasing {} resources...", resources.size());

		resources.forEach((key, resource) -> closeResourceSafely(key, resource));

		resources.clear();
		resourceCreationTime.clear();
		resourceTypes.clear();

		// Shutdown monitor
		monitor.shutdown();
		try {
			if (!monitor.awaitTermination(5, TimeUnit.SECONDS)) {
				monitor.shutdownNow();
			}
		} catch (InterruptedException e) {
			monitor.shutdownNow();
			Thread.currentThread().interrupt();
		}

		logger.info("All resources released successfully");
	}

	@Override
	public void registerShutdownHook() {
		if (!shutdownHookRegistered) {
			synchronized (this) {
				if (!shutdownHookRegistered) {
					Runtime.getRuntime().addShutdownHook(new Thread(() -> {
						logger.info("Resource Manager shutdown hook executing...");
						releaseAllResources();
					}, "ResourceManager-ShutdownHook"));

					shutdownHookRegistered = true;
					logger.debug("Shutdown hook registered");
				}
			}
		}
	}

	/**
	 * Start resource monitoring
	 */
	private void startMonitoring() {
		monitor.scheduleAtFixedRate(this::performResourceMonitoring, MONITORING_INTERVAL_MS, MONITORING_INTERVAL_MS,
				TimeUnit.MILLISECONDS);

		logger.debug("Resource monitoring started");
	}

	/**
	 * Perform resource monitoring and cleanup
	 */
	private void performResourceMonitoring() {
		try {
			logger.debug("Performing resource monitoring check...");

			long currentTime = System.currentTimeMillis();
			Map<String, String> staleResources = new ConcurrentHashMap<>();

			// Check for stale resources
			resourceCreationTime.forEach((key, creationTime) -> {
				if ((currentTime - creationTime) > MAX_RESOURCE_AGE_MS) {
					staleResources.put(key, resourceTypes.get(key));
				}
			});

			// Clean up stale resources
			if (!staleResources.isEmpty()) {
				logger.warn("Found {} stale resources to clean up", staleResources.size());
				staleResources.forEach((key, type) -> {
					logger.warn("Cleaning up stale resource: {} (type: {}, age: {}ms)", key, type,
							currentTime - resourceCreationTime.get(key));
					releaseResource(key);
				});
			}

			// Check resource validity for WebDrivers
			checkWebDriverValidity();

			// Check database connection validity
			checkDatabaseConnectionValidity();

		} catch (Exception e) {
			logger.error("Error during resource monitoring", e);
		}
	}

	/**
	 * Check WebDriver validity
	 */
	private void checkWebDriverValidity() {
		resources.entrySet().stream().filter(entry -> entry.getValue() instanceof WebDriver).forEach(entry -> {
			String key = entry.getKey();
			WebDriver driver = (WebDriver) entry.getValue();

			try {
				// Simple validity test
				driver.getCurrentUrl();
			} catch (Exception e) {
				logger.warn("WebDriver {} appears to be invalid, cleaning up: {}", key, e.getMessage());
				releaseResource(key);
			}
		});
	}

	/**
	 * Check database connection validity
	 */
	private void checkDatabaseConnectionValidity() {
		resources.entrySet().stream().filter(entry -> entry.getValue() instanceof Connection).forEach(entry -> {
			String key = entry.getKey();
			Connection connection = (Connection) entry.getValue();

			try {
				if (connection.isClosed() || !connection.isValid(5)) {
					logger.warn("Database connection {} is invalid, cleaning up", key);
					releaseResource(key);
				}
			} catch (Exception e) {
				logger.warn("Error checking database connection {}, cleaning up: {}", key, e.getMessage());
				releaseResource(key);
			}
		});
	}

	/**
	 * Close resource safely
	 */
	private void closeResourceSafely(String key, AutoCloseable resource) {
		try {
			String resourceType = determineResourceType(resource);
			logger.debug("Closing {} resource: {}", resourceType, key);

			if (resource instanceof WebDriver) {
				((WebDriver) resource).quit();
			} else {
				resource.close();
			}

		} catch (Exception e) {
			logger.error("Error closing resource {}: {}", key, e.getMessage());
		}
	}


	/**
	 * Get resource statistics
	 */
	public Map<String, Object> getResourceStatistics() {
		Map<String, Object> stats = new ConcurrentHashMap<>();

		// Count by type
		Map<String, Integer> typeCounts = new ConcurrentHashMap<>();
		resourceTypes.values().forEach(type -> typeCounts.merge(type, 1, Integer::sum));

		stats.put("totalResources", resources.size());
		stats.put("resourcesByType", typeCounts);
		stats.put("monitoringIntervalMs", MONITORING_INTERVAL_MS);
		stats.put("maxResourceAgeMs", MAX_RESOURCE_AGE_MS);
		stats.put("shutdownHookRegistered", shutdownHookRegistered);

		return stats;
	}

	/**
	 * Get resource age in milliseconds
	 */
	public long getResourceAge(String key) {
		Long creationTime = resourceCreationTime.get(key);
		if (creationTime == null) {
			return -1;
		}
		return System.currentTimeMillis() - creationTime;
	}

	/**
	 * Check if resource exists
	 */
	public boolean hasResource(String key) {
		return resources.containsKey(key);
	}

	/**
	 * Get resource type
	 */
	public String getResourceType(String key) {
		return resourceTypes.get(key);
	}

	/**
	 * Get all resource keys
	 */
	public java.util.Set<String> getResourceKeys() {
		return resources.keySet();
	}

	/**
	 * Force cleanup of resources by type
	 */
	public void cleanupResourcesByType(String resourceType) {
		logger.info("Cleaning up all resources of type: {}", resourceType);

		resources.entrySet().stream().filter(entry -> resourceType.equals(resourceTypes.get(entry.getKey())))
				.map(Map.Entry::getKey).forEach(this::releaseResource);
	}

	/**
	 * Get resource creation time
	 */
	public long getResourceCreationTime(String key) {
		return resourceCreationTime.getOrDefault(key, 0L);
	}
}

