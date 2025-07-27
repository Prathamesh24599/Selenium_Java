package novus.config.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;



/**
 * Security configuration
 */
public final class SecurityConfiguration {
	private final boolean encryptionEnabled;
	private final String encryptionAlgorithm;
	private final int keyLength;
	private final boolean certificateValidationEnabled;
	private final boolean insecureCertsAllowed;
	private final Map<String, String> securityHeaders;

	private SecurityConfiguration(boolean encryptionEnabled, String encryptionAlgorithm, int keyLength,
			boolean certificateValidationEnabled, boolean insecureCertsAllowed, Map<String, String> securityHeaders) {
		this.encryptionEnabled = encryptionEnabled;
		this.encryptionAlgorithm = encryptionAlgorithm;
		this.keyLength = keyLength;
		this.certificateValidationEnabled = certificateValidationEnabled;
		this.insecureCertsAllowed = insecureCertsAllowed;
		this.securityHeaders = Collections.unmodifiableMap(new HashMap<>(securityHeaders));
	}

	public static SecurityConfiguration createDefault() {
		return new Builder().build();
	}

	// Getters
	public boolean isEncryptionEnabled() {
		return encryptionEnabled;
	}

	public String getEncryptionAlgorithm() {
		return encryptionAlgorithm;
	}

	public int getKeyLength() {
		return keyLength;
	}

	public boolean isCertificateValidationEnabled() {
		return certificateValidationEnabled;
	}

	public boolean isInsecureCertsAllowed() {
		return insecureCertsAllowed;
	}

	public Map<String, String> getSecurityHeaders() {
		return securityHeaders;
	}

	public static class Builder {
		private boolean encryptionEnabled = false;
		private String encryptionAlgorithm = "AES-256";
		private int keyLength = 256;
		private boolean certificateValidationEnabled = true;
		private boolean insecureCertsAllowed = false;
		private Map<String, String> securityHeaders = new HashMap<>();

		public Builder encryptionEnabled(boolean enabled) {
			this.encryptionEnabled = enabled;
			return this;
		}

		public Builder encryptionAlgorithm(String algorithm) {
			this.encryptionAlgorithm = algorithm;
			return this;
		}

		public Builder keyLength(int length) {
			this.keyLength = length;
			return this;
		}

		public Builder certificateValidationEnabled(boolean enabled) {
			this.certificateValidationEnabled = enabled;
			return this;
		}

		public Builder insecureCertsAllowed(boolean allowed) {
			this.insecureCertsAllowed = allowed;
			return this;
		}

		public Builder securityHeader(String key, String value) {
			this.securityHeaders.put(key, value);
			return this;
		}

		public SecurityConfiguration build() {
			return new SecurityConfiguration(encryptionEnabled, encryptionAlgorithm, keyLength,
					certificateValidationEnabled, insecureCertsAllowed, securityHeaders);
		}
	}
}
