package nl.hu.bep.data;

import nl.hu.bep.domain.Ornament;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class OrnamentRepository extends Repository<Ornament, Long> {
    
    @Override
    protected String getTableName() {
        return "ornaments";
    }
    
    @Override
    protected String getIdColumn() {
        return "id";
    }
    
    @Override
    protected String getInsertSql() {
        return "INSERT INTO ornaments (name, description, color, material, is_air_pump_compatible, owner_id, aquarium_id, date_created) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String getUpdateSql() {
        return "UPDATE ornaments SET name = ?, description = ?, color = ?, material = ?, is_air_pump_compatible = ?, owner_id = ?, aquarium_id = ?, date_created = ? WHERE id = ?";
    }
    
    @Override
    protected Ornament mapRow(ResultSet rs) throws SQLException {
        return Ornament.reconstruct(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("color"),
                rs.getString("material"),
                rs.getBoolean("is_air_pump_compatible"),
                rs.getLong("owner_id"),
                getLongOrNull(rs, "aquarium_id"),
                rs.getTimestamp("date_created").toLocalDateTime()
        );
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement ps, Ornament ornament) throws SQLException {
        ps.setString(1, ornament.getName());
        ps.setString(2, ornament.getDescription());
        ps.setString(3, ornament.getColor());
        ps.setString(4, ornament.getMaterial());
        ps.setBoolean(5, ornament.isAirPumpCompatible());
        ps.setLong(6, ornament.getOwnerId());
        setLongOrNull(ps, 7, ornament.getAquariumId());
        ps.setTimestamp(8, Timestamp.valueOf(ornament.getDateCreated()));
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement ps, Ornament ornament) throws SQLException {
        setInsertParameters(ps, ornament);
        ps.setLong(9, ornament.getId());
    }

    // Helper methods for nullable Long values
    private Long getLongOrNull(ResultSet rs, String columnName) throws SQLException {
        long value = rs.getLong(columnName);
        return rs.wasNull() ? null : value;
    }
    
    private void setLongOrNull(PreparedStatement ps, int parameterIndex, Long value) throws SQLException {
        if (value != null) {
            ps.setLong(parameterIndex, value);
        } else {
            ps.setNull(parameterIndex, Types.BIGINT);
        }
    }
    
    /**
     * Find ornaments by owner ID
     */
    public List<Ornament> findByOwnerId(Long ownerId) {
        return findByField("owner_id", ownerId);
    }
    
    /**
     * Find ornaments by aquarium ID
     */
    public List<Ornament> findByAquariumId(Long aquariumId) {
        return findByField("aquarium_id", aquariumId);
    }
}