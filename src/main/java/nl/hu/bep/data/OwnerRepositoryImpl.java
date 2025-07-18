package nl.hu.bep.data;

import nl.hu.bep.domain.Owner;
import nl.hu.bep.domain.enums.Role;
import nl.hu.bep.data.interfaces.OwnerRepository;
import jakarta.inject.Inject;
import nl.hu.bep.config.DatabaseManager;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class OwnerRepositoryImpl extends RepositoryImpl<Owner, Long> implements OwnerRepository {
    
    @Inject
    public OwnerRepositoryImpl(DatabaseManager databaseManager) {
        super(databaseManager);
    }
    
    @Override
    protected String getTableName() { return "owners"; }
    
    @Override
    protected String getIdColumn() { return "id"; }
    
    @Override
    protected String getInsertSql() {
        return "INSERT INTO owners (first_name, last_name, email, password, role, last_login, date_created, aquarium_manager_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String getUpdateSql() {
        return "UPDATE owners SET first_name = ?, last_name = ?, email = ?, password = ?, role = ?, last_login = ?, aquarium_manager_id = ? WHERE id = ?";
    }
    
    @Override
    protected Owner mapRow(ResultSet rs) throws SQLException {
        return Owner.reconstruct(
                rs.getLong("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("password"),
                Role.valueOf(rs.getString("role")),
                getDateTime(rs, "last_login"),
                rs.getTimestamp("date_created").toLocalDateTime(),
                getLong(rs, "aquarium_manager_id"),
                new HashSet<>()
        );
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement ps, Owner owner) throws SQLException {
        ps.setString(1, owner.getFirstName());
        ps.setString(2, owner.getLastName());
        ps.setString(3, owner.getEmail());
        ps.setString(4, owner.getPassword());
        ps.setString(5, owner.getRole().name());
        setDateTime(ps, 6, owner.getLastLogin());
        ps.setTimestamp(7, Timestamp.valueOf(owner.getDateCreated()));
        setLong(ps, 8, owner.getAquariumManagerId());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement ps, Owner owner) throws SQLException {
        ps.setString(1, owner.getFirstName());
        ps.setString(2, owner.getLastName());
        ps.setString(3, owner.getEmail());
        ps.setString(4, owner.getPassword());
        ps.setString(5, owner.getRole().name());
        setDateTime(ps, 6, owner.getLastLogin());
        setLong(ps, 7, owner.getAquariumManagerId());
        ps.setLong(8, owner.getId());
    }
    
    public Optional<Owner> findByEmail(String email) {
        return findByField("email", email).stream().findFirst();
    }
    
    @Override
    public Owner findByUsername(String username) {
        return findByField("username", username).stream().findFirst().orElse(null);
    }
    
    @Override
    public List<Owner> findAllOwners() {
        return findAll();
    }
    
    public List<Owner> findByAquariumManagerId(Long managerId) {
        return findByField("aquarium_manager_id", managerId);
    }
}