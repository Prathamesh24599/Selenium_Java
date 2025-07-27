package novus.config.config_interfaces;

import novus.config.models.ProxyConfiguration;
import novus.config.models.TimeoutConfiguration;

/**
 * Configuration properties interface for type-safe access
 */
public interface ConfigurationProperties {
	String getBrowserName();

	String getEnvironment();

	String getBaseUrl();

	boolean isRemoteExecution();

	boolean isHeadlessMode();

	String getMobileDevice();

	int getThreadCount();

	TimeoutConfiguration getTimeouts();

	ProxyConfiguration getProxy();
}
