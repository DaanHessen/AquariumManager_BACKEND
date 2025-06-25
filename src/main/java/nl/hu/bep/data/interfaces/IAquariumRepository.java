package nl.hu.bep.data.interfaces;

import nl.hu.bep.domain.Aquarium;
import java.util.List;

/**
 * Aquarium repository interface for better testability.
 */
public interface IAquariumRepository extends IRepository<Aquarium, Long> {
  
    List<Aquarium> findByOwnerId(Long ownerId);
}
