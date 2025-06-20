package nl.hu.bep.domain.base;

/**
 * Base class for entities that can be assigned to aquariums.
 * Eliminates duplicate assignToAquarium/removeFromAquarium methods.
 */
public abstract class AssignableEntity {
    
    protected Long aquariumId;
    
    // Protected - can be called by subclasses for domain-level validation
    // This enforces proper encapsulation and allows subclass customization
    protected void assignToAquarium(Long aquariumId) {
        this.aquariumId = aquariumId;
    }

    protected void removeFromAquarium() {
        this.aquariumId = null;
    }
    
    public Long getAquariumId() {
        return aquariumId;
    }
    
    public boolean isAssignedToAquarium() {
        return aquariumId != null;
    }
} 