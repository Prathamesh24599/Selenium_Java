package novus.config.config_interfaces;

import java.util.List;

import novus.config.models.HealthStatus;

/**
 * System health monitoring interface
 */
public interface SystemHealthChecker {
    HealthStatus performHealthCheck();
    boolean isSystemHealthy();
    List<String> getHealthIssues();
    void registerHealthCheck(String name, HealthCheck check);
}
