package novus.config.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;



/**
 * Immutable proxy configuration holder
 */
public final class ProxyConfiguration {
	private final boolean enabled;
	private final String httpProxy;
	private final String sslProxy;
	private final String ftpProxy;
	private final String noProxy;
	private final String proxyType;
	private final Map<String, String> customProxySettings;

	private ProxyConfiguration(boolean enabled, String httpProxy, String sslProxy, String ftpProxy, String noProxy,
			String proxyType, Map<String, String> customProxySettings) {
		this.enabled = enabled;
		this.httpProxy = httpProxy;
		this.sslProxy = sslProxy;
		this.ftpProxy = ftpProxy;
		this.noProxy = noProxy;
		this.proxyType = proxyType;
		this.customProxySettings = Collections.unmodifiableMap(new HashMap<>(customProxySettings));
	}

	public static ProxyConfiguration createDefault() {
		return new Builder().build();
	}

	public static ProxyConfiguration createDisabled() {
		return new Builder().enabled(false).build();
	}

	// Getters
	public boolean isEnabled() {
		return enabled;
	}

	public String getHttpProxy() {
		return httpProxy;
	}

	public String getSslProxy() {
		return sslProxy;
	}

	public String getFtpProxy() {
		return ftpProxy;
	}

	public String getNoProxy() {
		return noProxy;
	}

	public String getProxyType() {
		return proxyType;
	}

	public Map<String, String> getCustomProxySettings() {
		return customProxySettings;
	}

	public static class Builder {
		private boolean enabled = false;
		private String httpProxy = "";
		private String sslProxy = "";
		private String ftpProxy = "";
		private String noProxy = "";
		private String proxyType = "MANUAL";
		private Map<String, String> customProxySettings = new HashMap<>();

		public Builder enabled(boolean enabled) {
			this.enabled = enabled;
			return this;
		}

		public Builder httpProxy(String httpProxy) {
			this.httpProxy = httpProxy;
			return this;
		}

		public Builder sslProxy(String sslProxy) {
			this.sslProxy = sslProxy;
			return this;
		}

		public Builder ftpProxy(String ftpProxy) {
			this.ftpProxy = ftpProxy;
			return this;
		}

		public Builder noProxy(String noProxy) {
			this.noProxy = noProxy;
			return this;
		}

		public Builder proxyType(String proxyType) {
			this.proxyType = proxyType;
			return this;
		}

		public Builder customProxySetting(String key, String value) {
			this.customProxySettings.put(key, value);
			return this;
		}

		public ProxyConfiguration build() {
			return new ProxyConfiguration(enabled, httpProxy, sslProxy, ftpProxy, noProxy, proxyType,
					customProxySettings);
		}
	}
}
