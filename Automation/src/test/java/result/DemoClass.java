package result;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;

public class DemoClass {
    private final JsonNode runConfig;
    private final JsonNode applicationConfig;
    
    public DemoClass() {
        this.runConfig = loadConfig("config/run_config.json");
        this.applicationConfig = loadConfig("config/application.json");
    }
    
    private JsonNode loadConfig(String configPath) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configPath)) {
            if (inputStream == null) {
                throw new RuntimeException("Config file not found: " + configPath);
            }
            return new ObjectMapper().readTree(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config: " + configPath, e);
        }
    }
    
    private String getSystemPropertyOrDefault(String propertyName, String defaultValue) {
        String systemValue = System.getProperty(propertyName);
        return systemValue != null ? systemValue : defaultValue;
    }

    public String getEnvironment() {
        return getSystemPropertyOrDefault("environment", 
               runConfig.path("environment").asText("staging"));
    }
    
    public String getBrowser() {
        return getSystemPropertyOrDefault("browser", 
               runConfig.path("browser").asText("chrome"));
    }
    
    public String getBaseUrl() {
        String env = getEnvironment();
        String baseUrl = applicationConfig.path("environments")
                                        .path(env)
                                        .path("baseUrl")
                                        .asText();
        
        if (baseUrl.isEmpty()) {
            throw new RuntimeException("Base URL not configured for environment: " + env);
        }
        
        return baseUrl;
    }
    
    public int getDefaultTimeout() {
        return runConfig.path("timeouts")
                      .path("defaultTimeout")
                      .asInt(30);
    }
    
    public boolean isParallelExecution() {
        return runConfig.path("execution")
                      .path("parallel")
                      .asBoolean(false);
    }
    
    // Add more getter methods as needed for other configuration values
    
    public static void main(String[] args) {
        DemoClass config = new DemoClass();
        
        System.out.println("Environment: " + config.getEnvironment());
        System.out.println("Browser: " + config.getBrowser());
        System.out.println("Base URL: " + config.getBaseUrl());
        System.out.println("Default Timeout: " + config.getDefaultTimeout());
        System.out.println("Parallel Execution: " + config.isParallelExecution());
    }
}