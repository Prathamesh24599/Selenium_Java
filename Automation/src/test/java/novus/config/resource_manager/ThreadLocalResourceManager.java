package novus.config.resource_manager;

import java.sql.Connection;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thread-local resource manager for thread-specific resource management
 */
public class ThreadLocalResourceManager {
	private static final Logger logger = LoggerFactory.getLogger(ThreadLocalResourceManager.class);

	private static final ThreadLocal<EnterpriseResourceManager> threadLocalManager = ThreadLocal.withInitial(() -> {
		EnterpriseResourceManager manager = new EnterpriseResourceManager();
		manager.registerShutdownHook();
		return manager;
	});

	/**
	 * Get resource manager for current thread
	 */
	public static EnterpriseResourceManager getInstance() {
		return threadLocalManager.get();
	}

	/**
	 * Register WebDriver for current thread
	 */
	public static void registerWebDriver(WebDriver driver) {
		String key = "webdriver-" + Thread.currentThread().threadId();
		getInstance().registerResource(key, (AutoCloseable)driver);
		logger.debug("Registered WebDriver for thread: {}", Thread.currentThread().threadId());
	}

	/**
	 * Get WebDriver for current thread
	 */
	public static WebDriver getCurrentWebDriver() {
		String key = "webdriver-" + Thread.currentThread().threadId();
		EnterpriseResourceManager manager = getInstance();

		if (manager.hasResource(key)) {
			// This is a simplified approach - in a real implementation,
			// you'd need a way to retrieve the actual resource
			logger.debug("WebDriver found for thread: {}", Thread.currentThread().threadId());
		}

		return null; // Would return actual WebDriver in real implementation
	}

	/**
	 * Release WebDriver for current thread
	 */
	public static void releaseCurrentWebDriver() {
		String key = "webdriver-" + Thread.currentThread().threadId();
		getInstance().releaseResource(key);
		logger.debug("Released WebDriver for thread: {}", Thread.currentThread().threadId());
	}

	/**
	 * Register database connection for current thread
	 */
//	public static void registerDatabaseConnection(String environmentKey, Connection connection) {
//		String key = "db-" + environmentKey + "-" + Thread.currentThread().getId();
//		getInstance().registerResource(key, connection);
//		logger.debug("Registered database connection for thread: {} environment: {}", Thread.currentThread().getId(),
//				environmentKey);
//	}

	/**
	 * Release database connection for current thread
	 */
	public static void releaseDatabaseConnection(String environmentKey) {
		String key = "db-" + environmentKey + "-" + Thread.currentThread().threadId();
		getInstance().releaseResource(key);
		logger.debug("Released database connection for thread: {} environment: {}", Thread.currentThread().threadId(),
				environmentKey);
	}

	/**
	 * Release all resources for current thread
	 */
	public static void releaseAllForCurrentThread() {
		getInstance().releaseAllResources();
		threadLocalManager.remove();
		logger.debug("Released all resources for thread: {}", Thread.currentThread().threadId());
	}

	/**
	 * Get resource statistics for current thread
	 */
	public static Map<String, Object> getCurrentThreadStatistics() {
		return getInstance().getResourceStatistics();
	}
}

