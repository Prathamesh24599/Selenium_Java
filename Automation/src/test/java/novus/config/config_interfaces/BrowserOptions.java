package novus.config.config_interfaces;

import java.util.Map;

import novus.config.models.DriverConfiguration;

/**
 * Browser-specific options builder interface
 */
public interface BrowserOptions<T> {
    T buildOptions(DriverConfiguration config);
    T buildHeadlessOptions(DriverConfiguration config);
    T buildMobileOptions(DriverConfiguration config, String deviceName);
    void applyCustomCapabilities(T options, Map<String, Object> capabilities);
}