package nl.hu.bep.data;

import nl.hu.bep.domain.Inhabitant;
import java.sql.*;
import java.util.List;

public class InhabitantRepository extends Repository<Inhabitant, Long> {

    @Override
    protected String getTableName() {
        return "inhabitants";
    }
    
    @Override
    protected String getIdColumn() {
        return "id";
    }
    
    @Override
    protected String getInsertSql() {
        return "INSERT INTO inhabitants (name, species_type, age, size, color, owner_id, aquarium_id, date_created) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String getUpdateSql() {
        return "UPDATE inhabitants SET name = ?, species_type = ?, age = ?, size = ?, color = ?, owner_id = ?, aquarium_id = ?, date_created = ? WHERE id = ?";
    }
    
    @Override
    protected Inhabitant mapRow(ResultSet rs) throws SQLException {
        // TODO: Implement based on POJO Inhabitant class
        return null;
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement ps, Inhabitant inhabitant) throws SQLException {
        // TODO: Implement based on POJO Inhabitant class
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement ps, Inhabitant inhabitant) throws SQLException {
        setInsertParameters(ps, inhabitant);
        // ps.setLong(9, inhabitant.getId()); // Set ID as last parameter for WHERE clause
    }

    /**
     * Find inhabitants by owner ID
     */
    public List<Inhabitant> findByOwnerId(Long ownerId) {
        return findByField("owner_id", ownerId);
    }
    
    /**
     * Find inhabitants by aquarium ID
     */
    public List<Inhabitant> findByAquariumId(Long aquariumId) {
        return findByField("aquarium_id", aquariumId);
    }
}