package nl.hu.bep.config;

import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@ApplicationScoped
public class DatabaseConfig {
    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static String jdbcUrl;
    private static String username;
    private static String password;
    
    @Resource(lookup = "java:comp/DefaultDataSource")
    private DataSource dataSource;

    public static synchronized void initialize() {
        if (initialized.get()) {
            log.info("Database already initialized, skipping re-initialization");
            return;
        }
        log.info("Initializing database configuration...");
        try {
            createDataSource();
            initialized.set(true);
            log.info("Database configuration initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize database configuration: {}", e.getMessage(), e);
            initialized.set(true);
        }
    }

    private static void createDataSource() {
        // For simplicity, use DriverManager directly (no pool)
        String host, port, database;
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl != null && databaseUrl.startsWith("postgres://")) {
            // Parse DATABASE_URL (e.g., postgres://user:pass@host:port/db)
            String[] urlParts = databaseUrl.split("[@/:]");
            username = urlParts[3];
            password = urlParts[4];
            host = urlParts[5];
            port = urlParts[6];
            database = urlParts[7];
            jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
        } else {
            host = System.getenv().getOrDefault("DB_HOST", "localhost");
            port = System.getenv().getOrDefault("DB_PORT", "5432");
            database = System.getenv().getOrDefault("DB_NAME", "aquariumdb");
            username = System.getenv().getOrDefault("DB_USER", "postgres");
            password = System.getenv().getOrDefault("DB_PASSWORD", "postgres");
            jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
        }
        log.info("JDBC URL: {}", jdbcUrl.replaceAll("password=[^&]*", "password=***"));
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }
    
    // For container-managed transactions, provide DataSource access
    public Connection getManagedConnection() throws SQLException {
        if (dataSource != null) {
            return dataSource.getConnection();
        }
        // Fallback to direct connection if DataSource not available
        return getConnection();
    }

    public static boolean isHealthy() {
        try (Connection connection = getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            log.error("Database health check failed: {}", e.getMessage());
            return false;
        }
    }
}