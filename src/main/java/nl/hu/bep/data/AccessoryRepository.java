package nl.hu.bep.data;

import nl.hu.bep.domain.Accessory;
import java.sql.*;
import java.time.LocalTime;
import java.util.List;

/**
 * Ultra-simple AccessoryRepository - PURE JDBC operations only.
 * No complex polymorphic logic or type switching.
 */
public class AccessoryRepository extends Repository<Accessory, Long> {
    
    @Override
    protected String getTableName() { return "accessories"; }
    
    @Override
    protected String getIdColumn() { return "id"; }
    
    @Override
    protected String getInsertSql() {
        return "INSERT INTO accessories (model, serial_number, owner_id, aquarium_id, color, description, date_created, accessory_type, is_external, capacity_liters, is_led, time_on, time_off, min_temperature, max_temperature, current_temperature) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String getUpdateSql() {
        return "UPDATE accessories SET model = ?, serial_number = ?, owner_id = ?, aquarium_id = ?, color = ?, description = ?, date_created = ?, accessory_type = ?, is_external = ?, capacity_liters = ?, is_led = ?, time_on = ?, time_off = ?, min_temperature = ?, max_temperature = ?, current_temperature = ? WHERE id = ?";
    }
    
    @Override
    protected Accessory mapRow(ResultSet rs) throws SQLException {
        // Simple approach - let domain handle the complexity
        return Accessory.reconstruct(
                rs.getString("accessory_type"),
                rs.getLong("id"),
                rs.getString("model"),
                rs.getString("serial_number"),
                rs.getLong("owner_id"),
                getLong(rs, "aquarium_id"),
                rs.getString("color"),
                rs.getString("description"),
                rs.getTimestamp("date_created").toLocalDateTime(),
                rs.getBoolean("is_external"),
                rs.getInt("capacity_liters"),
                rs.getBoolean("is_led"),
                rs.getTime("time_on") != null ? rs.getTime("time_on").toLocalTime() : LocalTime.MIDNIGHT,
                rs.getTime("time_off") != null ? rs.getTime("time_off").toLocalTime() : LocalTime.MIDNIGHT,
                rs.getDouble("min_temperature"),
                rs.getDouble("max_temperature"),
                rs.getDouble("current_temperature")
        );
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement ps, Accessory accessory) throws SQLException {
        ps.setString(1, accessory.getModel());
        ps.setString(2, accessory.getSerialNumber());
        ps.setLong(3, accessory.getOwnerId());
        setLong(ps, 4, accessory.getAquariumId());
        ps.setString(5, accessory.getColor());
        ps.setString(6, accessory.getDescription());
        ps.setTimestamp(7, Timestamp.valueOf(accessory.getDateCreated()));
        ps.setString(8, accessory.getAccessoryType());
        
        // Simple null handling - let database handle defaults
        ps.setNull(9, Types.BOOLEAN);
        ps.setNull(10, Types.INTEGER);
        ps.setNull(11, Types.BOOLEAN);
        ps.setNull(12, Types.TIME);
        ps.setNull(13, Types.TIME);
        ps.setNull(14, Types.DOUBLE);
        ps.setNull(15, Types.DOUBLE);
        ps.setNull(16, Types.DOUBLE);
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement ps, Accessory accessory) throws SQLException {
        setInsertParameters(ps, accessory);
        ps.setLong(17, accessory.getId());
    }
    
    // Simple queries - NO business logic
    public List<Accessory> findByOwnerId(Long ownerId) {
        return findByField("owner_id", ownerId);
    }
    
    public List<Accessory> findByAquariumId(Long aquariumId) {
        return findByField("aquarium_id", aquariumId);
    }
}