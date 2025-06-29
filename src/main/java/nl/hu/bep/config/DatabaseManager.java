package nl.hu.bep.config;

import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class DatabaseManager {
    private final String jdbcUrl;
    private final String username;
    private final String password;

    public DatabaseManager(String driver, String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("JDBC driver not found: " + driver, e);
        }
    }

    public Connection getConnection() throws SQLException {
        if (username != null && password != null) {
            return DriverManager.getConnection(jdbcUrl, username, password);
        }
        return DriverManager.getConnection(jdbcUrl);
    }

    public void initializeSchema() {
        log.info("Initializing database schema...");
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             InputStream is = getClass().getClassLoader().getResourceAsStream("schema.sql")) {

            if (is == null) {
                throw new RuntimeException("schema.sql not found on classpath");
            }

            StringBuilder script = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    script.append(line).append(System.lineSeparator());
                }
            }
            statement.execute(script.toString());
            log.info("Database schema initialized successfully.");

        } catch (SQLException | IOException e) {
            throw new RuntimeException("Failed to execute database schema script", e);
        }
    }
} 