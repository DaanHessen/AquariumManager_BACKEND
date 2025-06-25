package nl.hu.bep.data.interfaces;

import nl.hu.bep.domain.Inhabitant;
import java.util.List;

public interface InhabitantRepository extends Repository<Inhabitant, Long> {
  
    List<Inhabitant> findByOwnerId(Long ownerId);
    List<Inhabitant> findByAquariumId(Long aquariumId);
}
