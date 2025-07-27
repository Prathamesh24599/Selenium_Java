package novus.config.config_interfaces;

import java.util.Map;

/**
 * Resource manager for automatic cleanup
 */
public interface ResourceManager {
	void registerResource(String key, AutoCloseable resource);
    void releaseResource(String key);
    void releaseAllResources();
    void registerShutdownHook();
	void cleanupResourcesByType(String string);
	Map<String, Object> getResourceStatistics();
}

