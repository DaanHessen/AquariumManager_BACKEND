package nl.hu.bep.data.interfaces;

import nl.hu.bep.domain.Inhabitant;
import java.util.List;

/**
 * Inhabitant repository interface for better testability.
 */
public interface IInhabitantRepository extends IRepository<Inhabitant, Long> {
  
    List<Inhabitant> findByOwnerId(Long ownerId);
    List<Inhabitant> findByAquariumId(Long aquariumId);
}
