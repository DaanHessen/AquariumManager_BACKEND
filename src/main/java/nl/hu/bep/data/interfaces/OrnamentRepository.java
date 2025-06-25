package nl.hu.bep.data.interfaces;

import nl.hu.bep.domain.Ornament;
import java.util.List;

public interface OrnamentRepository extends Repository<Ornament, Long> {
    
    List<Ornament> findByOwnerId(Long ownerId);

    List<Ornament> findByAquariumId(Long aquariumId);
}
