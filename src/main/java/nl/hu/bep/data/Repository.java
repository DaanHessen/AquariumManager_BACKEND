package nl.hu.bep.data;

import nl.hu.bep.config.DatabaseConfig;
import nl.hu.bep.exception.infrastructure.RepositoryException;
import nl.hu.bep.data.interfaces.IRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Ultra-simple base repository - PURE JDBC operations only.
 * No business logic, no complex conversions, just SQL.
 */
@ApplicationScoped
public abstract class Repository<T, ID> implements IRepository<T, ID> {
    
    // Abstract methods - minimal and focused
    protected abstract T mapRow(ResultSet rs) throws SQLException;
    protected abstract void setInsertParameters(PreparedStatement ps, T entity) throws SQLException;
    protected abstract void setUpdateParameters(PreparedStatement ps, T entity) throws SQLException;
    protected abstract String getTableName();
    protected abstract String getIdColumn();
    protected abstract String getInsertSql();
    protected abstract String getUpdateSql();

    // Pure JDBC operations - no logic
    public Optional<T> findById(ID id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE " + getIdColumn() + " = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RepositoryException("Find by ID failed: " + id, e);
        }
    }

    public List<T> findAll() {
        String sql = "SELECT * FROM " + getTableName();
        List<T> result = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Find all failed", e);
        }
        return result;
    }

    public T insert(T entity) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(getInsertSql(), Statement.RETURN_GENERATED_KEYS)) {
            setInsertParameters(ps, entity);
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    ID generatedId = (ID) rs.getObject(1);
                    return findById(generatedId).orElse(entity);
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new RepositoryException("Insert failed", e);
        }
    }

    public T update(T entity) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(getUpdateSql())) {
            setUpdateParameters(ps, entity);
            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RepositoryException("Update failed", e);
        }
    }

    public void deleteById(ID id) {
        String sql = "DELETE FROM " + getTableName() + " WHERE " + getIdColumn() + " = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Delete failed: " + id, e);
        }
    }

    // Simple field search - no complex logic
    public List<T> findByField(String fieldName, Object value) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE " + fieldName + " = ?";
        List<T> result = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Find by field failed: " + fieldName, e);
        }
        return result;
    }

    // Minimal helpers - ONLY for null handling
    protected static Long getLong(ResultSet rs, String col) throws SQLException {
        long val = rs.getLong(col);
        return rs.wasNull() ? null : val;
    }

    protected static void setLong(PreparedStatement ps, int idx, Long val) throws SQLException {
        if (val != null) ps.setLong(idx, val);
        else ps.setNull(idx, Types.BIGINT);
    }

    protected static LocalDateTime getDateTime(ResultSet rs, String col) throws SQLException {
        Timestamp ts = rs.getTimestamp(col);
        return ts != null ? ts.toLocalDateTime() : null;
    }

    protected static void setDateTime(PreparedStatement ps, int idx, LocalDateTime val) throws SQLException {
        if (val != null) ps.setTimestamp(idx, Timestamp.valueOf(val));
        else ps.setNull(idx, Types.TIMESTAMP);
    }
}
