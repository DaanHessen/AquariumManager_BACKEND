package nl.hu.bep.data;

import jakarta.inject.Singleton;
import nl.hu.bep.domain.Accessory;
import java.util.List;

@Singleton
public class AccessoryRepository extends BaseRepository<Accessory, Long> {
    
    public AccessoryRepository() {
        super(Accessory.class);
    }

    /**
     * Find accessories by aquarium owner ID
     * Using the base repository's flexible method
     */
    public List<Accessory> findByAquariumOwnerId(Long ownerId) {
        return findByNestedField("aquarium.owner.id", ownerId);
    }

    /**
     * Find accessories directly by ownerId
     * Using the base repository's flexible method
     */
    public List<Accessory> findByOwnerId(Long ownerId) {
        return findByField("ownerId", ownerId);
    }
    
    /**
     * Find accessories by aquarium ID
     * Using the base repository's flexible method
     */
    public List<Accessory> findByAquariumId(Long aquariumId) {
        return findByNestedField("aquarium.id", aquariumId);
    }
} 