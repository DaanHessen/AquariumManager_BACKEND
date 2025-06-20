package nl.hu.bep.data;

import nl.hu.bep.domain.Inhabitant;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.domain.species.*;
import java.sql.*;
import java.util.List;

/**
 * Repository for Inhabitant entities using pure JDBC operations.
 * Handles polymorphic inheritance with discriminator column.
 */
public class InhabitantRepository extends Repository<Inhabitant, Long> {
    
    @Override
    protected String getTableName() { return "inhabitants"; }
    
    @Override
    protected String getIdColumn() { return "id"; }
    
    @Override
    protected String getInsertSql() {
        return "INSERT INTO inhabitants (species, color, count, is_schooling, water_type, owner_id, name, description, date_created, inhabitant_type, is_aggressive_eater, requires_special_food, is_snail_eater) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String getUpdateSql() {
        return "UPDATE inhabitants SET species = ?, color = ?, count = ?, is_schooling = ?, water_type = ?, name = ?, description = ?, is_aggressive_eater = ?, requires_special_food = ?, is_snail_eater = ? WHERE id = ?";
    }
    
    @Override
    protected Inhabitant mapRow(ResultSet rs) throws SQLException {
        String type = rs.getString("inhabitant_type");
        Long id = rs.getLong("id");
        String species = rs.getString("species");
        String color = rs.getString("color");
        int count = rs.getInt("count");
        boolean isSchooling = rs.getBoolean("is_schooling");
        WaterType waterType = WaterType.valueOf(rs.getString("water_type"));
        Long ownerId = getLong(rs, "owner_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        java.time.LocalDateTime dateCreated = getDateTime(rs, "date_created");
        
        // Type-specific properties
        boolean isAggressiveEater = rs.getBoolean("is_aggressive_eater");
        boolean requiresSpecialFood = rs.getBoolean("requires_special_food");
        boolean isSnailEater = rs.getBoolean("is_snail_eater");
        
        // Create the appropriate subclass based on discriminator
        return switch (type.toLowerCase()) {
            case "fish" -> reconstructFish(id, species, color, count, isSchooling, waterType, ownerId, 
                                          name, description, dateCreated, isAggressiveEater, requiresSpecialFood, isSnailEater);
            case "snail" -> reconstructSnail(id, species, color, count, isSchooling, waterType, ownerId, 
                                           name, description, dateCreated, isSnailEater);
            case "shrimp" -> reconstructShrimp(id, species, color, count, isSchooling, waterType, ownerId, 
                                             name, description, dateCreated);
            case "crayfish" -> reconstructCrayfish(id, species, color, count, isSchooling, waterType, ownerId, 
                                                  name, description, dateCreated);
            case "plant" -> reconstructPlant(id, species, color, count, isSchooling, waterType, ownerId, 
                                           name, description, dateCreated);
            case "coral" -> reconstructCoral(id, species, color, count, isSchooling, waterType, ownerId, 
                                           name, description, dateCreated);
            default -> throw new IllegalArgumentException("Unknown inhabitant type: " + type);
        };
    }
    
    // Reconstruction methods for each type
    private Fish reconstructFish(Long id, String species, String color, int count, boolean isSchooling, 
                               WaterType waterType, Long ownerId, String name, String description, 
                               java.time.LocalDateTime dateCreated, boolean isAggressiveEater, 
                               boolean requiresSpecialFood, boolean isSnailEater) {
        Fish fish = Fish.create(species, color, count, isSchooling, isAggressiveEater, 
                               requiresSpecialFood, waterType, isSnailEater, ownerId, name, description);
        setInhabitantId(fish, id, dateCreated);
        return fish;
    }
    
    private Snail reconstructSnail(Long id, String species, String color, int count, boolean isSchooling, 
                                 WaterType waterType, Long ownerId, String name, String description, 
                                 java.time.LocalDateTime dateCreated, boolean isSnailEater) {
        Snail snail = Snail.create(species, color, count, isSchooling, isSnailEater, 
                                  waterType, ownerId, name, description);
        setInhabitantId(snail, id, dateCreated);
        return snail;
    }
    
    private Shrimp reconstructShrimp(Long id, String species, String color, int count, boolean isSchooling, 
                                   WaterType waterType, Long ownerId, String name, String description, 
                                   java.time.LocalDateTime dateCreated) {
        Shrimp shrimp = Shrimp.create(species, color, count, isSchooling, waterType, ownerId, name, description);
        setInhabitantId(shrimp, id, dateCreated);
        return shrimp;
    }
    
    private Crayfish reconstructCrayfish(Long id, String species, String color, int count, boolean isSchooling, 
                                       WaterType waterType, Long ownerId, String name, String description, 
                                       java.time.LocalDateTime dateCreated) {
        Crayfish crayfish = Crayfish.create(species, color, count, isSchooling, waterType, ownerId, name, description);
        setInhabitantId(crayfish, id, dateCreated);
        return crayfish;
    }
    
    private Plant reconstructPlant(Long id, String species, String color, int count, boolean isSchooling, 
                                 WaterType waterType, Long ownerId, String name, String description, 
                                 java.time.LocalDateTime dateCreated) {
        Plant plant = Plant.create(species, color, count, isSchooling, waterType, ownerId, name, description);
        setInhabitantId(plant, id, dateCreated);
        return plant;
    }
    
    private Coral reconstructCoral(Long id, String species, String color, int count, boolean isSchooling, 
                                 WaterType waterType, Long ownerId, String name, String description, 
                                 java.time.LocalDateTime dateCreated) {
        Coral coral = Coral.create(species, color, count, isSchooling, waterType, ownerId, name, description);
        setInhabitantId(coral, id, dateCreated);
        return coral;
    }
    
    // Helper method to set ID and date created via reflection (since fields are private)
    private void setInhabitantId(Inhabitant inhabitant, Long id, java.time.LocalDateTime dateCreated) {
        try {
            java.lang.reflect.Field idField = Inhabitant.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(inhabitant, id);
            
            java.lang.reflect.Field dateField = Inhabitant.class.getDeclaredField("dateCreated");
            dateField.setAccessible(true);
            dateField.set(inhabitant, dateCreated);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set inhabitant ID and date", e);
        }
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
        
        // Type-specific properties - default to false if not applicable
        boolean isAggressiveEater = inhabitant instanceof Fish ? ((Fish) inhabitant).isAggressiveEater() : false;
        boolean requiresSpecialFood = inhabitant instanceof Fish ? ((Fish) inhabitant).isRequiresSpecialFood() : false;
        boolean isSnailEater = (inhabitant instanceof Fish ? ((Fish) inhabitant).isSnailEater() : 
                               inhabitant instanceof Snail ? ((Snail) inhabitant).isSnailEater() : false);
        
        ps.setBoolean(11, isAggressiveEater);
        ps.setBoolean(12, requiresSpecialFood);
        ps.setBoolean(13, isSnailEater);
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
        
        // Type-specific properties
        boolean isAggressiveEater = inhabitant instanceof Fish ? ((Fish) inhabitant).isAggressiveEater() : false;
        boolean requiresSpecialFood = inhabitant instanceof Fish ? ((Fish) inhabitant).isRequiresSpecialFood() : false;
        boolean isSnailEater = (inhabitant instanceof Fish ? ((Fish) inhabitant).isSnailEater() : 
                               inhabitant instanceof Snail ? ((Snail) inhabitant).isSnailEater() : false);
        
        ps.setBoolean(8, isAggressiveEater);
        ps.setBoolean(9, requiresSpecialFood);
        ps.setBoolean(10, isSnailEater);
        ps.setLong(11, inhabitant.getId());
    }
    
    // Simple query methods
    public List<Inhabitant> findByOwnerId(Long ownerId) {
        return findByField("owner_id", ownerId);
    }
    
    public List<Inhabitant> findByAquariumId(Long aquariumId) {
        return findByField("aquarium_id", aquariumId);
    }
}