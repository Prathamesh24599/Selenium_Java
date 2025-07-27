package novus.config.config_interfaces;

import novus.config.models.HealthCheckResult;

/**
 * Health check functional interface
 */
@FunctionalInterface
public interface HealthCheck {
    HealthCheckResult check();
}
