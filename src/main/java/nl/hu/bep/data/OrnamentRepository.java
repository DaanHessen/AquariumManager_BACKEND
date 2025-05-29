package nl.hu.bep.data;

import jakarta.inject.Singleton;
import nl.hu.bep.domain.Ornament;
import java.util.List;

@Singleton
public class OrnamentRepository extends BaseRepository<Ornament, Long> {
    
    public OrnamentRepository() {
        super(Ornament.class);
    }

    /**
     * Find ornaments by aquarium owner ID
     * Using the base repository's flexible method
     */
    public List<Ornament> findByAquariumOwnerId(Long ownerId) {
        return findByNestedField("aquarium.owner.id", ownerId);
    }

    /**
     * Find ornaments directly by ownerId
     * Using the base repository's flexible method
     */
    public List<Ornament> findByOwnerId(Long ownerId) {
        return findByField("ownerId", ownerId);
    }
    
    /**
     * Find ornaments by aquarium ID
     * Using the base repository's flexible method
     */
    public List<Ornament> findByAquariumId(Long aquariumId) {
        return findByNestedField("aquarium.id", aquariumId);
    }
} 