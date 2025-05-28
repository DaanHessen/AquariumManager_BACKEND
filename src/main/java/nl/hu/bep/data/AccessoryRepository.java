package nl.hu.bep.data;

import jakarta.inject.Singleton;
import nl.hu.bep.domain.Accessory;
import java.util.List;
import jakarta.persistence.TypedQuery;

@Singleton
public class AccessoryRepository extends Repository<Accessory, Long> {
    
    public AccessoryRepository() {
        super(Accessory.class);
    }

    public List<Accessory> findByAquariumOwnerId(Long ownerId) {
        return executeWithEntityManager(em -> {
            TypedQuery<Accessory> query = em.createQuery(
                "SELECT acc FROM Accessory acc JOIN acc.aquarium a WHERE a.owner.id = :ownerId", 
                Accessory.class);
            query.setParameter("ownerId", ownerId);
            return query.getResultList();
        });
    }

    // Find accessories directly by ownerId
    public List<Accessory> findByOwnerId(Long ownerId) {
        return executeWithEntityManager(em -> {
            TypedQuery<Accessory> query = em.createQuery(
                "SELECT a FROM Accessory a WHERE a.ownerId = :ownerId", 
                Accessory.class);
            query.setParameter("ownerId", ownerId);
            return query.getResultList();
        });
    }
    
    // Keep this method if it's needed elsewhere
    public List<Accessory> findByAquariumId(Long aquariumId) {
        return executeWithEntityManager(em -> {
            TypedQuery<Accessory> query = em.createQuery(
                "SELECT a FROM Accessory a WHERE a.aquarium.id = :aquariumId", 
                Accessory.class);
            query.setParameter("aquariumId", aquariumId);
            return query.getResultList();
        });
    }
} 