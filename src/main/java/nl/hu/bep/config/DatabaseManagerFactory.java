package nl.hu.bep.config;

import org.glassfish.hk2.api.Factory;

public class DatabaseManagerFactory implements Factory<DatabaseManager> {

    @Override
    public DatabaseManager provide() {
        String driver = "org.postgresql.Driver";
        String jdbcUrl = System.getenv("DATABASE_URL");
        if (jdbcUrl == null || jdbcUrl.isBlank()) {
            throw new IllegalStateException("DATABASE_URL environment variable must be set for production.");
        }
        return new DatabaseManager(driver, jdbcUrl, null, null);
    }

    @Override
    public void dispose(DatabaseManager instance) {
    }
} 