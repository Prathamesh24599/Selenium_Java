package novus.config.models;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Overall health status model
 */
public final class HealthStatus {
	private final boolean healthy;
	private final List<HealthCheckResult> results;
	private final long totalExecutionTimeMs;

	public HealthStatus(boolean healthy, List<HealthCheckResult> results, long totalExecutionTimeMs) {
		this.healthy = healthy;
		this.results = Collections.unmodifiableList(results);
		this.totalExecutionTimeMs = totalExecutionTimeMs;
	}

	// Getters
	public boolean isHealthy() {
		return healthy;
	}

	public List<HealthCheckResult> getResults() {
		return results;
	}

	public long getTotalExecutionTimeMs() {
		return totalExecutionTimeMs;
	}

	public List<String> getIssues() {
	    return results.stream()
	        .filter(result -> !result.isHealthy())
	        .map(result -> result.getName() + ": " + result.getMessage())
	        .collect(Collectors.toList());
	}
}

