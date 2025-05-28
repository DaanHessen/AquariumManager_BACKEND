package nl.hu.bep.data;

import jakarta.inject.Singleton;
import nl.hu.bep.domain.AquariumManager;

@Singleton
public class AquariumManagerRepository extends Repository<AquariumManager, Long> {
    
    public AquariumManagerRepository() {
        super(AquariumManager.class);
    }
} 