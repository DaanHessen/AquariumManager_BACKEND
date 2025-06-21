package nl.hu.bep.config;

import lombok.extern.slf4j.Slf4j;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class DatabaseConfig {
    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static String jdbcUrl;

    static {
        // Explicitly load PostgreSQL driver
        try {
            Class.forName("org.postgresql.Driver");
            log.info("PostgreSQL JDBC driver loaded successfully");
        } catch (ClassNotFoundException e) {
            log.error("Failed to load PostgreSQL JDBC driver", e);
            throw new RuntimeException("PostgreSQL JDBC driver not found", e);
        }
    }

    public static synchronized void initialize() {
        if (initialized.get()) {
            log.info("Database already initialized, skipping re-initialization");
            return;
        }
        log.info("Initializing database configuration...");
        String envUrl = System.getenv("DATABASE_URL");
        if (envUrl == null || envUrl.isBlank()) {
            log.error("DATABASE_URL environment variable must be set (JDBC format, e.g. jdbc:postgresql://user:pass@host:port/db)");
            throw new IllegalStateException("DATABASE_URL environment variable must be set");
        }
        jdbcUrl = envUrl;
        log.info("JDBC URL: {}", jdbcUrl.replaceAll("password=[^&]*", "password=***"));
        initialized.set(true);
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl);
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