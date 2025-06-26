package nl.hu.bep.data;

import nl.hu.bep.config.DatabaseConfig;
import nl.hu.bep.exception.ApplicationException;
import nl.hu.bep.data.interfaces.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public abstract class RepositoryImpl<T, ID> implements Repository<T, ID> {
    
    protected abstract T mapRow(ResultSet rs) throws SQLException;
    protected abstract void setInsertParameters(PreparedStatement ps, T entity) throws SQLException;
    protected abstract void setUpdateParameters(PreparedStatement ps, T entity) throws SQLException;
    protected abstract String getTableName();
    protected abstract String getIdColumn();
    protected abstract String getInsertSql();
    protected abstract String getUpdateSql();

    public Optional<T> findById(ID id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE " + getIdColumn() + " = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new ApplicationException.ConflictException("Find by ID failed: " + id, e);
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
            throw new ApplicationException.ConflictException("Find all failed", e);
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
                    @SuppressWarnings("unchecked")
                    ID generatedId = (ID) rs.getObject(1);
                    return findById(generatedId).orElse(entity);
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new ApplicationException.ConflictException("Insert failed", e);
        }
    }

    public T update(T entity) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(getUpdateSql())) {
            setUpdateParameters(ps, entity);
            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new ApplicationException.ConflictException("Update failed", e);
        }
    }

    public void deleteById(ID id) {
        String sql = "DELETE FROM " + getTableName() + " WHERE " + getIdColumn() + " = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ApplicationException.ConflictException("Delete failed: " + id, e);
        }
    }

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
            throw new ApplicationException.ConflictException("Find by field failed: " + fieldName, e);
        }
        return result;
    }

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
