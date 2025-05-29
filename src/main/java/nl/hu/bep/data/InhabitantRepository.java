package nl.hu.bep.data;

import jakarta.inject.Singleton;
import nl.hu.bep.domain.Inhabitant;
import java.util.List;

@Singleton
public class InhabitantRepository extends BaseRepository<Inhabitant, Long> {

    public InhabitantRepository() {
        super(Inhabitant.class);
    }

    /**
     * Find inhabitants directly by ownerId
     * Using the base repository's flexible method
     */
    public List<Inhabitant> findByOwnerId(Long ownerId) {
        return findByField("ownerId", ownerId);
    }
    
    /**
     * Find inhabitants by aquarium ID
     * Using the base repository's flexible method
     */
    public List<Inhabitant> findByAquariumId(Long aquariumId) {
        return findByNestedField("aquarium.id", aquariumId);
    }
} 