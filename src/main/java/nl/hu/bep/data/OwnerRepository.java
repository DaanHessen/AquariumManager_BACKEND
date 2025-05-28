package nl.hu.bep.data;

import jakarta.inject.Singleton;
import jakarta.persistence.TypedQuery;
import nl.hu.bep.domain.Owner;

import java.util.Optional;

@Singleton
public class OwnerRepository extends Repository<Owner, Long> {
    
    public OwnerRepository() {
        super(Owner.class);
    }
    
    public Optional<Owner> findByEmail(String email) {
        return executeWithEntityManager(em -> {
            TypedQuery<Owner> query = em.createQuery(
                "SELECT o FROM Owner o WHERE o.email = :email", Owner.class);
            query.setParameter("email", email);
            
            return query.getResultStream().findFirst();
        });
    }
    
    public Optional<Owner> findByIdWithAquariums(Long id) {
        return executeWithEntityManager(em -> {
            TypedQuery<Owner> query = em.createQuery(
                "SELECT o FROM Owner o LEFT JOIN FETCH o.ownedAquariums WHERE o.id = :id", Owner.class);
            query.setParameter("id", id);
            
            return query.getResultStream().findFirst();
        });
    }
} 