package nl.hu.bep.data.interfaces;

import nl.hu.bep.domain.Accessory;

import java.util.List;

public interface AccessoryRepository extends Repository<Accessory, Long> {
    
    List<Accessory> findByOwnerId(Long ownerId);
    List<Accessory> findByAquariumId(Long aquariumId);
}
