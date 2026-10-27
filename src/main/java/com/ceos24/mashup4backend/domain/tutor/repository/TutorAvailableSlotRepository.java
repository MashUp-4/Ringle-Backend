package com.ceos24.mashup4backend.domain.tutor.repository;

import com.ceos24.mashup4backend.domain.tutor.entity.TutorAvailableSlot;
import com.ceos24.mashup4backend.domain.tutor.enums.SlotStatus;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TutorAvailableSlotRepository extends JpaRepository<TutorAvailableSlot, Long> {

    @Query("""
            select s.tutor.id as tutorId, min(s.startAt) as earliestAvailableAt
            from TutorAvailableSlot s
            where s.tutor.id in :tutorIds and s.status = :status and s.startAt > :now
            group by s.tutor.id
            """)
    List<EarliestAvailability> findEarliestAvailability(
            @Param("tutorIds") Collection<Long> tutorIds,
            @Param("status") SlotStatus status,
            @Param("now") LocalDateTime now);

    interface EarliestAvailability {
        Long getTutorId();
        LocalDateTime getEarliestAvailableAt();
    }
}
