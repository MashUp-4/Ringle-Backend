package com.ceos24.mashup4backend.domain.tutor;

import com.ceos24.mashup4backend.domain.learning.entity.LearningGoal;
import com.ceos24.mashup4backend.domain.tutor.entity.Tutor;
import com.ceos24.mashup4backend.domain.tutor.entity.TutorAvailableSlot;
import com.ceos24.mashup4backend.domain.tutor.enums.*;
import com.ceos24.mashup4backend.domain.tutor.repository.TutorRepository;
import com.ceos24.mashup4backend.domain.tutor.repository.TutorSpecifications;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TutorAvailabilityTest {
    // 2026-10-26 is Monday. Fixed time makes boundary tests independent of execution date.
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 25, 12, 0);
    @Autowired EntityManager em;
    @Autowired TutorRepository tutors;

    @Test
    void bothConditionsMustMatchTheSameSlotAndResultsAreDistinct() {
        Tutor matching = tutor("matching");
        slot(matching, "2026-10-26T09:00");
        slot(matching, "2026-10-26T11:40");
        Tutor split = tutor("split");
        slot(split, "2026-10-26T19:00");
        slot(split, "2026-10-27T09:00");
        assertEquals(List.of(matching.getId()), ids(List.of(Weekday.MON), List.of(TimeSlotFilter.TIME_09_12)));
        assertEquals(1, tutors.count(TutorSpecifications.hasAvailableSlot(
                List.of(Weekday.MON), List.of(TimeSlotFilter.TIME_09_12), NOW)));
    }

    @Test
    void excludesPastNowBookedClosedAndEndBoundary() {
        slot(tutor("past"), "2026-10-19T09:00");
        slot(tutor("now"), "2026-10-25T12:00");
        slot(tutor("booked"), "2026-10-26T09:00").book();
        TutorAvailableSlot closed = slot(tutor("closed"), "2026-10-26T10:00");
        em.flush();
        em.createQuery("update TutorAvailableSlot s set s.status = :status where s.id = :id")
                .setParameter("status", SlotStatus.CLOSED).setParameter("id", closed.getId()).executeUpdate();
        slot(tutor("end"), "2026-10-26T12:00");
        assertTrue(ids(null, List.of(TimeSlotFilter.TIME_09_12)).isEmpty());
        assertFalse(ids(List.of(Weekday.SUN), null).stream().findAny().isPresent());
    }

    @Test
    void supportsSingleFiltersAndMultipleValues() {
        Tutor monday = tutor("monday");
        slot(monday, "2026-10-26T09:00");
        Tutor tuesday = tutor("tuesday");
        slot(tuesday, "2026-10-27T19:30");
        assertEquals(List.of(monday.getId()), ids(List.of(Weekday.MON), null));
        assertEquals(List.of(tuesday.getId()), ids(null, List.of(TimeSlotFilter.TIME_19_21)));
        assertEquals(2, ids(List.of(Weekday.MON, Weekday.TUE),
                List.of(TimeSlotFilter.TIME_09_12, TimeSlotFilter.TIME_19_21)).size());
    }

    @Test
    void sundayAndMidnightBoundariesUseStartTime() {
        Tutor sunday = tutor("sunday");
        slot(sunday, "2026-11-01T23:40");
        Tutor midnight = tutor("midnight");
        slot(midnight, "2026-11-02T00:00");
        assertEquals(List.of(sunday.getId()), ids(List.of(Weekday.SUN), List.of(TimeSlotFilter.TIME_21_24)));
        assertEquals(List.of(midnight.getId()), ids(List.of(Weekday.MON), List.of(TimeSlotFilter.TIME_00_03)));
    }

    @Test
    void omittedOrEmptyFiltersDoNotRequireAnySlot() {
        Tutor noSlot = tutor("no-slot");
        assertEquals(List.of(noSlot.getId()), ids(null, null));
        assertEquals(List.of(noSlot.getId()), ids(List.of(), List.of()));
    }

    private Tutor tutor(String name) {
        Tutor tutor = Tutor.create(name, "https://example.com/a.png", null, null,
                Gender.FEMALE, "US", null, LearningGoal.BUSINESS_ENGLISH);
        em.persist(tutor);
        return tutor;
    }

    private TutorAvailableSlot slot(Tutor tutor, String start) {
        LocalDateTime at = LocalDateTime.parse(start);
        TutorAvailableSlot slot = TutorAvailableSlot.create(tutor, at, at.plusMinutes(40));
        em.persist(slot);
        return slot;
    }

    private List<Long> ids(List<Weekday> days, List<TimeSlotFilter> times) {
        return tutors.findAll(TutorSpecifications.hasAvailableSlot(days, times, NOW))
                .stream().map(Tutor::getId).toList();
    }
}
