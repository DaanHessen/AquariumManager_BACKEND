package nl.hu.bep.data.interfaces;

import nl.hu.bep.domain.Aquarium;
import java.util.List;

public interface AquariumRepository extends Repository<Aquarium, Long> {
  
    List<Aquarium> findByOwnerId(Long ownerId);
}
