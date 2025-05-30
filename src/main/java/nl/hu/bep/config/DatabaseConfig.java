package nl.hu.bep.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;
import nl.hu.bep.data.exception.RepositoryException;

import javax.sql.DataSource;
import java.net.URI;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Enumeration;

@Slf4j
public class DatabaseConfig {
    private static final String PERSISTENCE_UNIT_NAME = "aquariumPU";
    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static final Object INIT_LOCK = new Object();
    
    private static volatile EntityManagerFactory emf;

    public static synchronized void initialize() {
        if (initialized.get()) {
            log.info("Database already initialized, skipping re-initialization");
            return;
        }

        log.info("Initializing database configuration...");
        
        try {
            createDataSource();
            createEntityManagerFactory();
            
            // Test the connection but don't fail startup if it's not ready
            try {
                testConnection();
                log.info("Database connection test successful");
            } catch (Exception e) {
                log.warn("Database connection test failed during initialization, but continuing startup. Connection will be retried on first use. Error: {}", e.getMessage());
                // Don't throw the exception - allow the application to start even if DB is not ready
            }
            
            initialized.set(true);
            log.info("Database configuration initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize database configuration: {}", e.getMessage(), e);
            // For Railway health checks, we still mark as initialized to allow app startup
            // The health check endpoint will handle database availability separately
            initialized.set(true);
            throw new RuntimeException("Database configuration failed", e);
        }
    }

    private static void createDataSource() {
        String host, port, database, username, password;
        String jdbcUrl;
        
        // Check if we're running on Railway/Heroku/Neon (DATABASE_URL is provided)
        String databaseUrl = System.getenv("DATABASE_URL");
        log.info("DATABASE_URL environment variable: {}", databaseUrl != null ? "present" : "missing");
        
        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            // Parse DATABASE_URL: postgres://username:password@hostname:port/database?sslmode=require
            try {
                URI dbUri = new URI(databaseUrl);
                host = dbUri.getHost();
                // Handle case where no port is specified in URL (dbUri.getPort() returns -1)
                int portValue = dbUri.getPort();
                port = portValue == -1 ? "5432" : String.valueOf(portValue);
                database = dbUri.getPath().substring(1); // Remove leading slash
                
                String userInfo = dbUri.getUserInfo();
                if (userInfo != null) {
                    String[] userParts = userInfo.split(":");
                    username = userParts[0];
                    password = userParts.length > 1 ? userParts[1] : "";
                } else {
                    username = "postgres";
                    password = "";
                }
                
                // Convert postgres:// to jdbc:postgresql:// and preserve query parameters (like sslmode=require)
                String query = dbUri.getQuery();
                if (query != null && !query.isEmpty()) {
                    jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s?%s", host, port, database, query);
                } else {
                    jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
                }
                
                log.info("Using DATABASE_URL configuration: host={}, port={}, database={}, username={}", 
                    host, port, database, username);
                log.info("JDBC URL with SSL parameters: {}", jdbcUrl.replaceAll("password=[^&]*", "password=***"));
            } catch (Exception e) {
                log.error("Failed to parse DATABASE_URL: {}, falling back to individual environment variables", databaseUrl, e);
                // Fall back to individual environment variables
                host = getEnv("DB_HOST", "localhost");
                port = getEnv("DB_PORT", "5432");
                database = getEnv("DB_NAME", "aquariumdb");
                username = getEnv("DB_USER", "postgres");
                password = getEnv("DB_PASSWORD", "postgres");
                jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
            }
        } else {
            // Use individual environment variables (for local development)
            log.info("DATABASE_URL not found, using individual environment variables");
            host = getEnv("DB_HOST", "localhost");
            port = getEnv("DB_PORT", "5432");
            database = getEnv("DB_NAME", "aquariumdb");
            username = getEnv("DB_USER", "postgres");
            password = getEnv("DB_PASSWORD", "postgres");
            log.info("Using individual DB config: host={}, port={}, database={}, username={}", 
                host, port, database, username);
            jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
        }
        
        // Store connection details for JPA configuration
        System.setProperty("aquarium.db.url", jdbcUrl);
        System.setProperty("aquarium.db.username", username);
        System.setProperty("aquarium.db.password", password);
        
        log.info("Database connection details prepared for JPA: {}:{}/{}", host, port, database);
    }
    
    private static void createEntityManagerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");
        props.put("jakarta.persistence.jdbc.url", System.getProperty("aquarium.db.url"));
        props.put("jakarta.persistence.jdbc.user", System.getProperty("aquarium.db.username"));
        props.put("jakarta.persistence.jdbc.password", System.getProperty("aquarium.db.password"));
        
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.hbm2ddl.auto", getEnv("HIBERNATE_HBM2DDL", "update"));
        
        // Railway memory optimization - disable SQL logging
        props.put("hibernate.show_sql", "false");
        props.put("hibernate.format_sql", "false");
        props.put("hibernate.use_sql_comments", "false");
        
        // Configure Hibernate to use HikariCP connection pool with Railway constraints
        props.put("hibernate.connection.provider_class", "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
        
        // Ultra-conservative settings for Railway memory limits
        props.put("hibernate.hikari.maximumPoolSize", "3");
        props.put("hibernate.hikari.minimumIdle", "1");
        props.put("hibernate.hikari.idleTimeout", "30000");
        props.put("hibernate.hikari.connectionTimeout", "15000");
        props.put("hibernate.hikari.maxLifetime", "180000");
        props.put("hibernate.hikari.leakDetectionThreshold", "60000");
        props.put("hibernate.hikari.keepaliveTime", "30000");
        
        // Additional memory optimizations
        props.put("hibernate.jdbc.batch_size", "5");
        props.put("hibernate.cache.use_second_level_cache", "false");
        props.put("hibernate.cache.use_query_cache", "false");
        props.put("hibernate.bytecode.use_reflection_optimizer", "false");
        props.put("hibernate.query.plan_cache_max_size", "64");
        props.put("hibernate.query.plan_parameter_metadata_max_size", "32");
        props.put("hibernate.generate_statistics", "false");
        
        emf = Persistence.createEntityManagerFactory("aquariumPU", props);
        log.info("EntityManagerFactory created successfully with memory-optimized settings");
    }

    private static void testConnection() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createNativeQuery("SELECT 1").getSingleResult();
            em.getTransaction().commit();
            log.debug("Database connection test successful");
        } catch (Exception e) {
            log.error("Database connection test failed: {}", e.getMessage());
            throw e;
        }
    }

    public static EntityManager createEntityManager() {
        if (!initialized.get()) initialize();
        return emf.createEntityManager();
    }

    public static DataSource getDataSource() {
        // Since we're using Hibernate-managed connection pooling, 
        // we don't expose a separate DataSource object
        throw new UnsupportedOperationException("DataSource is managed internally by Hibernate");
    }

    public static boolean isHealthy() {
        if (!initialized.get()) {
            log.warn("Database health check failed: Database not initialized");
            return false;
        }
        
        // If EMF is null, try to initialize it
        if (emf == null) {
            log.warn("EntityManagerFactory not ready, attempting re-initialization...");
            try {
                initialize();
            } catch (Exception e) {
                log.error("Failed to re-initialize database components: {}", e.getMessage());
                return false;
            }
        }
        
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createNativeQuery("SELECT 1").getSingleResult();
            em.getTransaction().commit();
            log.debug("Database health check passed");
            return true;
        } catch (Exception e) {
            log.error("Database health check failed with exception: {}", e.getMessage());
            return false;
        }
    }

    public static synchronized void shutdown() {
        log.info("Starting database shutdown procedure...");
        
        try {
            // Close EntityManagerFactory first
            if (emf != null && emf.isOpen()) {
                log.info("Closing EntityManagerFactory...");
                emf.close();
                emf = null;
                log.info("EntityManagerFactory closed successfully");
            }
            
            // Force JDBC driver cleanup to prevent memory leaks
            cleanupJdbcDrivers();
            
            // Clear system properties
            System.clearProperty("aquarium.db.url");
            System.clearProperty("aquarium.db.username");
            System.clearProperty("aquarium.db.password");
            
            initialized.set(false);
            log.info("Database resources shut down successfully");
            
        } catch (Exception e) {
            log.error("Error during database shutdown: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Clean up JDBC drivers to prevent memory leaks
     */
    private static void cleanupJdbcDrivers() {
        try {
            log.info("Cleaning up JDBC drivers...");
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                try {
                    if (driver.getClass().getName().contains("postgresql")) {
                        log.info("Deregistering PostgreSQL JDBC driver: {}", driver.getClass().getName());
                        DriverManager.deregisterDriver(driver);
                    }
                } catch (SQLException e) {
                    log.warn("Error deregistering JDBC driver {}: {}", driver.getClass().getName(), e.getMessage());
                }
            }
            log.info("JDBC driver cleanup completed");
        } catch (Exception e) {
            log.error("Error during JDBC driver cleanup: {}", e.getMessage(), e);
        }
    }
    
    private static String getEnv(String name, String defaultValue) {
        String value = System.getenv(name);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }
} 