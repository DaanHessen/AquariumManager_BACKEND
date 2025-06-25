package nl.hu.bep.data;

import nl.hu.bep.domain.Inhabitant;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.data.interfaces.InhabitantRepository;
import java.sql.*;
import java.util.List;

public class InhabitantRepositoryImpl extends RepositoryImpl<Inhabitant, Long> implements InhabitantRepository {
    
    @Override
    protected String getTableName() { return "inhabitants"; }
    
    @Override
    protected String getIdColumn() { return "id"; }
    
    @Override
    protected String getInsertSql() {
        return "INSERT INTO inhabitants (species, color, count, is_schooling, water_type, owner_id, name, description, date_created, inhabitant_type, is_aggressive_eater, requires_special_food, is_snail_eater, aquarium_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String getUpdateSql() {
        return "UPDATE inhabitants SET species = ?, color = ?, count = ?, is_schooling = ?, water_type = ?, name = ?, description = ?, is_aggressive_eater = ?, requires_special_food = ?, is_snail_eater = ?, aquarium_id = ? WHERE id = ?";
    }
    
    @Override
    protected Inhabitant mapRow(ResultSet rs) throws SQLException {
        return Inhabitant.reconstruct(
                rs.getString("inhabitant_type"),
                rs.getLong("id"),
                rs.getString("species"),
                rs.getString("color"),
                rs.getInt("count"),
                rs.getBoolean("is_schooling"),
                WaterType.valueOf(rs.getString("water_type")),
                getLong(rs, "owner_id"),
                rs.getString("name"),
                rs.getString("description"),
                getDateTime(rs, "date_created"),
                getLong(rs, "aquarium_id"),
                rs.getBoolean("is_aggressive_eater"),
                rs.getBoolean("requires_special_food"),
                rs.getBoolean("is_snail_eater")
        );
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement ps, Inhabitant inhabitant) throws SQLException {
        ps.setString(1, inhabitant.getSpecies());
        ps.setString(2, inhabitant.getColor());
        ps.setInt(3, inhabitant.getCount());
        ps.setBoolean(4, inhabitant.isSchooling());
        ps.setString(5, inhabitant.getWaterType().name());
        ps.setLong(6, inhabitant.getOwnerId());
        ps.setString(7, inhabitant.getName());
        ps.setString(8, inhabitant.getDescription());
        setDateTime(ps, 9, inhabitant.getDateCreated());
        ps.setString(10, inhabitant.getType());
        
        Inhabitant.InhabitantProperties props = inhabitant.getTypeSpecificProperties();
        ps.setBoolean(11, props.isAggressiveEater);
        ps.setBoolean(12, props.requiresSpecialFood);
        ps.setBoolean(13, props.isSnailEater);
        setLong(ps, 14, inhabitant.getAquariumId());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement ps, Inhabitant inhabitant) throws SQLException {
        ps.setString(1, inhabitant.getSpecies());
        ps.setString(2, inhabitant.getColor());
        ps.setInt(3, inhabitant.getCount());
        ps.setBoolean(4, inhabitant.isSchooling());
        ps.setString(5, inhabitant.getWaterType().name());
        ps.setString(6, inhabitant.getName());
        ps.setString(7, inhabitant.getDescription());
        
        Inhabitant.InhabitantProperties props = inhabitant.getTypeSpecificProperties();
        ps.setBoolean(8, props.isAggressiveEater);
        ps.setBoolean(9, props.requiresSpecialFood);
        ps.setBoolean(10, props.isSnailEater);
        setLong(ps, 11, inhabitant.getAquariumId());
        ps.setLong(12, inhabitant.getId());
    }
    
    public List<Inhabitant> findByOwnerId(Long ownerId) {
        return findByField("owner_id", ownerId);
    }
    
    public List<Inhabitant> findByAquariumId(Long aquariumId) {
        return findByField("aquarium_id", aquariumId);
    }
}