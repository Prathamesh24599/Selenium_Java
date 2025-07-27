package novus.config.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import novus.config.models.PerformanceConfiguration.Builder;

/**
 * Performance monitoring configuration
 */
public final class PerformanceConfiguration {
	private final boolean loggingEnabled;
	private final boolean networkThrottlingEnabled;
	private final boolean cpuThrottlingEnabled;
	private final Map<String, Object> networkConditions;
	private final Map<String, Object> performanceMetrics;

	private PerformanceConfiguration(boolean loggingEnabled, boolean networkThrottlingEnabled,
			boolean cpuThrottlingEnabled, Map<String, Object> networkConditions,
			Map<String, Object> performanceMetrics) {
		this.loggingEnabled = loggingEnabled;
		this.networkThrottlingEnabled = networkThrottlingEnabled;
		this.cpuThrottlingEnabled = cpuThrottlingEnabled;
		this.networkConditions = Collections.unmodifiableMap(new HashMap<>(networkConditions));
		this.performanceMetrics = Collections.unmodifiableMap(new HashMap<>(performanceMetrics));
	}

	public static PerformanceConfiguration createDefault() {
		return new Builder().build();
	}

	// Getters
	public boolean isLoggingEnabled() {
		return loggingEnabled;
	}

	public boolean isNetworkThrottlingEnabled() {
		return networkThrottlingEnabled;
	}

	public boolean isCpuThrottlingEnabled() {
		return cpuThrottlingEnabled;
	}

	public Map<String, Object> getNetworkConditions() {
		return networkConditions;
	}

	public Map<String, Object> getPerformanceMetrics() {
		return performanceMetrics;
	}

	public static class Builder {
		private boolean loggingEnabled = false;
		private boolean networkThrottlingEnabled = false;
		private boolean cpuThrottlingEnabled = false;
		private Map<String, Object> networkConditions = new HashMap<>();
		private Map<String, Object> performanceMetrics = new HashMap<>();

		public Builder loggingEnabled(boolean enabled) {
			this.loggingEnabled = enabled;
			return this;
		}

		public Builder networkThrottlingEnabled(boolean enabled) {
			this.networkThrottlingEnabled = enabled;
			return this;
		}

		public Builder cpuThrottlingEnabled(boolean enabled) {
			this.cpuThrottlingEnabled = enabled;
			return this;
		}

		public Builder networkCondition(String key, Object value) {
			this.networkConditions.put(key, value);
			return this;
		}

		public Builder performanceMetric(String key, Object value) {
			this.performanceMetrics.put(key, value);
			return this;
		}

		public PerformanceConfiguration build() {
			return new PerformanceConfiguration(loggingEnabled, networkThrottlingEnabled, cpuThrottlingEnabled,
					networkConditions, performanceMetrics);
		}
	}
}


