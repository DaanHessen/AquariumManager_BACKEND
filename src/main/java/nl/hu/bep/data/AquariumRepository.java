package nl.hu.bep.data;

import jakarta.inject.Singleton;
import jakarta.persistence.TypedQuery;
import nl.hu.bep.domain.Aquarium;

import java.util.List;
import java.util.Optional;

@Singleton
public class AquariumRepository extends Repository<Aquarium, Long> {
    
    public AquariumRepository() {
        super(Aquarium.class);
    }
    
    public Optional<Aquarium> findByIdWithInhabitants(Long id) {
        return executeWithEntityManager(em -> {
            TypedQuery<Aquarium> query = em.createQuery(
                "SELECT a FROM Aquarium a LEFT JOIN FETCH a.inhabitants WHERE a.id = :id", Aquarium.class);
            query.setParameter("id", id);
            
            return query.getResultStream().findFirst();
        });
    }
    
    public Optional<Aquarium> findByIdWithAccessories(Long id) {
        return executeWithEntityManager(em -> {
            TypedQuery<Aquarium> query = em.createQuery(
                "SELECT a FROM Aquarium a LEFT JOIN FETCH a.accessories WHERE a.id = :id", Aquarium.class);
            query.setParameter("id", id);
            
            return query.getResultStream().findFirst();
        });
    }
    
    public Optional<Aquarium> findByIdWithOrnaments(Long id) {
        return executeWithEntityManager(em -> {
            TypedQuery<Aquarium> query = em.createQuery(
                "SELECT a FROM Aquarium a LEFT JOIN FETCH a.ornaments WHERE a.id = :id", Aquarium.class);
            query.setParameter("id", id);
            
            return query.getResultStream().findFirst();
        });
    }
    
    public Optional<Aquarium> findByIdWithOwner(Long id) {
        return executeWithEntityManager(em -> {
            TypedQuery<Aquarium> query = em.createQuery(
                "SELECT a FROM Aquarium a LEFT JOIN FETCH a.owner WHERE a.id = :id", Aquarium.class);
            query.setParameter("id", id);
            
            return query.getResultStream().findFirst();
        });
    }
    
    public Optional<Aquarium> findByIdWithManager(Long id) {
        return executeWithEntityManager(em -> {
            TypedQuery<Aquarium> query = em.createQuery(
                "SELECT a FROM Aquarium a LEFT JOIN FETCH a.aquariumManager WHERE a.id = :id", Aquarium.class);
            query.setParameter("id", id);
            
            return query.getResultStream().findFirst();
        });
    }
    
    public List<Aquarium> findAllWithCollections() {
        return executeWithEntityManager(em -> {
            TypedQuery<Aquarium> query = em.createQuery(
                "SELECT DISTINCT a FROM Aquarium a " +
                "LEFT JOIN FETCH a.inhabitants " +
                "LEFT JOIN FETCH a.accessories " +
                "LEFT JOIN FETCH a.ornaments " +
                "LEFT JOIN FETCH a.owner", Aquarium.class);
            
            return query.getResultList();
        });
    }
    
    public List<Aquarium> findByOwnerIdWithCollections(Long ownerId) {
        return executeWithEntityManager(em -> {
            TypedQuery<Aquarium> query = em.createQuery(
                "SELECT DISTINCT a FROM Aquarium a " +
                "LEFT JOIN FETCH a.inhabitants " +
                "LEFT JOIN FETCH a.accessories " +
                "LEFT JOIN FETCH a.ornaments " +
                "LEFT JOIN FETCH a.owner " +
                "WHERE a.owner.id = :ownerId", Aquarium.class);
            query.setParameter("ownerId", ownerId);
            
            return query.getResultList();
        });
    }
    
    public Optional<Aquarium> findByIdWithAllCollections(Long id) {
        return executeWithEntityManager(em -> {
            TypedQuery<Aquarium> query = em.createQuery(
                "SELECT DISTINCT a FROM Aquarium a " +
                "LEFT JOIN FETCH a.inhabitants " +
                "LEFT JOIN FETCH a.accessories " +
                "LEFT JOIN FETCH a.ornaments " +
                "LEFT JOIN FETCH a.owner " +
                "WHERE a.id = :id", Aquarium.class);
            query.setParameter("id", id);
            
            return query.getResultStream().findFirst();
        });
    }
} 