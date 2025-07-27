package novus.config.config_interfaces;

public class CustomExceptions {
	// ===============================
	// EXCEPTION CLASSES
	// ===============================

	public static class ConfigurationException extends RuntimeException {
	    public ConfigurationException(String message) {
	        super(message);
	    }
	    
	    public ConfigurationException(String message, Throwable cause) {
	        super(message, cause);
	    }
	}

	public static class DriverCreationException extends RuntimeException {
	    public DriverCreationException(String message) {
	        super(message);
	    }
	    
	    public DriverCreationException(String message, Throwable cause) {
	        super(message, cause);
	    }
	}
}
