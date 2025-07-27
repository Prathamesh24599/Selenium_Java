package novus.config.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check result model
 */
public final class HealthCheckResult {
	private final boolean healthy;
	private final String name;
	private final String message;
	private final long executionTimeMs;
	private final Map<String, Object> details;

	public HealthCheckResult(boolean healthy, String name, String message, long executionTimeMs,
			Map<String, Object> details) {
		this.healthy = healthy;
		this.name = name;
		this.message = message;
		this.executionTimeMs = executionTimeMs;
		this.details = Collections.unmodifiableMap(new HashMap<>(details));
	}

	public static HealthCheckResult healthy(String name, String message) {
		return new HealthCheckResult(true, name, message, 0, new HashMap<>());
	}

	public static HealthCheckResult unhealthy(String name, String message) {
		return new HealthCheckResult(false, name, message, 0, new HashMap<>());
	}

	// Getters
	public boolean isHealthy() {
		return healthy;
	}

	public String getName() {
		return name;
	}

	public String getMessage() {
		return message;
	}

	public long getExecutionTimeMs() {
		return executionTimeMs;
	}

	public Map<String, Object> getDetails() {
		return details;
	}
}

