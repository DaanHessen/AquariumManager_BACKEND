package nl.hu.bep.data;

import jakarta.inject.Singleton;
import jakarta.persistence.TypedQuery;
import nl.hu.bep.domain.Aquarium;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import jakarta.persistence.EntityManager;

@Singleton
public class AquariumRepository extends BaseRepository<Aquarium, Long> {
    
    public AquariumRepository() {
        super(Aquarium.class);
    }
    
    /**
     * Public wrapper for executeInTransaction to be used by service layer
     */
    public <R> R executeInTransaction(Function<EntityManager, R> action) {
        return super.executeInTransaction(action);
    }
    
    /**
     * Public wrapper for executeWithEntityManager to be used by service layer
     */
    public <R> R executeWithEntityManager(Function<EntityManager, R> action) {
        return super.executeWithEntityManager(action);
    }
    
    /**
     * Find aquarium by ID with inhabitants loaded
     * Using the base repository's flexible method
     */
    public Optional<Aquarium> findByIdWithInhabitants(Long id) {
        return findByIdWithRelationships(id, "inhabitants");
    }
    
    /**
     * Find aquarium by ID with all collections loaded
     * Using the base repository's flexible method
     */
    public Optional<Aquarium> findByIdWithAllCollections(Long id) {
        return findByIdWithRelationships(id, "inhabitants", "accessories", "ornaments", "owner", "stateHistory");
    }
    
    /**
     * Find all aquariums with all collections for a specific owner
     * Using the base repository's flexible method
     */
    public List<Aquarium> findByOwnerIdWithCollections(Long ownerId) {
        return findByNestedFieldWithRelationships("owner.id", ownerId, 
            "inhabitants", "accessories", "ornaments", "owner", "stateHistory");
    }
    
    /**
     * Find all aquariums with all collections
     * Custom method since base repository doesn't handle "find all with relationships" 
     */
    public List<Aquarium> findAllWithCollections() {
        return executeWithEntityManager(em -> {
            TypedQuery<Aquarium> query = em.createQuery(
                "SELECT DISTINCT a FROM Aquarium a " +
                "LEFT JOIN FETCH a.inhabitants " +
                "LEFT JOIN FETCH a.accessories " +
                "LEFT JOIN FETCH a.ornaments " +
                "LEFT JOIN FETCH a.owner " +
                "LEFT JOIN FETCH a.stateHistory", Aquarium.class);
            
            return query.getResultList();
        });
    }
    
    // Note: The following methods are now replaced by the generic base repository methods:
    // - findByIdWithAccessories(id) -> findByIdWithRelationships(id, "accessories")
    // - findByIdWithOrnaments(id) -> findByIdWithRelationships(id, "ornaments") 
    // - findByIdWithOwner(id) -> findByIdWithRelationships(id, "owner")
    // - findByIdWithManager(id) -> findByIdWithRelationships(id, "aquariumManager")
} 