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

    public static void initialize() {
        if (initialized.get()) return;
        
        synchronized (INIT_LOCK) {
            if (initialized.get()) return;
            
            try {
                log.info("Initializing PostgreSQL database");
                createDataSource();
                createEntityManagerFactory();
                initialized.set(true);
                log.info("Database configuration initialized successfully");
            } catch (Exception e) {
                log.error("Failed to initialize database", e);
                shutdown();
                throw new RepositoryException.DatabaseConfigException("Database initialization failed", e);
            }
        }
    }

    private static void createDataSource() {
        String host, port, database, username, password;
        
        // Check if we're running on Heroku (DATABASE_URL is provided)
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            // Parse Heroku DATABASE_URL: postgres://username:password@hostname:port/database
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
                
                log.info("Using Heroku DATABASE_URL configuration");
            } catch (Exception e) {
                log.error("Failed to parse DATABASE_URL, falling back to individual environment variables", e);
                // Fall back to individual environment variables
                host = getEnv("DB_HOST", "localhost");
                port = getEnv("DB_PORT", "5432");
                database = getEnv("DB_NAME", "aquariumdb");
                username = getEnv("DB_USER", "postgres");
                password = getEnv("DB_PASSWORD", "postgres");
            }
        } else {
            // Use individual environment variables (for local development)
            host = getEnv("DB_HOST", "localhost");
            port = getEnv("DB_PORT", "5432");
            database = getEnv("DB_NAME", "aquariumdb");
            username = getEnv("DB_USER", "postgres");
            password = getEnv("DB_PASSWORD", "postgres");
        }
        
        int poolSize = Integer.parseInt(getEnv("DB_POOL_SIZE", "10"));
        
        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
        
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
        
        dataSource = new HikariDataSource(config);
        log.info("PostgreSQL connection pool initialized: {}:{}/{}", host, port, database);
    }
    
    private static void createEntityManagerFactory() {
        Map<String, Object> props = new HashMap<>();
        
        props.put("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");
        props.put("jakarta.persistence.jdbc.url", dataSource.getJdbcUrl());
        props.put("jakarta.persistence.jdbc.user", dataSource.getUsername());
        props.put("jakarta.persistence.jdbc.password", dataSource.getPassword());
        
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.hbm2ddl.auto", getEnv("HIBERNATE_HBM2DDL", "update"));
        props.put("hibernate.show_sql", "false");
        props.put("hibernate.format_sql", "false");
        props.put("hibernate.enable_lazy_load_no_trans", "true");
        props.put("hibernate.connection.provider_class", "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
        
        // Production settings
        props.put("hibernate.connection.release_mode", "after_transaction");
        props.put("hibernate.jdbc.batch_size", "50");
        
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, props);
        log.info("EntityManagerFactory created");
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
        if (!initialized.get() || emf == null || dataSource == null || dataSource.isClosed()) {
            return false;
        }
        
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createNativeQuery("SELECT 1").getSingleResult();
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            log.warn("Database health check failed", e);
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