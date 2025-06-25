package nl.hu.bep.domain.base;

public abstract class AssignableEntity {
    
    protected Long aquariumId;
    
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