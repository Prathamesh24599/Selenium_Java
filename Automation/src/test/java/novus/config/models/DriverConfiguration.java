package novus.config.models;

//===============================
//CONFIGURATION DATA MODELS
//===============================


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable configuration holder for driver settings using Builder pattern
 */
public final class DriverConfiguration {
	// Configuration constants
	public static final int DEFAULT_IMPLICIT_TIMEOUT = 10;
	public static final int DEFAULT_PAGE_LOAD_TIMEOUT = 30;
	public static final int DEFAULT_SCRIPT_TIMEOUT = 20;
	public static final String DEFAULT_BROWSER = "chrome";
	public static final boolean DEFAULT_HEADLESS = false;
	public static final boolean DEFAULT_REMOTE = false;

	private final String browserName;
	private final boolean headless;
	private final boolean remote;
	private final String mobileDevice;
	private final Map<String, Object> customCapabilities;
	private final TimeoutConfiguration timeouts;
	private final ProxyConfiguration proxy;
	private final PerformanceConfiguration performance;
	private final SecurityConfiguration security;

	private DriverConfiguration(Builder builder) {
		this.browserName = builder.browserName;
		this.headless = builder.headless;
		this.remote = builder.remote;
		this.mobileDevice = builder.mobileDevice;
		this.customCapabilities = Collections.unmodifiableMap(new HashMap<>(builder.customCapabilities));
		this.timeouts = builder.timeouts;
		this.proxy = builder.proxy;
		this.performance = builder.performance;
		this.security = builder.security;
	}

	// Getters
	public String getBrowserName() {
		return browserName;
	}

	public boolean isHeadless() {
		return headless;
	}

	public boolean isRemote() {
		return remote;
	}

	public String getMobileDevice() {
		return mobileDevice;
	}

	public Map<String, Object> getCustomCapabilities() {
		return customCapabilities;
	}

	public TimeoutConfiguration getTimeouts() {
		return timeouts;
	}

	public ProxyConfiguration getProxy() {
		return proxy;
	}

	public PerformanceConfiguration getPerformance() {
		return performance;
	}

	public SecurityConfiguration getSecurity() {
		return security;
	}

	public static class Builder {
		private String browserName = DEFAULT_BROWSER;
		private boolean headless = DEFAULT_HEADLESS;
		private boolean remote = DEFAULT_REMOTE;
		private String mobileDevice = "";
		private Map<String, Object> customCapabilities = new HashMap<>();
		private TimeoutConfiguration timeouts = TimeoutConfiguration.createDefault();
		private ProxyConfiguration proxy = ProxyConfiguration.createDefault();
		private PerformanceConfiguration performance = PerformanceConfiguration.createDefault();
		private SecurityConfiguration security = SecurityConfiguration.createDefault();

		public Builder browserName(String browserName) {
			this.browserName = browserName;
			return this;
		}

		public Builder headless(boolean headless) {
			this.headless = headless;
			return this;
		}

		public Builder remote(boolean remote) {
			this.remote = remote;
			return this;
		}

		public Builder mobileDevice(String mobileDevice) {
			this.mobileDevice = mobileDevice;
			return this;
		}

		public Builder customCapabilities(Map<String, Object> capabilities) {
			this.customCapabilities.putAll(capabilities);
			return this;
		}

		public Builder timeouts(TimeoutConfiguration timeouts) {
			this.timeouts = timeouts;
			return this;
		}

		public Builder proxy(ProxyConfiguration proxy) {
			this.proxy = proxy;
			return this;
		}

		public Builder performance(PerformanceConfiguration performance) {
			this.performance = performance;
			return this;
		}

		public Builder security(SecurityConfiguration security) {
			this.security = security;
			return this;
		}

		public DriverConfiguration build() {
			return new DriverConfiguration(this);
		}
	}
}


