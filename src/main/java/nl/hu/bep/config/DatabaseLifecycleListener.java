package nl.hu.bep.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;

@WebListener
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
        log.info("Application shutting down. Closing database connections...");
        try {
            DatabaseConfig.shutdown();
            log.info("Database connections closed successfully");
        } catch (Exception e) {
            log.error("Error closing database connections", e);
        }
    }
}