package nl.hu.bep.domain.base;

/**
 * Base class for entities that can be assigned to aquariums.
 * Eliminates duplicate assignToAquarium/removeFromAquarium methods.
 */
public abstract class AssignableEntity {
    
    protected Long aquariumId;
    
    // Package-private - only domain layer can call these methods
    // This enforces proper encapsulation and security boundaries
    void assignToAquarium(Long aquariumId) {
        this.aquariumId = aquariumId;
    }

    void removeFromAquarium() {
        this.aquariumId = null;
    }
    
    public Long getAquariumId() {
        return aquariumId;
    }
    
    public boolean isAssignedToAquarium() {
        return aquariumId != null;
    }
} 