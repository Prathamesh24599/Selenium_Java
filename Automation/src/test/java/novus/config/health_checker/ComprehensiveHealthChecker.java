// ===============================
// SYSTEM HEALTH CHECKER IMPLEMENTATION
// ===============================

package novus.config.health_checker;

import novus.config.config_interfaces.HealthCheck;
import novus.config.config_interfaces.SystemHealthChecker;
import novus.config.models.HealthCheckResult;
import novus.config.models.HealthStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Comprehensive system health checker with configurable health checks
 */
public class ComprehensiveHealthChecker implements SystemHealthChecker {
	private static final Logger logger = LoggerFactory.getLogger(ComprehensiveHealthChecker.class);

	// Health check constants
	private static final long HEALTH_CHECK_TIMEOUT_MS = 10000; // 10 seconds
	private static final double MEMORY_WARNING_THRESHOLD = 80.0; // 80%
	private static final double MEMORY_CRITICAL_THRESHOLD = 90.0; // 90%
	private static final long DISK_WARNING_THRESHOLD_GB = 1; // 1 GB
	private static final long DISK_CRITICAL_THRESHOLD_GB = 0; // 500 MB
	private static final int NETWORK_TIMEOUT_MS = 5000; // 5 seconds

	// Registered health checks
	private final Map<String, HealthCheck> customHealthChecks = new ConcurrentHashMap<>();

	// Configuration
	private final boolean enableNetworkChecks;
	private final boolean enableDiskChecks;
	private final boolean enableMemoryChecks;
	private final List<String> networkHosts;
	private final List<String> criticalDirectories;

	public ComprehensiveHealthChecker() {
		this(true, true, true, getDefaultNetworkHosts(), getDefaultCriticalDirectories());
	}

	public ComprehensiveHealthChecker(boolean enableNetworkChecks, boolean enableDiskChecks, boolean enableMemoryChecks,
			List<String> networkHosts, List<String> criticalDirectories) {
		this.enableNetworkChecks = enableNetworkChecks;
		this.enableDiskChecks = enableDiskChecks;
		this.enableMemoryChecks = enableMemoryChecks;
		this.networkHosts = new ArrayList<>(networkHosts);
		this.criticalDirectories = new ArrayList<>(criticalDirectories);

		logger.info("Health checker initialized - Network: {}, Disk: {}, Memory: {}", enableNetworkChecks,
				enableDiskChecks, enableMemoryChecks);
	}

	@Override
	public HealthStatus performHealthCheck() {
		long startTime = System.currentTimeMillis();
		List<HealthCheckResult> results = new ArrayList<>();

		logger.debug("Starting comprehensive health check...");

		// Run core health checks in parallel
		List<CompletableFuture<HealthCheckResult>> futures = new ArrayList<>();

		if (enableMemoryChecks) {
			futures.add(CompletableFuture.supplyAsync(this::checkMemoryUsage));
		}

		if (enableDiskChecks) {
			futures.add(CompletableFuture.supplyAsync(this::checkDiskSpace));
		}

		if (enableNetworkChecks) {
			futures.add(CompletableFuture.supplyAsync(this::checkNetworkConnectivity));
		}

		// Add system-specific checks
		futures.add(CompletableFuture.supplyAsync(this::checkJvmHealth));
		futures.add(CompletableFuture.supplyAsync(this::checkFileSystemPermissions));

		// Run custom health checks
		customHealthChecks.forEach((name, healthCheck) -> {
			futures.add(CompletableFuture.supplyAsync(() -> {
				try {
					return healthCheck.check();
				} catch (Exception e) {
					logger.error("Custom health check '{}' failed", name, e);
					return HealthCheckResult.unhealthy(name, "Check execution failed: " + e.getMessage());
				}
			}));
		});

		// Wait for all checks to complete with timeout
		try {
			CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

			allFutures.get(HEALTH_CHECK_TIMEOUT_MS, TimeUnit.MILLISECONDS);

			// Collect results
			for (CompletableFuture<HealthCheckResult> future : futures) {
				try {
					results.add(future.get());
				} catch (Exception e) {
					logger.error("Health check execution failed", e);
					results.add(HealthCheckResult.unhealthy("unknown", "Health check failed: " + e.getMessage()));
				}
			}

		} catch (Exception e) {
			logger.error("Health check timeout or execution error", e);
			results.add(HealthCheckResult.unhealthy("timeout", "Health check timed out"));
		}

		long totalTime = System.currentTimeMillis() - startTime;
		boolean overallHealthy = results.stream().allMatch(HealthCheckResult::isHealthy);

		HealthStatus status = new HealthStatus(overallHealthy, results, totalTime);

		logger.info("Health check completed in {}ms - Status: {}", totalTime, overallHealthy ? "HEALTHY" : "UNHEALTHY");

		if (!overallHealthy) {
			logger.warn("Health issues detected: {}", status.getIssues());
		}

		return status;
	}

	@Override
	public boolean isSystemHealthy() {
		return performHealthCheck().isHealthy();
	}

	@Override
	public List<String> getHealthIssues() {
		return performHealthCheck().getIssues();
	}

	@Override
	public void registerHealthCheck(String name, HealthCheck check) {
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("Health check name cannot be null or empty");
		}
		if (check == null) {
			throw new IllegalArgumentException("Health check cannot be null");
		}

		customHealthChecks.put(name, check);
		logger.info("Registered custom health check: {}", name);
	}

	/**
	 * Check memory usage and availability
	 */
	private HealthCheckResult checkMemoryUsage() {
		long startTime = System.currentTimeMillis();
		Map<String, Object> details = new HashMap<>();

		try {
			Runtime runtime = Runtime.getRuntime();
			long maxMemory = runtime.maxMemory();
			long totalMemory = runtime.totalMemory();
			long freeMemory = runtime.freeMemory();
			long usedMemory = totalMemory - freeMemory;

			double usagePercentage = (double) usedMemory / maxMemory * 100;
			long maxMemoryMB = maxMemory / (1024 * 1024);
			long usedMemoryMB = usedMemory / (1024 * 1024);

			details.put("maxMemoryMB", maxMemoryMB);
			details.put("usedMemoryMB", usedMemoryMB);
			details.put("usagePercentage", usagePercentage);
			details.put("availableProcessors", runtime.availableProcessors());

			long executionTime = System.currentTimeMillis() - startTime;

			if (usagePercentage > MEMORY_CRITICAL_THRESHOLD) {
				return new HealthCheckResult(false, "memory", String.format("Critical memory usage: %.2f%% (%dMB/%dMB)",
						usagePercentage, usedMemoryMB, maxMemoryMB), executionTime, details);
			} else if (usagePercentage > MEMORY_WARNING_THRESHOLD) {
				return new HealthCheckResult(false, "memory", String.format("High memory usage: %.2f%% (%dMB/%dMB)",
						usagePercentage, usedMemoryMB, maxMemoryMB), executionTime, details);
			} else if (maxMemoryMB < 512) {
				return new HealthCheckResult(false, "memory", String.format("Low maximum memory: %dMB", maxMemoryMB),
						executionTime, details);
			}

			return new HealthCheckResult(true, "memory", String.format("Memory usage normal: %.2f%% (%dMB/%dMB)",
					usagePercentage, usedMemoryMB, maxMemoryMB), executionTime, details);

		} catch (Exception e) {
			logger.error("Error checking memory usage", e);
			return new HealthCheckResult(false, "memory", "Memory check failed: " + e.getMessage(),
					System.currentTimeMillis() - startTime, details);
		}
	}

	/**
	 * Check disk space availability
	 */
	private HealthCheckResult checkDiskSpace() {
		long startTime = System.currentTimeMillis();
		Map<String, Object> details = new HashMap<>();
		List<String> issues = new ArrayList<>();

		try {
			// Check current directory
			File currentDir = new File(".");
			long freeSpaceBytes = currentDir.getFreeSpace();
			long totalSpaceBytes = currentDir.getTotalSpace();
			long freeSpaceGB = freeSpaceBytes / (1024L * 1024L * 1024L);
			long totalSpaceGB = totalSpaceBytes / (1024L * 1024L * 1024L);

			details.put("currentDirFreeSpaceGB", freeSpaceGB);
			details.put("currentDirTotalSpaceGB", totalSpaceGB);

			if (freeSpaceGB <= DISK_CRITICAL_THRESHOLD_GB) {
				issues.add(String.format("Critical disk space in current directory: %dGB remaining", freeSpaceGB));
			} else if (freeSpaceGB <= DISK_WARNING_THRESHOLD_GB) {
				issues.add(String.format("Low disk space in current directory: %dGB remaining", freeSpaceGB));
			}

			// Check critical directories
			for (String directory : criticalDirectories) {
				try {
					Path dirPath = Paths.get(directory);
					if (Files.exists(dirPath)) {
						File dir = dirPath.toFile();
						long dirFreeSpaceGB = dir.getFreeSpace() / (1024L * 1024L * 1024L);
						details.put(directory + "_freeSpaceGB", dirFreeSpaceGB);

						if (dirFreeSpaceGB <= DISK_CRITICAL_THRESHOLD_GB) {
							issues.add(String.format("Critical disk space in %s: %dGB remaining", directory,
									dirFreeSpaceGB));
						}
					} else {
						// Try to create directory if it doesn't exist
						Files.createDirectories(dirPath);
						details.put(directory + "_status", "created");
					}
				} catch (Exception e) {
					issues.add(String.format("Cannot access directory %s: %s", directory, e.getMessage()));
				}
			}

			long executionTime = System.currentTimeMillis() - startTime;

			if (!issues.isEmpty()) {
				return new HealthCheckResult(false, "disk", "Disk space issues: " + String.join("; ", issues),
						executionTime, details);
			}

			return new HealthCheckResult(true, "disk",
					String.format("Disk space adequate: %dGB free in current directory", freeSpaceGB), executionTime,
					details);

		} catch (Exception e) {
			logger.error("Error checking disk space", e);
			return new HealthCheckResult(false, "disk", "Disk space check failed: " + e.getMessage(),
					System.currentTimeMillis() - startTime, details);
		}
	}

	/**
	 * Check network connectivity
	 */
	private HealthCheckResult checkNetworkConnectivity() {
		long startTime = System.currentTimeMillis();
		Map<String, Object> details = new HashMap<>();
		List<String> unreachableHosts = new ArrayList<>();

		try {
			for (String host : networkHosts) {
				try {
					long hostStartTime = System.currentTimeMillis();
					boolean reachable = InetAddress.getByName(host).isReachable(NETWORK_TIMEOUT_MS);
					long responseTime = System.currentTimeMillis() - hostStartTime;

					details.put(host + "_reachable", reachable);
					details.put(host + "_responseTimeMs", responseTime);

					if (!reachable) {
						unreachableHosts.add(host);
					}
				} catch (Exception e) {
					unreachableHosts.add(host);
					details.put(host + "_error", e.getMessage());
				}
			}

			long executionTime = System.currentTimeMillis() - startTime;

			if (!unreachableHosts.isEmpty()) {
				return new HealthCheckResult(false, "network",
						"Network connectivity issues with hosts: " + String.join(", ", unreachableHosts), executionTime,
						details);
			}

			return new HealthCheckResult(true, "network",
					String.format("Network connectivity verified for %d hosts", networkHosts.size()), executionTime,
					details);

		} catch (Exception e) {
			logger.error("Error checking network connectivity", e);
			return new HealthCheckResult(false, "network", "Network check failed: " + e.getMessage(),
					System.currentTimeMillis() - startTime, details);
		}
	}

	/**
	 * Check JVM health and system properties
	 */
	private HealthCheckResult checkJvmHealth() {
		long startTime = System.currentTimeMillis();
		Map<String, Object> details = new HashMap<>();
		List<String> issues = new ArrayList<>();

		try {
			// Check Java version
			String javaVersion = System.getProperty("java.version");
			details.put("javaVersion", javaVersion);

			// Check JVM vendor
			String javaVendor = System.getProperty("java.vendor");
			details.put("javaVendor", javaVendor);

			// Check operating system
			String osName = System.getProperty("os.name");
			String osVersion = System.getProperty("os.version");
			details.put("osName", osName);
			details.put("osVersion", osVersion);

			// Check system properties that might affect testing
			String userDir = System.getProperty("user.dir");
			details.put("userDir", userDir);

			String tempDir = System.getProperty("java.io.tmpdir");
			details.put("tempDir", tempDir);

			// Check if temp directory is accessible
			try {
				Path tempPath = Paths.get(tempDir);
				if (!Files.exists(tempPath) || !Files.isWritable(tempPath)) {
					issues.add("Temp directory is not accessible: " + tempDir);
				}
			} catch (Exception e) {
				issues.add("Cannot access temp directory: " + e.getMessage());
			}

			// Check available processors
			int processors = Runtime.getRuntime().availableProcessors();
			details.put("availableProcessors", processors);

			if (processors < 2) {
				issues.add("Low number of available processors: " + processors);
			}

			long executionTime = System.currentTimeMillis() - startTime;

			if (!issues.isEmpty()) {
				return new HealthCheckResult(false, "jvm", "JVM health issues: " + String.join("; ", issues),
						executionTime, details);
			}

			return new HealthCheckResult(true, "jvm",
					String.format("JVM healthy: %s on %s (%d processors)", javaVersion, osName, processors),
					executionTime, details);

		} catch (Exception e) {
			logger.error("Error checking JVM health", e);
			return new HealthCheckResult(false, "jvm", "JVM health check failed: " + e.getMessage(),
					System.currentTimeMillis() - startTime, details);
		}
	}

	/**
	 * Check file system permissions
	 */
	private HealthCheckResult checkFileSystemPermissions() {
		long startTime = System.currentTimeMillis();
		Map<String, Object> details = new HashMap<>();
		List<String> issues = new ArrayList<>();

		try {
			// Check current directory permissions
			File currentDir = new File(".");
			details.put("currentDirReadable", currentDir.canRead());
			details.put("currentDirWritable", currentDir.canWrite());
			details.put("currentDirExecutable", currentDir.canExecute());

			if (!currentDir.canRead()) {
				issues.add("Current directory is not readable");
			}
			if (!currentDir.canWrite()) {
				issues.add("Current directory is not writable");
			}

			// Test write permissions with temporary file
			try {
				Path testFile = Paths.get("health-check-temp.tmp");
				Files.write(testFile, "test".getBytes());
				Files.delete(testFile);
				details.put("writeTestPassed", true);
			} catch (Exception e) {
				issues.add("Write permission test failed: " + e.getMessage());
				details.put("writeTestPassed", false);
			}

			// Check critical directories
			for (String directory : criticalDirectories) {
				try {
					Path dirPath = Paths.get(directory);
					if (Files.exists(dirPath)) {
						boolean readable = Files.isReadable(dirPath);
						boolean writable = Files.isWritable(dirPath);

						details.put(directory + "_readable", readable);
						details.put(directory + "_writable", writable);

						if (!readable) {
							issues.add(String.format("Directory %s is not readable", directory));
						}
						if (!writable) {
							issues.add(String.format("Directory %s is not writable", directory));
						}
					}
				} catch (Exception e) {
					issues.add(String.format("Cannot check permissions for %s: %s", directory, e.getMessage()));
				}
			}

			long executionTime = System.currentTimeMillis() - startTime;

			if (!issues.isEmpty()) {
				return new HealthCheckResult(false, "filesystem",
						"File system permission issues: " + String.join("; ", issues), executionTime, details);
			}

			return new HealthCheckResult(true, "filesystem", "File system permissions are adequate", executionTime,
					details);

		} catch (Exception e) {
			logger.error("Error checking file system permissions", e);
			return new HealthCheckResult(false, "filesystem", "File system check failed: " + e.getMessage(),
					System.currentTimeMillis() - startTime, details);
		}
	}

	/**
	 * Get default network hosts to check
	 */
	private static List<String> getDefaultNetworkHosts() {
		return List.of("google.com", "github.com", "localhost");
	}

	/**
	 * Get default critical directories to check
	 */
	private static List<String> getDefaultCriticalDirectories() {
		return List.of("./test-output", "./test-output/screenshots", "./test-output/downloads", "./logs");
	}

	/**
	 * Get health check statistics
	 */
	public Map<String, Object> getHealthCheckStatistics() {
		Map<String, Object> stats = new HashMap<>();
		stats.put("registeredCustomChecks", customHealthChecks.size());
		stats.put("enabledFeatures",
				Map.of("memory", enableMemoryChecks, "disk", enableDiskChecks, "network", enableNetworkChecks));
		stats.put("networkHosts", networkHosts.size());
		stats.put("criticalDirectories", criticalDirectories.size());
		stats.put("healthCheckTimeoutMs", HEALTH_CHECK_TIMEOUT_MS);

		return stats;
	}

	/**
	 * Remove custom health check
	 */
	public boolean removeHealthCheck(String name) {
		boolean removed = customHealthChecks.remove(name) != null;
		if (removed) {
			logger.info("Removed custom health check: {}", name);
		}
		return removed;
	}

	/**
	 * Get list of registered health check names
	 */
	public List<String> getRegisteredHealthCheckNames() {
		return new ArrayList<>(customHealthChecks.keySet());
	}
}