package novus.config.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Immutable timeout configuration holder
 */
public final class TimeoutConfiguration {
	private final int implicitTimeout;
	private final int pageLoadTimeout;
	private final int scriptTimeout;
	private final int pollingInterval;
	private final Map<String, Integer> customTimeouts;

	private TimeoutConfiguration(int implicitTimeout, int pageLoadTimeout, int scriptTimeout, int pollingInterval,
			Map<String, Integer> customTimeouts) {
		this.implicitTimeout = implicitTimeout;
		this.pageLoadTimeout = pageLoadTimeout;
		this.scriptTimeout = scriptTimeout;
		this.pollingInterval = pollingInterval;
		this.customTimeouts = Collections.unmodifiableMap(new HashMap<>(customTimeouts));
	}

	public static TimeoutConfiguration createDefault() {
		return new Builder().build();
	}

	public static TimeoutConfiguration create(int implicitTimeout, int pageLoadTimeout, int scriptTimeout) {
		return new Builder().implicitTimeout(implicitTimeout).pageLoadTimeout(pageLoadTimeout)
				.scriptTimeout(scriptTimeout).build();
	}

	// Getters
	public int getImplicitTimeout() {
		return implicitTimeout;
	}

	public int getPageLoadTimeout() {
		return pageLoadTimeout;
	}

	public int getScriptTimeout() {
		return scriptTimeout;
	}

	public int getPollingInterval() {
		return pollingInterval;
	}

	public Map<String, Integer> getCustomTimeouts() {
		return customTimeouts;
	}

	public int getCustomTimeout(String type, int defaultValue) {
		return customTimeouts.getOrDefault(type, defaultValue);
	}

	public static class Builder {
		private int implicitTimeout = DriverConfiguration.DEFAULT_IMPLICIT_TIMEOUT;
		private int pageLoadTimeout = DriverConfiguration.DEFAULT_PAGE_LOAD_TIMEOUT;
		private int scriptTimeout = DriverConfiguration.DEFAULT_SCRIPT_TIMEOUT;
		private int pollingInterval = 500;
		private Map<String, Integer> customTimeouts = new HashMap<>();

		public Builder implicitTimeout(int timeout) {
			this.implicitTimeout = timeout;
			return this;
		}

		public Builder pageLoadTimeout(int timeout) {
			this.pageLoadTimeout = timeout;
			return this;
		}

		public Builder scriptTimeout(int timeout) {
			this.scriptTimeout = timeout;
			return this;
		}

		public Builder pollingInterval(int interval) {
			this.pollingInterval = interval;
			return this;
		}

		public Builder customTimeout(String type, int timeout) {
			this.customTimeouts.put(type, timeout);
			return this;
		}

		public TimeoutConfiguration build() {
			return new TimeoutConfiguration(implicitTimeout, pageLoadTimeout, scriptTimeout, pollingInterval,
					customTimeouts);
		}
	}
}
