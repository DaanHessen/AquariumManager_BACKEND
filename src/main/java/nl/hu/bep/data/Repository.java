package nl.hu.bep.data;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import nl.hu.bep.config.DatabaseConfig;
import nl.hu.bep.data.exception.RepositoryException;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class Repository<T, ID> {
    
    private final Class<T> entityClass;
    
    protected Repository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
    
    public Optional<T> findById(ID id) {
        return executeWithEntityManager(em -> {
            T entity = em.find(entityClass, id);
            return Optional.ofNullable(entity);
        });
    }
    
    public List<T> findAll() {
        return executeWithEntityManager(em -> {
            String entityName = entityClass.getSimpleName();
            String query = String.format("SELECT e FROM %s e", entityName);
            TypedQuery<T> typedQuery = em.createQuery(query, entityClass);
            return typedQuery.getResultList();
        });
    }
    
    public T save(T entity) {
        return executeInTransaction(em -> {
            T managedEntity = em.merge(entity);
            return managedEntity;
        });
    }
    
    public void deleteById(ID id) {
        executeInTransaction(em -> {
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
            }
            return null;
        });
    }
    
    public boolean existsById(ID id) {
        return executeWithEntityManager(em -> {
            String queryStr = String.format("SELECT COUNT(e) FROM %s e WHERE e.id = :id", entityClass.getSimpleName());
            Long count = em.createQuery(queryStr, Long.class)
                           .setParameter("id", id)
                           .getSingleResult();
            return count > 0;
        });
    }

    protected EntityManager getEntityManager() {
        return DatabaseConfig.createEntityManager();
    }
    
    protected <R> R executeWithEntityManager(Function<EntityManager, R> action) {
        EntityManager em = getEntityManager();
        try {
            return action.apply(em);
        } catch (PersistenceException e) {
            throw new RepositoryException.ConnectionException(
                "Database error in " + entityClass.getSimpleName() + " repository: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RepositoryException.DatabaseConfigException(
                "Unexpected error in " + entityClass.getSimpleName() + " repository: " + e.getMessage(), e);
        } finally {
            closeEntityManager(em);
        }
    }

    protected <R> R executeInTransaction(Function<EntityManager, R> action) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            R result = action.apply(em);
            tx.commit();
            return result;
        } catch (PersistenceException e) {
            rollbackTransaction(tx);
            throw new RepositoryException.ConnectionException(
                "Database error in " + entityClass.getSimpleName() + " repository: " + e.getMessage(), e);
        } catch (Exception e) {
            rollbackTransaction(tx);
            throw new RepositoryException.DatabaseConfigException(
                "Unexpected error in " + entityClass.getSimpleName() + " repository: " + e.getMessage(), e);
        } finally {
            closeEntityManager(em);
        }
    }
    
    private void rollbackTransaction(EntityTransaction tx) {
        try {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
        } catch (Exception e) {
            System.err.println("Error during transaction rollback: " + e.getMessage());
        }
    }
    
    private void closeEntityManager(EntityManager em) {
        try {
            if (em != null && em.isOpen()) {
                em.close();
            }
        } catch (Exception e) {
            System.err.println("Error closing EntityManager: " + e.getMessage());
        }
    }
} 