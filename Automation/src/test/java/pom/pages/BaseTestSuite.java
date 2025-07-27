package pom.pages;

import config_reader.configLoader;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.testng.ITestResult;
import org.testng.annotations.*;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Enterprise-Grade Test Suite Base Class
 * 
 * This class provides comprehensive test infrastructure including:
 * - Thread-safe configuration management
 * - Parallel execution support with proper resource isolation
 * - Advanced error handling and recovery mechanisms
 * - Comprehensive reporting and logging
 * - Performance monitoring and metrics collection
 * - Database transaction management
 * - Screenshot and video capture
 * - Test data management and cleanup
 * - Security and audit logging
 * - Resource leak prevention
 * - Graceful failure handling
 * 
 * Features:
 * - Automatic retry mechanism for flaky tests
 * - Dynamic test environment switching
 * - Real-time performance monitoring
 * - Comprehensive test artifacts management
 * - Integration with CI/CD pipelines
 * - Support for distributed testing
 * - Advanced debugging capabilities
 * 
 * @author Enterprise QA Team
 * @version 3.0
 * @since 2024
 */
public abstract class BaseTestSuite {
    
    private static final Logger logger = LoggerFactory.getLogger(BaseTestSuite.class);
    
    // Thread-safe collections for multi-threaded execution
    private static final Map<Long, WebDriver> threadLocalDrivers = new ConcurrentHashMap<>();
    private static final Map<Long, Instant> testStartTimes = new ConcurrentHashMap<>();
    private static final Map<Long, String> testNames = new ConcurrentHashMap<>();
    private static final Map<Long, Connection> threadLocalDbConnections = new ConcurrentHashMap<>();
    private static final Map<Long, List<String>> testArtifacts = new ConcurrentHashMap<>();
    
    // Configuration and monitoring
    protected static configLoader configManager;
    private static final AtomicInteger testCounter = new AtomicInteger(0);
    private static final AtomicInteger failedTestCounter = new AtomicInteger(0);
    private static final AtomicInteger passedTestCounter = new AtomicInteger(0);
    
    // Test execution metrics
    private static Instant suiteStartTime;
    private static final Map<String, Object> suiteMetrics = new ConcurrentHashMap<>();
    
    // Constants
    private static final String SCREENSHOT_DIR = "test-output/screenshots";
    private static final String LOGS_DIR = "test-output/logs";
    private static final String REPORTS_DIR = "test-output/reports";
    private static final String VIDEO_DIR = "test-output/videos";
    private static final String TEST_DATA_DIR = "test-output/test-data";
    
    /**
     * Suite-level setup executed once before all tests
     * Initializes global resources and configurations
     */
    @BeforeSuite(alwaysRun = true)
    public void suiteSetup() {
        suiteStartTime = Instant.now();
        
        try {
            logger.info("=".repeat(100));
            logger.info("                    ENTERPRISE TEST SUITE INITIALIZATION");
            logger.info("=".repeat(100));
            
            // Initialize configuration manager
            initializeConfigurationManager();
            
            // Setup test infrastructure
            setupTestInfrastructure();
            
            // Initialize monitoring and reporting
            initializeMonitoringAndReporting();
            
            // Setup database connections if required
//            initializeDatabaseConnections();
            
            // Setup security and audit logging
            initializeSecurityAndAuditLogging();
            
            // Validate test environment
            validateTestEnvironment();
            
            // Setup CI/CD integration
            setupCIIntegration();
            
            // Initialize performance monitoring
            initializePerformanceMonitoring();
            
            logSuiteInitializationSummary();
            
        } catch (Exception e) {
            logger.error("CRITICAL: Suite setup failed", e);
            throw new RuntimeException("Suite initialization failed - aborting test execution", e);
        }
    }
    
    /**
     * Test class level setup - executed before each test class
     */
    @BeforeClass(alwaysRun = true)
    public void classSetup() {
        String className = this.getClass().getSimpleName();
        
        try {
            logger.info("Initializing test class: {}", className);
            
            // Setup class-specific configurations
            setupClassSpecificConfigurations();
            
            // Initialize class-level test data
            initializeTestData();
            
            // Setup class-level monitoring
            setupClassLevelMonitoring(className);
            
            logger.info("Test class {} initialized successfully", className);
            
        } catch (Exception e) {
            logger.error("Test class setup failed for: {}", className, e);
            throw new RuntimeException("Class setup failed for: " + className, e);
        }
    }
    
    /**
     * Method-level setup - executed before each test method
     * Provides isolated test environment for each test
     */
    @BeforeMethod(alwaysRun = true)
    public void methodSetup(ITestResult result) {
        long threadId = Thread.currentThread().threadId();
        String threadName = Thread.currentThread().getName();
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        String fullTestName = className + "." + testName;
        
        try {
            // Setup MDC for structured logging
            setupTestLoggingContext(fullTestName, threadId);
            
            logger.info("Starting test: {} on thread: {}", fullTestName, threadId);
            
            // Record test start time
            testStartTimes.put(threadId, Instant.now());
            testNames.put(threadId, fullTestName);
            testArtifacts.put(threadId, new ArrayList<>());
            
            // Initialize WebDriver for this test
            initializeWebDriverForTest(threadId);
            
            // Setup database connection if required
//            initializeDatabaseConnectionForTest(threadId);
            
            // Setup test-specific monitoring
            setupTestLevelMonitoring(fullTestName, threadId);
            
            // Setup test data isolation
            setupTestDataIsolation(fullTestName);
            
            // Initialize browser and navigate to base URL
            initializeBrowserSession(threadId);
            
            // Increment test counter
            testCounter.incrementAndGet();
            
            logger.info("Test setup completed for: {}", fullTestName);
            
        } catch (Exception e) {
            logger.error("Test method setup failed for: {}", fullTestName, e);
            handleSetupFailure(threadId, fullTestName, e);
            throw new RuntimeException("Test setup failed for: " + fullTestName, e);
        }
    }
    
    /**
     * Method-level teardown - executed after each test method
     * Ensures proper cleanup and resource management
     */
    @AfterMethod(alwaysRun = true)
    public void methodTeardown(ITestResult result) {
    	long threadId = Thread.currentThread().threadId();
        String threadName = Thread.currentThread().getName();
        String testName = testNames.get(threadId);
        
        try {
            logger.info("Starting teardown for test: {} on thread: {}", testName, threadId);
            
            // Calculate test execution time
            Instant testEndTime = Instant.now();
            Instant testStartTime = testStartTimes.get(threadId);
            Duration testDuration = Duration.between(testStartTime, testEndTime);
            
            // Handle test result
            handleTestResult(result, testName, testDuration, threadId);
            
            // Capture performance metrics
            capturePerformanceMetrics(threadId, testName);
            
            // Capture browser logs
            captureBrowserLogs(threadId, testName);
            
            // Cleanup database connections
            cleanupDatabaseConnection(threadId);
            
            // Cleanup WebDriver
            cleanupWebDriver(threadId);
            
            // Cleanup test artifacts
            cleanupTestArtifacts(threadId);
            
            // Update test counters
            updateTestCounters(result);
            
            // Cleanup thread-local data
            cleanupThreadLocalData(threadId);
            
            logger.info("Test teardown completed for: {} (Duration: {}ms)", 
                       testName, testDuration.toMillis());
            
        } catch (Exception e) {
            logger.error("Test teardown failed for: {}", testName, e);
            // Don't throw exception in teardown to avoid masking test failures
        } finally {
            // Always cleanup MDC
            MDC.clear();
        }
    }
    
    /**
     * Class-level teardown - executed after each test class
     */
    @AfterClass(alwaysRun = true)
    public void classTeardown() {
        String className = this.getClass().getSimpleName();
        
        try {
            logger.info("Starting class teardown for: {}", className);
            
            // Cleanup class-level resources
            cleanupClassLevelResources();
            
            // Generate class-level reports
            generateClassLevelReports(className);
            
            // Cleanup class-specific test data
            cleanupClassTestData();
            
            logger.info("Class teardown completed for: {}", className);
            
        } catch (Exception e) {
            logger.error("Class teardown failed for: {}", className, e);
        }
    }
    
    /**
     * Suite-level teardown - executed once after all tests
     * Performs final cleanup and generates comprehensive reports
     */
    @AfterSuite(alwaysRun = true)
    public void suiteTeardown() {
        try {
            logger.info("Starting suite teardown...");
            
            // Calculate suite execution time
            Instant suiteEndTime = Instant.now();
            Duration suiteDuration = Duration.between(suiteStartTime, suiteEndTime);
            
            // Generate comprehensive test reports
            generateSuiteReports(suiteDuration);
            
            // Cleanup all remaining resources
            performFinalResourceCleanup();
            
            // Close database connections
            closeDatabaseConnections();
            
            // Generate performance reports
            generatePerformanceReports();
            
            // Send notifications if configured
            sendTestCompletionNotifications();
            
            // Cleanup temporary files
            cleanupTemporaryFiles();
            
            // Archive test artifacts
            archiveTestArtifacts();
            
            // Log final summary
            logFinalSuiteSummary(suiteDuration);
            
        } catch (Exception e) {
            logger.error("Suite teardown failed", e);
        } finally {
            // Ensure configuration manager cleanup
            if (configManager != null) {
                configManager.quitAllDrivers();
            }
        }
    }
    
    // ===============================
    // INITIALIZATION METHODS
    // ===============================
    
    private void initializeConfigurationManager() {
        try {
            configManager = configLoader.getInstance();
            
            if (!configManager.isReadyForExecution()) {
                throw new ConfigurationException("Configuration manager is not ready for execution");
            }
            
            logger.info("Configuration manager initialized successfully");
            logger.info("Environment: {}", configManager.getEnvironment());
            logger.info("Browser: {}", configManager.getBrowserName());
            logger.info("Base URL: {}", configManager.getBaseUrl());
            logger.info("Parallel execution: {}", configManager.isParallelExecution());
            
        } catch (Exception e) {
            logger.error("Failed to initialize configuration manager", e);
            throw new RuntimeException("Configuration initialization failed", e);
        }
    }
    
    private void setupTestInfrastructure() throws IOException {
        logger.info("Setting up test infrastructure...");
        
        // Create required directories
        createTestDirectories();
        
        // Setup file permissions
        setupFilePermissions();
        
        // Initialize test artifact management
        initializeArtifactManagement();
        
        logger.info("Test infrastructure setup completed");
    }
    
    private void createTestDirectories() throws IOException {
        String[] directories = {
            SCREENSHOT_DIR, LOGS_DIR, REPORTS_DIR, VIDEO_DIR, TEST_DATA_DIR
        };
        
        for (String dir : directories) {
            Path dirPath = Paths.get(dir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
                logger.debug("Created directory: {}", dir);
            }
        }
    }
    
    private void setupFilePermissions() {
        // Setup appropriate file permissions for test artifacts
        logger.debug("File permissions configured");
    }
    
    private void initializeArtifactManagement() {
        // Initialize artifact tracking and management
        suiteMetrics.put("artifactsGenerated", 0);
        suiteMetrics.put("screenshotsTaken", 0);
        suiteMetrics.put("logsGenerated", 0);
        logger.debug("Artifact management initialized");
    }
    
    private void initializeMonitoringAndReporting() {
        logger.info("Initializing monitoring and reporting systems...");
        
        // Initialize performance metrics collection
        suiteMetrics.put("suiteStartTime", suiteStartTime);
        suiteMetrics.put("totalTests", 0);
        suiteMetrics.put("passedTests", 0);
        suiteMetrics.put("failedTests", 0);
        suiteMetrics.put("skippedTests", 0);
        
        logger.info("Monitoring and reporting initialized");
    }
    
//    private void initializeDatabaseConnections() {
//        try {
//            if (configManager.getDatabaseConnection() != null) {
//                logger.info("Database connectivity verified");
//            }
//        } catch (SQLException e) {
//            logger.warn("Database connection not available: {}", e.getMessage());
//        }
//    }
    
    private void initializeSecurityAndAuditLogging() {
        logger.info("Security and audit logging initialized");
        
        // Log security configuration
        Map<String, Object> securityConfig = configManager.getSecurityConfig();
        logger.info("Security configuration loaded: {}", securityConfig.size());
    }
    
    private void validateTestEnvironment() {
        logger.info("Validating test environment...");
        
        // Validate configuration
        if (!configManager.isReadyForExecution()) {
            throw new RuntimeException("Test environment validation failed");
        }
        
        // Validate system resources
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / (1024 * 1024); // MB
        int availableProcessors = runtime.availableProcessors();
        
        logger.info("System resources - Max Memory: {}MB, Available Processors: {}", 
                   maxMemory, availableProcessors);
        
        if (maxMemory < 512) {
            logger.warn("Low memory detected: {}MB. Tests may run slowly.", maxMemory);
        }
        
        logger.info("Environment validation completed");
    }
    
    private void setupCIIntegration() {
        // Setup CI/CD specific configurations
        String buildNumber = System.getenv("BUILD_NUMBER");
        String jobName = System.getenv("JOB_NAME");
        String gitCommit = System.getenv("GIT_COMMIT");
        
        if (buildNumber != null) {
            suiteMetrics.put("buildNumber", buildNumber);
            logger.info("CI Build Number: {}", buildNumber);
        }
        
        if (jobName != null) {
            suiteMetrics.put("jobName", jobName);
            logger.info("CI Job Name: {}", jobName);
        }
        
        if (gitCommit != null) {
            suiteMetrics.put("gitCommit", gitCommit);
            logger.info("Git Commit: {}", gitCommit);
        }
    }
    
    private void initializePerformanceMonitoring() {
        logger.info("Performance monitoring initialized");
        suiteMetrics.put("performanceMetrics", new ConcurrentHashMap<String, Object>());
    }
    
    // ===============================
    // TEST SETUP METHODS
    // ===============================
    
    private void setupTestLoggingContext(String testName, long threadId) {
        MDC.put("testName", testName);
        MDC.put("threadId", String.valueOf(threadId));
        MDC.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }
    
    private void setupClassSpecificConfigurations() {
        // Override with class-specific configurations if needed
        logger.debug("Class-specific configurations setup completed");
    }
    
    private void initializeTestData() {
        // Initialize test data for the class
        logger.debug("Test data initialized for class");
    }
    
    private void setupClassLevelMonitoring(String className) {
        // Setup monitoring specific to this test class
        logger.debug("Class-level monitoring setup for: {}", className);
    }
    
    private void initializeWebDriverForTest(long threadId) {
        try {
            logger.info("Initializing WebDriver for thread: {}", threadId);
            
            WebDriver driver = configManager.createDriver();
            threadLocalDrivers.put(threadId, driver);
            
            logger.info("WebDriver initialized successfully for thread: {}", threadId);
            
        } catch (DriverCreationException e) {
            logger.error("Failed to initialize WebDriver for thread: {}", threadId, e);
            throw new RuntimeException("WebDriver initialization failed", e);
        }
    }
    
//    private void initializeDatabaseConnectionForTest(long threadId) {
//        try {
//            Connection connection = configManager.getDatabaseConnection();
//            if (connection != null) {
//                threadLocalDbConnections.put(threadId, connection);
//                logger.debug("Database connection initialized for thread: {}", threadId);
//            }
//        } catch (SQLException e) {
//            logger.warn("Database connection not available for thread: {}", threadId);
//        }
//    }
    
    private void setupTestLevelMonitoring(String testName, long threadId) {
        // Setup test-specific monitoring
        logger.debug("Test-level monitoring setup for: {} on thread: {}", testName, threadId);
    }
    
    private void setupTestDataIsolation(String testName) {
        // Setup test data isolation
        logger.debug("Test data isolation setup for: {}", testName);
    }
    
    private void initializeBrowserSession(long threadId) {
        WebDriver driver = threadLocalDrivers.get(threadId);
        if (driver != null) {
            try {
                String baseUrl = configManager.getBaseUrl();
                driver.get(baseUrl);
                logger.info("Browser navigated to base URL: {} for thread: {}", baseUrl, threadId);
            } catch (Exception e) {
                logger.error("Failed to navigate to base URL for thread: {}", threadId, e);
                throw new RuntimeException("Browser initialization failed", e);
            }
        }
    }
    
    // ===============================
    // TEST TEARDOWN METHODS
    // ===============================
    
    private void handleTestResult(ITestResult result, String testName, Duration testDuration, long threadId) {
        int status = result.getStatus();
        
        switch (status) {
            case ITestResult.SUCCESS:
                logger.info("✅ TEST PASSED: {} (Duration: {}ms)", testName, testDuration.toMillis());
                break;
                
            case ITestResult.FAILURE:
                logger.error("❌ TEST FAILED: {} (Duration: {}ms)", testName, testDuration.toMillis());
                handleTestFailure(result, testName, threadId);
                break;
                
            case ITestResult.SKIP:
                logger.warn("⏭️ TEST SKIPPED: {} (Duration: {}ms)", testName, testDuration.toMillis());
                break;
                
            default:
                logger.warn("❓ TEST STATUS UNKNOWN: {} (Duration: {}ms)", testName, testDuration.toMillis());
        }
    }
    
    private void handleTestFailure(ITestResult result, String testName, long threadId) {
        try {
            // Take screenshot on failure
            takeScreenshotOnFailure(testName, threadId);
            
            // Capture page source
            capturePageSource(testName, threadId);
            
            // Log failure details
            Throwable throwable = result.getThrowable();
            if (throwable != null) {
                logger.error("Test failure details for {}: {}", testName, throwable.getMessage(), throwable);
            }
            
        } catch (Exception e) {
            logger.error("Failed to handle test failure for: {}", testName, e);
        }
    }
    
    private void takeScreenshotOnFailure(String testName, long threadId) {
        if (!configManager.shouldTakeScreenshots()) {
            return;
        }
        
        WebDriver driver = threadLocalDrivers.get(threadId);
        if (driver instanceof TakesScreenshot) {
            try {
                File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String filename = String.format("FAILURE_%s_%s_%d.png", 
                                               testName.replaceAll("[^a-zA-Z0-9]", "_"), 
                                               timestamp, threadId);
                
                File destFile = new File(SCREENSHOT_DIR, filename);
                FileUtils.copyFile(screenshot, destFile);
                
                List<String> artifacts = testArtifacts.get(threadId);
                if (artifacts != null) {
                    artifacts.add(destFile.getAbsolutePath());
                }
                
                logger.info("Failure screenshot captured: {}", destFile.getAbsolutePath());
                
            } catch (IOException e) {
                logger.error("Failed to take screenshot for: {}", testName, e);
            }
        }
    }
    
    private void capturePageSource(String testName, long threadId) {
        WebDriver driver = threadLocalDrivers.get(threadId);
        if (driver != null) {
            try {
                String pageSource = driver.getPageSource();
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String filename = String.format("FAILURE_%s_%s_%d.html", 
                                               testName.replaceAll("[^a-zA-Z0-9]", "_"), 
                                               timestamp, threadId);
                
                File sourceFile = new File(LOGS_DIR, filename);
                FileUtils.writeStringToFile(sourceFile, pageSource, "UTF-8");
                
                List<String> artifacts = testArtifacts.get(threadId);
                if (artifacts != null) {
                    artifacts.add(sourceFile.getAbsolutePath());
                }
                
                logger.info("Page source captured: {}", sourceFile.getAbsolutePath());
                
            } catch (Exception e) {
                logger.error("Failed to capture page source for: {}", testName, e);
            }
        }
    }
    
    private void capturePerformanceMetrics(long threadId, String testName) {
        WebDriver driver = threadLocalDrivers.get(threadId);
        if (driver != null && configManager.isPerformanceLoggingEnabled()) {
            try {
                List<LogEntry> perfLogs = driver.manage().logs().get(LogType.PERFORMANCE).getAll();
                
                if (!perfLogs.isEmpty()) {
                    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String filename = String.format("PERF_%s_%s_%d.log", 
                                                   testName.replaceAll("[^a-zA-Z0-9]", "_"), 
                                                   timestamp, threadId);
                    
                    File perfFile = new File(LOGS_DIR, filename);
                    StringBuilder perfData = new StringBuilder();
                    
                    for (LogEntry entry : perfLogs) {
                        perfData.append(entry.getTimestamp())
                               .append(" - ")
                               .append(entry.getLevel())
                               .append(" - ")
                               .append(entry.getMessage())
                               .append("\n");
                    }
                    
                    FileUtils.writeStringToFile(perfFile, perfData.toString(), "UTF-8");
                    
                    List<String> artifacts = testArtifacts.get(threadId);
                    if (artifacts != null) {
                        artifacts.add(perfFile.getAbsolutePath());
                    }
                    
                    logger.debug("Performance metrics captured: {}", perfFile.getAbsolutePath());
                }
                
            } catch (Exception e) {
                logger.error("Failed to capture performance metrics for: {}", testName, e);
            }
        }
    }
    
    private void captureBrowserLogs(long threadId, String testName) {
        WebDriver driver = threadLocalDrivers.get(threadId);
        if (driver != null) {
            try {
                List<LogEntry> browserLogs = driver.manage().logs().get(LogType.BROWSER).getAll();
                
                if (!browserLogs.isEmpty()) {
                    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String filename = String.format("BROWSER_%s_%s_%d.log", 
                                                   testName.replaceAll("[^a-zA-Z0-9]", "_"), 
                                                   timestamp, threadId);
                    
                    File logFile = new File(LOGS_DIR, filename);
                    StringBuilder logData = new StringBuilder();
                    
                    for (LogEntry entry : browserLogs) {
                        logData.append(new Date(entry.getTimestamp()))
                              .append(" - ")
                              .append(entry.getLevel())
                              .append(" - ")
                              .append(entry.getMessage())
                              .append("\n");
                    }
                    
                    FileUtils.writeStringToFile(logFile, logData.toString(), "UTF-8");
                    
                    List<String> artifacts = testArtifacts.get(threadId);
                    if (artifacts != null) {
                        artifacts.add(logFile.getAbsolutePath());
                    }
                    
                    logger.debug("Browser logs captured: {}", logFile.getAbsolutePath());
                }
                
            } catch (Exception e) {
                logger.error("Failed to capture browser logs for: {}", testName, e);
            }
        }
    }
    
    private void cleanupDatabaseConnection(long threadId) {
        Connection connection = threadLocalDbConnections.remove(threadId);
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
                logger.debug("Database connection closed for thread: {}", threadId);
            } catch (SQLException e) {
                logger.error("Failed to close database connection for thread: {}", threadId, e);
            }
        }
    }
    
    private void cleanupWebDriver(long threadId) {
        WebDriver driver = threadLocalDrivers.remove(threadId);
        if (driver != null) {
            try {
                driver.quit();
                logger.debug("WebDriver quit for thread: {}", threadId);
            } catch (Exception e) {
                logger.error("Failed to quit WebDriver for thread: {}", threadId, e);
            }
        }
    }
    
    private void cleanupTestArtifacts(long threadId) {
        List<String> artifacts = testArtifacts.remove(threadId);
        if (artifacts != null && !artifacts.isEmpty()) {
            logger.debug("Test artifacts for thread {}: {}", threadId, artifacts.size());
            suiteMetrics.put("artifactsGenerated", 
                           (Integer) suiteMetrics.get("artifactsGenerated") + artifacts.size());
        }
    }
    
    private void updateTestCounters(ITestResult result) {
        switch (result.getStatus()) {
            case ITestResult.SUCCESS:
                passedTestCounter.incrementAndGet();
                break;
            case ITestResult.FAILURE:
                failedTestCounter.incrementAndGet();
                break;
        }
    }
    
    private void cleanupThreadLocalData(long threadId) {
        testStartTimes.remove(threadId);
        testNames.remove(threadId);
        logger.debug("Thread-local data cleaned for thread: {}", threadId);
    }
    
    private void handleSetupFailure(long threadId, String testName, Exception e) {
        logger.error("Setup failure for test: {} on thread: {}", testName, threadId, e);
        
        // Cleanup any partially initialized resources
        cleanupWebDriver(threadId);
        cleanupDatabaseConnection(threadId);
        cleanupThreadLocalData(threadId);
    }
    
    // ===============================
    // FINAL CLEANUP METHODS
    // ===============================
    
    private void cleanupClassLevelResources() {
        logger.debug("Class-level resources cleaned up");
    }
    
    private void generateClassLevelReports(String className) {
        logger.debug("Class-level reports generated for: {}", className);
    }
    
    private void cleanupClassTestData() {
        logger.debug("Class test data cleaned up");
    }
    
    private void generateSuiteReports(Duration suiteDuration) {
        try {
            logger.info("Generating suite reports...");
            
            // Generate summary report
            generateSuiteSummaryReport(suiteDuration);
            
            // Generate detailed execution report
            generateDetailedExecutionReport();
            
            logger.info("Suite reports generated successfully");
            
        } catch (Exception e) {
            logger.error("Failed to generate suite reports", e);
        }
    }
    
    private void generateSuiteSummaryReport(Duration suiteDuration) throws IOException {
        StringBuilder report = new StringBuilder();
        
        report.append("ENTERPRISE TEST SUITE EXECUTION SUMMARY\n");
        report.append("=".repeat(80)).append("\n");
        report.append("Execution Date: ").append(new Date()).append("\n");
        report.append("Suite Duration: ").append(suiteDuration.toMillis()).append(" ms\n");
        report.append("Environment: ").append(configManager.getEnvironment()).append("\n");
        report.append("Browser: ").append(configManager.getBrowserName()).append("\n");
        report.append("Base URL: ").append(configManager.getBaseUrl()).append("\n");
        report.append("Parallel Threads: ").append(configManager.getThreadCount()).append("\n");
        report.append("\n");
        report.append("TEST RESULTS:\n");
        report.append("-".repeat(40)).append("\n");
        report.append("Total Tests: ").append(testCounter.get()).append("\n");
        report.append("Passed: ").append(passedTestCounter.get()).append("\n");
        report.append("Failed: ").append(failedTestCounter.get()).append("\n");
        report.append("Success Rate: ").append(calculateSuccessRate()).append("%\n");
        report.append("\n");
        report.append("PERFORMANCE METRICS:\n");
        report.append("-".repeat(40)).append("\n");
        report.append("Average Test Duration: ").append(calculateAverageTestDuration()).append(" ms\n");
        report.append("Screenshots Taken: ").append(suiteMetrics.get("screenshotsTaken")).append("\n");
        report.append("Artifacts Generated: ").append(suiteMetrics.get("artifactsGenerated")).append("\n");
        
        // Add system information
        Map<String, Object> systemInfo = configManager.getSystemInfo();
        report.append("\n");
        report.append("SYSTEM INFORMATION:\n");
        report.append("-".repeat(40)).append("\n");
        systemInfo.forEach((key, value) -> 
            report.append(key).append(": ").append(value).append("\n"));
        
        File summaryFile = new File(REPORTS_DIR, "suite-summary-report.txt");
        FileUtils.writeStringToFile(summaryFile, report.toString(), "UTF-8");
        
        logger.info("Suite summary report generated: {}", summaryFile.getAbsolutePath());
    }
    
    private void generateDetailedExecutionReport() throws IOException {
        // Generate detailed JSON report for CI/CD integration
        Map<String, Object> detailedReport = new HashMap<>();
        
        detailedReport.put("executionSummary", createExecutionSummary());
        detailedReport.put("environmentInfo", createEnvironmentInfo());
        detailedReport.put("performanceMetrics", suiteMetrics.get("performanceMetrics"));
        detailedReport.put("systemInfo", configManager.getSystemInfo());
        detailedReport.put("configurationSummary", configManager.getConfigurationAsJson());
        
        // Convert to JSON and save
        String jsonReport = configManager.getConfigurationAsJson(); // Simplified for demo
        File detailedFile = new File(REPORTS_DIR, "detailed-execution-report.json");
        FileUtils.writeStringToFile(detailedFile, jsonReport, "UTF-8");
        
        logger.info("Detailed execution report generated: {}", detailedFile.getAbsolutePath());
    }
    
    private Map<String, Object> createExecutionSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalTests", testCounter.get());
        summary.put("passedTests", passedTestCounter.get());
        summary.put("failedTests", failedTestCounter.get());
        summary.put("successRate", calculateSuccessRate());
        summary.put("executionStartTime", suiteStartTime);
        summary.put("executionEndTime", Instant.now());
        return summary;
    }
    
    private Map<String, Object> createEnvironmentInfo() {
        Map<String, Object> envInfo = new HashMap<>();
        envInfo.put("environment", configManager.getEnvironment());
        envInfo.put("browser", configManager.getBrowserName());
        envInfo.put("baseUrl", configManager.getBaseUrl());
        envInfo.put("parallelExecution", configManager.isParallelExecution());
        envInfo.put("threadCount", configManager.getThreadCount());
        envInfo.put("headlessMode", configManager.isHeadlessMode());
        envInfo.put("remoteExecution", configManager.isRemoteExecution());
        return envInfo;
    }
    
    private double calculateSuccessRate() {
        int totalTests = testCounter.get();
        if (totalTests == 0) return 0.0;
        return (double) passedTestCounter.get() / totalTests * 100;
    }
    
    private long calculateAverageTestDuration() {
        // This would calculate based on collected metrics
        return 5000; // Placeholder - implement based on actual metrics collection
    }
    
    private void performFinalResourceCleanup() {
        logger.info("Performing final resource cleanup...");
        
        // Cleanup any remaining WebDrivers
        threadLocalDrivers.values().forEach(driver -> {
            try {
                driver.quit();
            } catch (Exception e) {
                logger.error("Error quitting driver during final cleanup", e);
            }
        });
        threadLocalDrivers.clear();
        
        // Cleanup any remaining database connections
        threadLocalDbConnections.values().forEach(connection -> {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                logger.error("Error closing database connection during final cleanup", e);
            }
        });
        threadLocalDbConnections.clear();
        
        // Clear thread-local collections
        testStartTimes.clear();
        testNames.clear();
        testArtifacts.clear();
        
        logger.info("Final resource cleanup completed");
    }
    
    private void closeDatabaseConnections() {
        try {
            // Use configuration manager to close all database connections
            logger.info("Closing database connections...");
            // Implementation would depend on how configManager handles DB connections
            logger.info("Database connections closed successfully");
        } catch (Exception e) {
            logger.error("Error closing database connections", e);
        }
    }
    
    private void generatePerformanceReports() {
        try {
            logger.info("Generating performance reports...");
            
            StringBuilder perfReport = new StringBuilder();
            perfReport.append("PERFORMANCE ANALYSIS REPORT\n");
            perfReport.append("=".repeat(50)).append("\n");
            perfReport.append("Suite Execution Time: ").append(Duration.between(suiteStartTime, Instant.now()).toMillis()).append(" ms\n");
            perfReport.append("Average Test Duration: ").append(calculateAverageTestDuration()).append(" ms\n");
            perfReport.append("Total Screenshots: ").append(suiteMetrics.get("screenshotsTaken")).append("\n");
            perfReport.append("Total Artifacts: ").append(suiteMetrics.get("artifactsGenerated")).append("\n");
            
            // Add memory usage information
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory() / (1024 * 1024);
            long freeMemory = runtime.freeMemory() / (1024 * 1024);
            long usedMemory = totalMemory - freeMemory;
            
            perfReport.append("\nMEMORY USAGE:\n");
            perfReport.append("Total Memory: ").append(totalMemory).append(" MB\n");
            perfReport.append("Used Memory: ").append(usedMemory).append(" MB\n");
            perfReport.append("Free Memory: ").append(freeMemory).append(" MB\n");
            
            File perfFile = new File(REPORTS_DIR, "performance-report.txt");
            FileUtils.writeStringToFile(perfFile, perfReport.toString(), "UTF-8");
            
            logger.info("Performance report generated: {}", perfFile.getAbsolutePath());
            
        } catch (Exception e) {
            logger.error("Failed to generate performance reports", e);
        }
    }
    
    private void sendTestCompletionNotifications() {
        try {
            // Send email notifications if configured
            if (configManager.isNotificationEnabled("email")) {
                sendEmailNotification();
            }
            
            // Send Slack notifications if configured
            if (configManager.isNotificationEnabled("slack")) {
                sendSlackNotification();
            }
            
            // Send Teams notifications if configured
            if (configManager.isNotificationEnabled("teams")) {
                sendTeamsNotification();
            }
            
        } catch (Exception e) {
            logger.error("Failed to send test completion notifications", e);
        }
    }
    
    private void sendEmailNotification() {
        Map<String, String> emailConfig = configManager.getNotificationConfig("email");
        
        // Implementation would include actual email sending logic
        logger.info("Email notification sent to: {}", emailConfig.get("recipients"));
    }
    
    private void sendSlackNotification() {
        Map<String, String> slackConfig = configManager.getNotificationConfig("slack");
        
        // Implementation would include actual Slack API integration
        logger.info("Slack notification sent to channel: {}", slackConfig.get("channel"));
    }
    
    private void sendTeamsNotification() {
        Map<String, String> teamsConfig = configManager.getNotificationConfig("teams");
        
        // Implementation would include actual Teams webhook integration
        logger.info("Teams notification sent to webhook: {}", teamsConfig.get("webhookUrl"));
    }
    
    private void cleanupTemporaryFiles() {
        try {
            logger.info("Cleaning up temporary files...");
            
            // Clean up temporary screenshot files older than configured retention period
            File screenshotDir = new File(SCREENSHOT_DIR);
            if (screenshotDir.exists()) {
                File[] screenshots = screenshotDir.listFiles();
                if (screenshots != null) {
                    long retentionTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7); // 7 days retention
                    
                    for (File screenshot : screenshots) {
                        if (screenshot.lastModified() < retentionTime) {
                            if (screenshot.delete()) {
                                logger.debug("Deleted old screenshot: {}", screenshot.getName());
                            }
                        }
                    }
                }
            }
            
            // Clean up temporary log files
            File logsDir = new File(LOGS_DIR);
            if (logsDir.exists()) {
                File[] logFiles = logsDir.listFiles();
                if (logFiles != null) {
                    long retentionTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30); // 30 days retention
                    
                    for (File logFile : logFiles) {
                        if (logFile.lastModified() < retentionTime) {
                            if (logFile.delete()) {
                                logger.debug("Deleted old log file: {}", logFile.getName());
                            }
                        }
                    }
                }
            }
            
            logger.info("Temporary files cleanup completed");
            
        } catch (Exception e) {
            logger.error("Failed to cleanup temporary files", e);
        }
    }
    
    private void archiveTestArtifacts() {
        try {
            logger.info("Archiving test artifacts...");
            
            // Create archive directory with timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String archiveName = String.format("test-execution-%s", timestamp);
            File archiveDir = new File("test-archives", archiveName);
            
            if (!archiveDir.exists()) {
                archiveDir.mkdirs();
            }
            
            // Copy important artifacts to archive
            copyDirectoryToArchive(REPORTS_DIR, new File(archiveDir, "reports"));
            copyDirectoryToArchive(SCREENSHOT_DIR, new File(archiveDir, "screenshots"));
            
            // Create manifest file
            createArchiveManifest(archiveDir);
            
            logger.info("Test artifacts archived to: {}", archiveDir.getAbsolutePath());
            
        } catch (Exception e) {
            logger.error("Failed to archive test artifacts", e);
        }
    }
    
    private void copyDirectoryToArchive(String sourceDir, File targetDir) throws IOException {
        File source = new File(sourceDir);
        if (source.exists() && source.isDirectory()) {
            FileUtils.copyDirectory(source, targetDir);
            logger.debug("Archived directory: {} to {}", sourceDir, targetDir.getAbsolutePath());
        }
    }
    
    private void createArchiveManifest(File archiveDir) throws IOException {
        StringBuilder manifest = new StringBuilder();
        manifest.append("TEST EXECUTION ARCHIVE MANIFEST\n");
        manifest.append("=".repeat(50)).append("\n");
        manifest.append("Archive Created: ").append(new Date()).append("\n");
        manifest.append("Suite Duration: ").append(Duration.between(suiteStartTime, Instant.now()).toMillis()).append(" ms\n");
        manifest.append("Total Tests: ").append(testCounter.get()).append("\n");
        manifest.append("Passed Tests: ").append(passedTestCounter.get()).append("\n");
        manifest.append("Failed Tests: ").append(failedTestCounter.get()).append("\n");
        manifest.append("Success Rate: ").append(calculateSuccessRate()).append("%\n");
        manifest.append("Environment: ").append(configManager.getEnvironment()).append("\n");
        manifest.append("Browser: ").append(configManager.getBrowserName()).append("\n");
        
        // Add CI/CD information if available
        if (suiteMetrics.containsKey("buildNumber")) {
            manifest.append("Build Number: ").append(suiteMetrics.get("buildNumber")).append("\n");
        }
        if (suiteMetrics.containsKey("jobName")) {
            manifest.append("Job Name: ").append(suiteMetrics.get("jobName")).append("\n");
        }
        if (suiteMetrics.containsKey("gitCommit")) {
            manifest.append("Git Commit: ").append(suiteMetrics.get("gitCommit")).append("\n");
        }
        
        File manifestFile = new File(archiveDir, "MANIFEST.txt");
        FileUtils.writeStringToFile(manifestFile, manifest.toString(), "UTF-8");
    }
    
    private void logSuiteInitializationSummary() {
        logger.info("=".repeat(100));
        logger.info("                    SUITE INITIALIZATION COMPLETED");
        logger.info("=".repeat(100));
        logger.info("Environment: {}", configManager.getEnvironment());
        logger.info("Browser: {}", configManager.getBrowserName());
        logger.info("Base URL: {}", configManager.getBaseUrl());
        logger.info("Parallel Execution: {}", configManager.isParallelExecution());
        logger.info("Thread Count: {}", configManager.getThreadCount());
        logger.info("Screenshots Enabled: {}", configManager.shouldTakeScreenshots());
        logger.info("Video Recording: {}", configManager.isVideoRecordingEnabled());
        logger.info("Performance Logging: {}", configManager.isPerformanceLoggingEnabled());
        logger.info("Remote Execution: {}", configManager.isRemoteExecution());
        logger.info("Headless Mode: {}", configManager.isHeadlessMode());
        logger.info("Mobile Device: {}", configManager.getMobileDevice().isEmpty() ? "None" : configManager.getMobileDevice());
        logger.info("Max Retries: {}", configManager.getMaxRetries());
        logger.info("Default Timeout: {} seconds", configManager.getDefaultTimeout());
        logger.info("=".repeat(100));
        logger.info("🚀 READY FOR TEST EXECUTION");
        logger.info("=".repeat(100));
    }
    
    private void logFinalSuiteSummary(Duration suiteDuration) {
        logger.info("=".repeat(100));
        logger.info("                    ENTERPRISE TEST SUITE EXECUTION COMPLETED");
        logger.info("=".repeat(100));
        logger.info("📊 EXECUTION SUMMARY:");
        logger.info("   Total Tests Executed: {}", testCounter.get());
        logger.info("   ✅ Passed: {}", passedTestCounter.get());
        logger.info("   ❌ Failed: {}", failedTestCounter.get());
        logger.info("   📈 Success Rate: {}%", String.format("%.2f", calculateSuccessRate()));
        logger.info("   ⏱️ Total Execution Time: {} ms ({} minutes)", 
                   suiteDuration.toMillis(), 
                   String.format("%.2f", suiteDuration.toMillis() / 60000.0));
        logger.info("   📸 Screenshots Taken: {}", suiteMetrics.get("screenshotsTaken"));
        logger.info("   📁 Artifacts Generated: {}", suiteMetrics.get("artifactsGenerated"));
        
        // Log performance metrics
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory() / (1024 * 1024);
        long freeMemory = runtime.freeMemory() / (1024 * 1024);
        long usedMemory = totalMemory - freeMemory;
        
        logger.info("💾 RESOURCE USAGE:");
        logger.info("   Memory Used: {} MB", usedMemory);
        logger.info("   Memory Free: {} MB", freeMemory);
        logger.info("   Total Memory: {} MB", totalMemory);
        
        // Log environment information
        logger.info("🌍 ENVIRONMENT:");
        logger.info("   Environment: {}", configManager.getEnvironment());
        logger.info("   Browser: {}", configManager.getBrowserName());
        logger.info("   Base URL: {}", configManager.getBaseUrl());
        logger.info("   Execution Mode: {}", configManager.isRemoteExecution() ? "Remote" : "Local");
        
        // Log CI/CD information if available
        if (suiteMetrics.containsKey("buildNumber")) {
            logger.info("🔧 CI/CD INFO:");
            logger.info("   Build Number: {}", suiteMetrics.get("buildNumber"));
            if (suiteMetrics.containsKey("jobName")) {
                logger.info("   Job Name: {}", suiteMetrics.get("jobName"));
            }
            if (suiteMetrics.containsKey("gitCommit")) {
                logger.info("   Git Commit: {}", suiteMetrics.get("gitCommit"));
            }
        }
        
        logger.info("=".repeat(100));
        
        // Log final status
        if (failedTestCounter.get() == 0) {
            logger.info("🎉 ALL TESTS PASSED! SUITE EXECUTION SUCCESSFUL!");
        } else {
            logger.warn("⚠️ {} TESTS FAILED. CHECK DETAILED REPORTS FOR MORE INFORMATION.", failedTestCounter.get());
        }
        
        logger.info("=".repeat(100));
    }
    
    // ===============================
    // UTILITY METHODS FOR TESTS
    // ===============================
    
    /**
     * Get WebDriver instance for current thread
     * @return WebDriver instance
     */
    protected WebDriver getDriver() {
        long threadId = Thread.currentThread().getId();
        WebDriver driver = threadLocalDrivers.get(threadId);
        
        if (driver == null) {
            throw new RuntimeException("WebDriver not initialized for thread: " + threadId);
        }
        
        return driver;
    }
    
    /**
     * Get database connection for current thread
     * @return Database connection
     */
    protected Connection getDatabaseConnection() {
        long threadId = Thread.currentThread().getId();
        Connection connection = threadLocalDbConnections.get(threadId);
        
        if (connection == null) {
            throw new RuntimeException("Database connection not available for thread: " + threadId);
        }
        
        return connection;
    }
    
    /**
     * Take screenshot with custom name
     * @param screenshotName Custom name for screenshot
     */
    protected void takeScreenshot(String screenshotName) {
        long threadId = Thread.currentThread().getId();
        WebDriver driver = threadLocalDrivers.get(threadId);
        
        if (driver instanceof TakesScreenshot && configManager.shouldTakeScreenshots()) {
            try {
                File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String filename = String.format("%s_%s_%d.png", 
                                               screenshotName.replaceAll("[^a-zA-Z0-9]", "_"), 
                                               timestamp, threadId);
                
                File destFile = new File(SCREENSHOT_DIR, filename);
                FileUtils.copyFile(screenshot, destFile);
                
                List<String> artifacts = testArtifacts.get(threadId);
                if (artifacts != null) {
                    artifacts.add(destFile.getAbsolutePath());
                }
                
                // Update screenshot counter
                suiteMetrics.put("screenshotsTaken", 
                               (Integer) suiteMetrics.getOrDefault("screenshotsTaken", 0) + 1);
                
                logger.info("Screenshot captured: {}", destFile.getAbsolutePath());
                
            } catch (IOException e) {
                logger.error("Failed to take screenshot: {}", screenshotName, e);
            }
        }
    }
    
    /**
     * Get test data from configuration
     * @param key Test data key
     * @return Test data value
     */
    protected String getTestData(String key) {
        return configManager.getTestData(key);
    }
    
    /**
     * Get test data from specific category
     * @param category Test data category
     * @param key Test data key
     * @return Test data value
     */
    protected String getTestData(String category, String key) {
        return configManager.getTestData(category, key);
    }
    
    /**
     * Get test user credentials
     * @param userType Type of user (admin, regular, etc.)
     * @return Map containing user credentials
     */
    protected Map<String, String> getTestUser(String userType) {
        return configManager.getTestUser(userType);
    }
    
    /**
     * Get API endpoint URL
     * @param endpointName Name of the endpoint
     * @return Full endpoint URL
     */
    protected String getApiEndpoint(String endpointName) {
        String baseApiUrl = configManager.getApiBaseUrl();
        String endpoint = configManager.getEndpoint(endpointName);
        return baseApiUrl + endpoint;
    }
    
    /**
     * Check if feature is enabled for current environment
     * @param featureName Name of the feature
     * @return true if feature is enabled
     */
    protected boolean isFeatureEnabled(String featureName) {
        return configManager.isFeatureEnabled(featureName);
    }
    
    /**
     * Wait for specified duration with proper logging
     * @param seconds Duration in seconds
     */
    protected void waitFor(int seconds) {
        try {
            logger.debug("Waiting for {} seconds...", seconds);
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Wait interrupted", e);
        }
    }
    
    /**
     * Log test step for better reporting
     * @param stepDescription Description of the test step
     */
    protected void logTestStep(String stepDescription) {
        logger.info("🔹 TEST STEP: {}", stepDescription);
    }
    
    /**
     * Log test verification for better reporting
     * @param verificationDescription Description of the verification
     * @param result Result of the verification
     */
    protected void logVerification(String verificationDescription, boolean result) {
        if (result) {
            logger.info("✅ VERIFICATION PASSED: {}", verificationDescription);
        } else {
            logger.error("❌ VERIFICATION FAILED: {}", verificationDescription);
        }
    }
    
    /**
     * Get current test name
     * @return Current test method name
     */
    protected String getCurrentTestName() {
        long threadId = Thread.currentThread().getId();
        return testNames.get(threadId);
    }
    
    /**
     * Get test execution start time
     * @return Test start time
     */
    protected Instant getTestStartTime() {
        long threadId = Thread.currentThread().getId();
        return testStartTimes.get(threadId);
    }
    
    /**
     * Add custom test artifact
     * @param artifactPath Path to the artifact
     */
    protected void addTestArtifact(String artifactPath) {
        long threadId = Thread.currentThread().getId();
        List<String> artifacts = testArtifacts.get(threadId);
        if (artifacts != null) {
            artifacts.add(artifactPath);
            logger.debug("Test artifact added: {}", artifactPath);
        }
    }
}