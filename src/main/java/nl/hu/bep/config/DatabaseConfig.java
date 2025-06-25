package nl.hu.bep.config;

import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        
        // Initialize schema if database is empty
        initializeSchemaIfEmpty();
        
        initialized.set(true);
    }

    private static void initializeSchemaIfEmpty() {
        try (Connection connection = getConnection()) {
            if (isDatabaseEmpty(connection)) {
                log.info("Database is empty, initializing schema...");
                executeSchemaScript(connection);
                log.info("Database schema initialized successfully");
            } else {
                log.info("Database already contains tables, skipping schema initialization");
            }
        } catch (SQLException e) {
            log.error("Failed to initialize database schema", e);
            throw new RuntimeException("Failed to initialize database schema", e);
        }
    }

    private static boolean isDatabaseEmpty(Connection connection) throws SQLException {
        String query = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_type = 'BASE TABLE'";
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            int tableCount = rs.getInt(1);
            log.info("Found {} tables in database", tableCount);
            return tableCount == 0;
        }
    }

    private static void executeSchemaScript(Connection connection) throws SQLException {
        try (InputStream is = DatabaseConfig.class.getResourceAsStream("/schema.sql")) {
            if (is == null) {
                // Try alternative path
                try (InputStream altIs = DatabaseConfig.class.getClassLoader().getResourceAsStream("schema.sql")) {
                    if (altIs == null) {
                        throw new RuntimeException("Schema file not found in classpath");
                    }
                    executeScript(connection, altIs);
                }
            } else {
                executeScript(connection, is);
            }
        } catch (IOException e) {
            throw new SQLException("Failed to read schema file", e);
        }
    }

    private static void executeScript(Connection connection, InputStream inputStream) throws SQLException, IOException {
        StringBuilder script = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("--")) {
                    script.append(line).append("\n");
                }
            }
        }

        // Execute the script
        try (Statement statement = connection.createStatement()) {
            statement.execute(script.toString());
        }
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