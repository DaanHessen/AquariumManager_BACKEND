package nl.hu.bep.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;
import nl.hu.bep.data.exception.RepositoryException;

import javax.sql.DataSource;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class DatabaseConfig {
    private static final String PERSISTENCE_UNIT_NAME = "aquariumPU";
    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static final Object INIT_LOCK = new Object();
    
    private static volatile EntityManagerFactory emf;
    private static volatile HikariDataSource dataSource;

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
                port = String.valueOf(dbUri.getPort());
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
        
        int poolSize = Integer.parseInt(getEnv("DB_POOL_SIZE", "10"));
        
        log.info("Attempting to connect to database with JDBC URL: {}", jdbcUrl.replaceAll("password=[^&]*", "password=***"));
        
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");
        
        config.setMaximumPoolSize(poolSize);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(30000);
        
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        
        try {
            dataSource = new HikariDataSource(config);
            log.info("PostgreSQL connection pool initialized successfully: {}:{}/{}", host, port, database);
        } catch (Exception e) {
            log.error("Failed to initialize PostgreSQL connection pool for {}:{}/{}", host, port, database, e);
            throw e;
        }
    }
    
    private static void createEntityManagerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");
        props.put("jakarta.persistence.jdbc.url", dataSource.getJdbcUrl());
        props.put("jakarta.persistence.jdbc.user", dataSource.getUsername());
        props.put("jakarta.persistence.jdbc.password", dataSource.getPassword());
        
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.hbm2ddl.auto", getEnv("HIBERNATE_HBM2DDL", "update"));
        props.put("hibernate.show_sql", "true");
        props.put("hibernate.format_sql", "true");
        props.put("hibernate.use_sql_comments", "true");
        
        props.put("hibernate.connection.provider_class", "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
        props.put("hibernate.hikari.dataSource", dataSource);
        
        emf = Persistence.createEntityManagerFactory("aquarium-pu", props);
        log.info("EntityManagerFactory created successfully");
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
        if (!initialized.get()) initialize();
        return dataSource;
    }

    public static boolean isHealthy() {
        if (!initialized.get()) {
            log.warn("Database health check failed: Database not initialized");
            return false;
        }
        
        // If EMF or DataSource are null, try to initialize them
        if (emf == null || dataSource == null) {
            log.warn("Database components not ready, attempting re-initialization...");
            try {
                if (dataSource == null) {
                    createDataSource();
                }
                if (emf == null) {
                    createEntityManagerFactory();
                }
            } catch (Exception e) {
                log.error("Failed to re-initialize database components: {}", e.getMessage());
                return false;
            }
        }
        
        if (dataSource != null && dataSource.isClosed()) {
            log.warn("DataSource is closed");
            return false;
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
        if (emf != null && emf.isOpen()) {
            emf.close();
            emf = null;
        }
        
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            dataSource = null;
        }
        
        initialized.set(false);
        log.info("Database resources shut down");
    }
    
    private static String getEnv(String name, String defaultValue) {
        String value = System.getenv(name);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }
} 