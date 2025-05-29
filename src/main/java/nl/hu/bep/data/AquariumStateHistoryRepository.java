package nl.hu.bep.data;

import jakarta.inject.Singleton;
import jakarta.persistence.TypedQuery;
import nl.hu.bep.domain.AquariumStateHistory;
import nl.hu.bep.domain.enums.AquariumState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Singleton
public class AquariumStateHistoryRepository extends BaseRepository<AquariumStateHistory, Long> {

    public AquariumStateHistoryRepository() {
        super(AquariumStateHistory.class);
    }

    /**
     * Find all state history for a specific aquarium, ordered by start time
     */
    public List<AquariumStateHistory> findByAquariumIdOrderByStartTime(Long aquariumId) {
        return executeWithEntityManager(em -> {
            TypedQuery<AquariumStateHistory> query = em.createQuery(
                "SELECT h FROM AquariumStateHistory h WHERE h.aquarium.id = :aquariumId ORDER BY h.startTime ASC", 
                AquariumStateHistory.class);
            query.setParameter("aquariumId", aquariumId);
            return query.getResultList();
        });
    }

    /**
     * Find the currently active state history record for an aquarium
     */
    public Optional<AquariumStateHistory> findActiveByAquariumId(Long aquariumId) {
        return executeWithEntityManager(em -> {
            TypedQuery<AquariumStateHistory> query = em.createQuery(
                "SELECT h FROM AquariumStateHistory h WHERE h.aquarium.id = :aquariumId AND h.endTime IS NULL", 
                AquariumStateHistory.class);
            query.setParameter("aquariumId", aquariumId);
            return query.getResultStream().findFirst();
        });
    }

    /**
     * Find state history by aquarium and state type
     */
    public List<AquariumStateHistory> findByAquariumIdAndState(Long aquariumId, AquariumState state) {
        return executeWithEntityManager(em -> {
            TypedQuery<AquariumStateHistory> query = em.createQuery(
                "SELECT h FROM AquariumStateHistory h WHERE h.aquarium.id = :aquariumId AND h.state = :state ORDER BY h.startTime ASC", 
                AquariumStateHistory.class);
            query.setParameter("aquariumId", aquariumId);
            query.setParameter("state", state);
            return query.getResultList();
        });
    }

    /**
     * Find state history within a date range
     */
    public List<AquariumStateHistory> findByAquariumIdAndDateRange(Long aquariumId, LocalDateTime startDate, LocalDateTime endDate) {
        return executeWithEntityManager(em -> {
            TypedQuery<AquariumStateHistory> query = em.createQuery(
                "SELECT h FROM AquariumStateHistory h WHERE h.aquarium.id = :aquariumId " +
                "AND h.startTime >= :startDate AND (h.endTime IS NULL OR h.endTime <= :endDate) " +
                "ORDER BY h.startTime ASC", 
                AquariumStateHistory.class);
            query.setParameter("aquariumId", aquariumId);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList();
        });
    }
} 