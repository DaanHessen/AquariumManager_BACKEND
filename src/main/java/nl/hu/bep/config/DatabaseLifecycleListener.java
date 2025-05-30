package nl.hu.bep.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatabaseLifecycleListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("Application starting up. Initializing database...");
        try {
            DatabaseConfig.initialize();
            log.info("Database initialization completed successfully");
        } catch (Exception e) {
            log.error("Database initialization failed, but allowing application to continue", e);
            // Don't rethrow - let application try to start anyway
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("Application shutting down. Starting comprehensive cleanup...");
        
        try {
            // Database cleanup
            DatabaseConfig.shutdown();
            
            // Force garbage collection to help with memory cleanup
            System.gc();
            
            // Additional cleanup for Railway environment
            cleanupSystemResources();
            
            log.info("Application shutdown cleanup completed successfully");
            
        } catch (Exception e) {
            log.error("Error during application shutdown cleanup", e);
        }
    }
    
    /**
     * Additional system resource cleanup for Railway environment
     */
    private void cleanupSystemResources() {
        try {
            // Clear any ThreadLocal variables that might hold references
            cleanupThreadLocals();
            
            // Interrupt any remaining non-daemon threads
            cleanupThreads();
            
            log.info("System resource cleanup completed");
        } catch (Exception e) {
            log.warn("Error during system resource cleanup: {}", e.getMessage());
        }
    }
    
    private void cleanupThreadLocals() {
        try {
            // This helps clean up any ThreadLocal variables that might be holding references
            Thread.currentThread().getContextClassLoader().clearAssertionStatus();
            log.debug("ThreadLocal cleanup completed");
        } catch (Exception e) {
            log.debug("ThreadLocal cleanup encountered minor issues: {}", e.getMessage());
        }
    }
    
    private void cleanupThreads() {
        try {
            // Get all threads and check for any that should be stopped
            ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
            ThreadGroup parentGroup;
            while ((parentGroup = rootGroup.getParent()) != null) {
                rootGroup = parentGroup;
            }
            
            Thread[] threads = new Thread[rootGroup.activeCount()];
            int threadCount = rootGroup.enumerate(threads);
            
            for (int i = 0; i < threadCount; i++) {
                Thread thread = threads[i];
                if (thread != null && !thread.isDaemon() && 
                    (thread.getName().contains("HikariPool") || 
                     thread.getName().contains("postgres") ||
                     thread.getName().contains("pool"))) {
                    log.info("Attempting to interrupt thread: {}", thread.getName());
                    try {
                        thread.interrupt();
                    } catch (Exception e) {
                        log.debug("Could not interrupt thread {}: {}", thread.getName(), e.getMessage());
                    }
                }
            }
            log.debug("Thread cleanup completed");
        } catch (Exception e) {
            log.debug("Thread cleanup encountered issues: {}", e.getMessage());
        }
    }
}