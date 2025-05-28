package nl.hu.bep.data;

import jakarta.inject.Singleton;
import nl.hu.bep.domain.Ornament;
import java.util.List;
import jakarta.persistence.TypedQuery;

@Singleton
public class OrnamentRepository extends Repository<Ornament, Long> {
    
    public OrnamentRepository() {
        super(Ornament.class);
    }

    public List<Ornament> findByAquariumOwnerId(Long ownerId) {
        return executeWithEntityManager(em -> {
            TypedQuery<Ornament> query = em.createQuery(
                "SELECT o FROM Ornament o JOIN o.aquarium a WHERE a.owner.id = :ownerId", 
                Ornament.class);
            query.setParameter("ownerId", ownerId);
            return query.getResultList();
        });
    }

    // Find ornaments directly by ownerId
    public List<Ornament> findByOwnerId(Long ownerId) {
        return executeWithEntityManager(em -> {
            TypedQuery<Ornament> query = em.createQuery(
                "SELECT o FROM Ornament o WHERE o.ownerId = :ownerId", 
                Ornament.class);
            query.setParameter("ownerId", ownerId);
            return query.getResultList();
        });
    }
    
    // Keep this method if it's needed elsewhere
    public List<Ornament> findByAquariumId(Long aquariumId) {
        return executeWithEntityManager(em -> {
            TypedQuery<Ornament> query = em.createQuery(
                "SELECT o FROM Ornament o WHERE o.aquarium.id = :aquariumId", 
                Ornament.class);
            query.setParameter("aquariumId", aquariumId);
            return query.getResultList();
        });
    }
} 