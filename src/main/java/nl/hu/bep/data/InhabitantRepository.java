package nl.hu.bep.data;

import jakarta.inject.Singleton;
import jakarta.persistence.TypedQuery;
import nl.hu.bep.domain.Inhabitant;
import java.util.List;

@Singleton
public class InhabitantRepository extends Repository<Inhabitant, Long> {

    public InhabitantRepository() {
        super(Inhabitant.class);
    }

    // public List<Inhabitant> findByAquariumOwnerId(Long ownerId) {
    //     return executeWithEntityManager(em -> {
    //         TypedQuery<Inhabitant> query = em.createQuery(
    //             "SELECT i FROM Inhabitant i JOIN i.aquarium a WHERE a.owner.id = :ownerId", 
    //             Inhabitant.class);
    //         query.setParameter("ownerId", ownerId);
    //         return query.getResultList();
    //     });
    // }

    // Find inhabitants directly by ownerId
    public List<Inhabitant> findByOwnerId(Long ownerId) {
        return executeWithEntityManager(em -> {
            TypedQuery<Inhabitant> query = em.createQuery(
                "SELECT i FROM Inhabitant i WHERE i.ownerId = :ownerId", 
                Inhabitant.class);
            query.setParameter("ownerId", ownerId);
            return query.getResultList();
        });
    }
    
    // Keep this method if it's needed elsewhere (e.g., Aquarium detail page)
    public List<Inhabitant> findByAquariumId(Long aquariumId) {
        return executeWithEntityManager(em -> {
            TypedQuery<Inhabitant> query = em.createQuery(
                "SELECT i FROM Inhabitant i WHERE i.aquarium.id = :aquariumId", 
                Inhabitant.class);
            query.setParameter("aquariumId", aquariumId);
            return query.getResultList();
        });
    }
} 