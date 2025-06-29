package nl.hu.bep.common;

import nl.hu.bep.config.DatabaseManager;
import org.glassfish.hk2.api.Factory;

import java.io.InputStream;
import java.util.Properties;

public class TestDatabaseManagerFactory implements Factory<DatabaseManager> {

    @Override
    public DatabaseManager provide() {
        try {
            Properties props = new Properties();
            try (InputStream input = getClass().getClassLoader().getResourceAsStream("test.properties")) {
                if (input == null) {
                    throw new RuntimeException("test.properties not found on the classpath");
                }
                props.load(input);
            }
            DatabaseManager dbManager = new DatabaseManager(
                    props.getProperty("jdbc.driver"),
                    props.getProperty("jdbc.url"),
                    props.getProperty("jdbc.username"),
                    props.getProperty("jdbc.password")
            );
            dbManager.initializeSchema();
            return dbManager;
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize test DatabaseManager", e);
        }
    }

    @Override
    public void dispose(DatabaseManager instance) {
    }
} 