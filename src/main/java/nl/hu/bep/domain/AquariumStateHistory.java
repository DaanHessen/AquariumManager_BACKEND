package nl.hu.bep.domain;

import nl.hu.bep.domain.enums.AquariumState;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "aquarium_state_history")
@Getter
@EqualsAndHashCode(exclude = {"aquarium"})
@ToString(exclude = {"aquarium"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
public class AquariumStateHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aquarium_id", nullable = false)
    private Aquarium aquarium;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private AquariumState state;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "duration_minutes")
    private Long durationMinutes;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Creates a new state history record for when an aquarium enters a new state
     */
    public static AquariumStateHistory create(Aquarium aquarium, AquariumState state) {
        AquariumStateHistory history = new AquariumStateHistory();
        history.aquarium = aquarium;
        history.state = state;
        history.startTime = LocalDateTime.now();
        history.createdAt = LocalDateTime.now();
        return history;
    }
    
    /**
     * Creates a new state history record with frontend-provided duration for the previous state
     * Used when frontend manages timing and sends duration on state change
     */
    public static AquariumStateHistory createWithDuration(Aquarium aquarium, AquariumState state, Long durationMinutes) {
        AquariumStateHistory history = new AquariumStateHistory();
        history.aquarium = aquarium;
        history.state = state;
        history.startTime = LocalDateTime.now();
        history.durationMinutes = durationMinutes;
        history.endTime = history.startTime; // Set end time same as start for completed states
        history.createdAt = LocalDateTime.now();
        return history;
    }
    
    /**
     * Creates a new active state history record (no end time, duration will be calculated by frontend)
     */
    public static AquariumStateHistory createActive(Aquarium aquarium, AquariumState state) {
        AquariumStateHistory history = new AquariumStateHistory();
        history.aquarium = aquarium;
        history.state = state;
        history.startTime = LocalDateTime.now();
        history.createdAt = LocalDateTime.now();
        // No endTime or durationMinutes - this represents the current active state
        return history;
    }
    
    /**
     * Ends the current state history record by setting end time and calculating duration
     */
    public void endState() {
        if (endTime != null) {
            throw new IllegalStateException("State history record has already been ended");
        }
        
        endTime = LocalDateTime.now();
        durationMinutes = ChronoUnit.MINUTES.between(startTime, endTime);
    }
    
    /**
     * Calculates the current duration if the state is still active
     */
    public long getCurrentDurationMinutes() {
        LocalDateTime endTimeToUse = endTime != null ? endTime : LocalDateTime.now();
        return ChronoUnit.MINUTES.between(startTime, endTimeToUse);
    }
    
    /**
     * Checks if this state history record is currently active (no end time)
     */
    public boolean isActive() {
        return endTime == null;
    }
    
    /**
     * Gets the duration in minutes, calculating current duration if still active
     */
    public long getDurationMinutes() {
        return durationMinutes != null ? durationMinutes : getCurrentDurationMinutes();
    }
} 