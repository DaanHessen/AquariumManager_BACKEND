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

/**
 * Enhanced base repository with flexible query capabilities
 */
public abstract class BaseRepository<T, ID> {
    
    private final Class<T> entityClass;
    
    protected BaseRepository(Class<T> entityClass) {
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
    
    /**
     * Find entity by ID with specified relationships eagerly loaded
     * @param id The entity ID
     * @param relationships Array of relationship names to fetch (e.g., "inhabitants", "accessories")
     * @return Optional entity with loaded relationships
     */
    public Optional<T> findByIdWithRelationships(ID id, String... relationships) {
        return executeWithEntityManager(em -> {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT DISTINCT e FROM ").append(entityClass.getSimpleName()).append(" e ");
            
            for (String relationship : relationships) {
                queryBuilder.append("LEFT JOIN FETCH e.").append(relationship).append(" ");
            }
            
            queryBuilder.append("WHERE e.id = :id");
            
            TypedQuery<T> query = em.createQuery(queryBuilder.toString(), entityClass);
            query.setParameter("id", id);
            
            return query.getResultStream().findFirst();
        });
    }
    
    /**
     * Find entities by a specific field value
     * @param fieldName The field name to search by
     * @param value The value to search for
     * @return List of matching entities
     */
    public List<T> findByField(String fieldName, Object value) {
        return executeWithEntityManager(em -> {
            String queryStr = String.format("SELECT e FROM %s e WHERE e.%s = :value", 
                entityClass.getSimpleName(), fieldName);
            TypedQuery<T> query = em.createQuery(queryStr, entityClass);
            query.setParameter("value", value);
            return query.getResultList();
        });
    }
    
    /**
     * Find entities by a specific field value with relationships loaded
     * @param fieldName The field name to search by
     * @param value The value to search for
     * @param relationships Array of relationship names to fetch
     * @return List of matching entities with loaded relationships
     */
    public List<T> findByFieldWithRelationships(String fieldName, Object value, String... relationships) {
        return executeWithEntityManager(em -> {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT DISTINCT e FROM ").append(entityClass.getSimpleName()).append(" e ");
            
            for (String relationship : relationships) {
                queryBuilder.append("LEFT JOIN FETCH e.").append(relationship).append(" ");
            }
            
            queryBuilder.append("WHERE e.").append(fieldName).append(" = :value");
            
            TypedQuery<T> query = em.createQuery(queryBuilder.toString(), entityClass);
            query.setParameter("value", value);
            
            return query.getResultList();
        });
    }
    
    /**
     * Find entities by nested field (e.g., "owner.id")
     * @param nestedFieldPath The nested field path (e.g., "owner.id")
     * @param value The value to search for
     * @return List of matching entities
     */
    public List<T> findByNestedField(String nestedFieldPath, Object value) {
        return executeWithEntityManager(em -> {
            String queryStr = String.format("SELECT e FROM %s e WHERE e.%s = :value", 
                entityClass.getSimpleName(), nestedFieldPath);
            TypedQuery<T> query = em.createQuery(queryStr, entityClass);
            query.setParameter("value", value);
            return query.getResultList();
        });
    }
    
    /**
     * Find entities by nested field with relationships loaded
     * @param nestedFieldPath The nested field path (e.g., "owner.id")
     * @param value The value to search for
     * @param relationships Array of relationship names to fetch
     * @return List of matching entities with loaded relationships
     */
    public List<T> findByNestedFieldWithRelationships(String nestedFieldPath, Object value, String... relationships) {
        return executeWithEntityManager(em -> {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT DISTINCT e FROM ").append(entityClass.getSimpleName()).append(" e ");
            
            for (String relationship : relationships) {
                queryBuilder.append("LEFT JOIN FETCH e.").append(relationship).append(" ");
            }
            
            queryBuilder.append("WHERE e.").append(nestedFieldPath).append(" = :value");
            
            TypedQuery<T> query = em.createQuery(queryBuilder.toString(), entityClass);
            query.setParameter("value", value);
            
            return query.getResultList();
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
        EntityManager em = null;
        try {
            em = getEntityManager();
            return action.apply(em);
        } catch (PersistenceException e) {
            throw new RepositoryException.ConnectionException(
                "Database error in " + entityClass.getSimpleName() + " repository: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RepositoryException.DatabaseConfigException(
                "Unexpected error in " + entityClass.getSimpleName() + " repository: " + e.getMessage(), e);
        } finally {
            closeEntityManagerSafely(em);
        }
    }

    protected <R> R executeInTransaction(Function<EntityManager, R> action) {
        EntityManager em = null;
        EntityTransaction tx = null;
        
        try {
            em = getEntityManager();
            tx = em.getTransaction();
            tx.begin();
            R result = action.apply(em);
            tx.commit();
            return result;
        } catch (PersistenceException e) {
            rollbackTransactionSafely(tx);
            throw new RepositoryException.ConnectionException(
                "Database error in " + entityClass.getSimpleName() + " repository: " + e.getMessage(), e);
        } catch (Exception e) {
            rollbackTransactionSafely(tx);
            throw new RepositoryException.DatabaseConfigException(
                "Unexpected error in " + entityClass.getSimpleName() + " repository: " + e.getMessage(), e);
        } finally {
            closeEntityManagerSafely(em);
        }
    }
    
    private void rollbackTransactionSafely(EntityTransaction tx) {
        if (tx != null) {
            try {
                if (tx.isActive()) {
                    tx.rollback();
                }
            } catch (Exception e) {
                // Log error but don't throw to avoid suppressing original exception
                System.err.println("Error during transaction rollback: " + e.getMessage());
            }
        }
    }
    
    private void closeEntityManagerSafely(EntityManager em) {
        if (em != null) {
            try {
                // Clear persistence context to help with memory cleanup
                if (em.isOpen()) {
                    em.clear();
                    em.close();
                }
            } catch (Exception e) {
                // Log error but don't throw to avoid suppressing original exception
                System.err.println("Error closing EntityManager: " + e.getMessage());
            }
        }
    }
    
    // Compatibility methods to maintain backward compatibility
    private void rollbackTransaction(EntityTransaction tx) {
        rollbackTransactionSafely(tx);
    }
    
    private void closeEntityManager(EntityManager em) {
        closeEntityManagerSafely(em);
    }
} 