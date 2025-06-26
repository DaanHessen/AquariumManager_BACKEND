package nl.hu.bep.data;

import nl.hu.bep.domain.Aquarium;
import nl.hu.bep.domain.enums.AquariumState;
import nl.hu.bep.domain.enums.SubstrateType;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.domain.value.Dimensions;
import nl.hu.bep.data.interfaces.AquariumRepository;

import java.sql.*;
import java.util.List;

public class AquariumRepositoryImpl extends RepositoryImpl<Aquarium, Long> implements AquariumRepository {
    
    @Override
    protected String getTableName() { return "aquariums"; }
    
    @Override
    protected String getIdColumn() { return "id"; }
    
    @Override
    protected String getInsertSql() {
        return "INSERT INTO aquariums (name, length, width, height, substrate, water_type, temperature, state, current_state_start_time, color, description, owner_id, aquarium_manager_id, date_created) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String getUpdateSql() {
        return "UPDATE aquariums SET name = ?, length = ?, width = ?, height = ?, substrate = ?, water_type = ?, temperature = ?, state = ?, current_state_start_time = ?, color = ?, description = ?, owner_id = ?, aquarium_manager_id = ? WHERE id = ?";
    }
    
    @Override
    protected Aquarium mapRow(ResultSet rs) throws SQLException {
        return Aquarium.reconstruct(
                rs.getLong("id"),
                rs.getString("name"),
                new Dimensions(rs.getDouble("length"), rs.getDouble("width"), rs.getDouble("height")),
                SubstrateType.valueOf(rs.getString("substrate")),
                WaterType.valueOf(rs.getString("water_type")),
                rs.getDouble("temperature"),
                AquariumState.valueOf(rs.getString("state")),
                rs.getTimestamp("current_state_start_time").toLocalDateTime(),
                rs.getString("color"),
                rs.getString("description"),
                rs.getTimestamp("date_created").toLocalDateTime(),
                getLong(rs, "aquarium_manager_id"),
                getLong(rs, "owner_id")
        );
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement ps, Aquarium aquarium) throws SQLException {
        ps.setString(1, aquarium.getName());
        ps.setDouble(2, aquarium.getDimensions().getLength());
        ps.setDouble(3, aquarium.getDimensions().getWidth());
        ps.setDouble(4, aquarium.getDimensions().getHeight());
        ps.setString(5, aquarium.getSubstrate().name());
        ps.setString(6, aquarium.getWaterType().name());
        ps.setDouble(7, aquarium.getTemperature());
        ps.setString(8, aquarium.getState().name());
        ps.setTimestamp(9, Timestamp.valueOf(aquarium.getCurrentStateStartTime()));
        ps.setString(10, aquarium.getColor());
        ps.setString(11, aquarium.getDescription());
        setLong(ps, 12, aquarium.getOwnerId());
        setLong(ps, 13, aquarium.getAquariumManagerId());
        ps.setTimestamp(14, Timestamp.valueOf(aquarium.getDateCreated()));
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement ps, Aquarium aquarium) throws SQLException {
        setInsertParameters(ps, aquarium);
        ps.setLong(14, aquarium.getId());
    }
    
    public List<Aquarium> findByOwnerId(Long ownerId) {
        return findByField("owner_id", ownerId);
    }
}