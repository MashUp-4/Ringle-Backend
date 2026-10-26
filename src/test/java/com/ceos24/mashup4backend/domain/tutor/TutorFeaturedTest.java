package com.ceos24.mashup4backend.domain.tutor;

import com.ceos24.mashup4backend.domain.tutor.dto.TutorSearchRequest;
import com.ceos24.mashup4backend.domain.tutor.entity.Tutor;
import com.ceos24.mashup4backend.domain.tutor.entity.TutorAvailableSlot;
import com.ceos24.mashup4backend.domain.tutor.enums.Gender;
import com.ceos24.mashup4backend.domain.tutor.enums.TimeSlotFilter;
import com.ceos24.mashup4backend.domain.tutor.enums.Weekday;
import com.ceos24.mashup4backend.domain.tutor.repository.TutorRepository;
import com.ceos24.mashup4backend.domain.tutor.repository.TutorSpecifications;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TutorFeaturedTest {
    @Autowired EntityManager em;
    @Autowired TutorRepository tutors;

    @Test
    void includesThresholdsAndExcludesEachInsufficientOrMissingValue() {
        Tutor boundary = tutor(4.5f, 5, 90);
        Tutor higher = tutor(4.9f, 20, 100);
        tutor(4.49f, 5, 90);
        tutor(4.5f, 4, 90);
        tutor(4.5f, 5, 89);
        tutor(null, 5, 90);
        tutor(4.5f, 5, null);

        TutorSearchRequest request = new TutorSearchRequest();
        request.setIsFeatured(true);
        assertEquals(List.of(boundary.getId(), higher.getId()), ids(request));
    }

    @Test
    void falseAndOmittedDoNotFilterOutNonFeaturedTutors() {
        Tutor featured = tutor(4.5f, 5, 90);
        Tutor other = tutor(null, 0, null);
        TutorSearchRequest request = new TutorSearchRequest();
        assertEquals(List.of(featured.getId(), other.getId()), ids(request));
        request.setIsFeatured(false);
        assertEquals(List.of(featured.getId(), other.getId()), ids(request));
    }

    @Test
    void combinesRecommendationAndAvailabilityOnTheSameTutor() {
        LocalDateTime monday = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY)).toLocalDate().atTime(9, 0);
        Tutor matching = tutor(4.5f, 5, 90);
        slot(matching, monday);
        tutor(4.9f, 20, 100); // Recommended, but no available slot.
        slot(tutor(4.4f, 20, 100), monday);
        slot(tutor(4.9f, 20, 100), monday.withHour(19));

        TutorSearchRequest request = new TutorSearchRequest();
        request.setIsFeatured(true);
        request.setWeekdays(List.of(Weekday.MON));
        request.setTimeSlots(List.of(TimeSlotFilter.TIME_09_12));
        assertEquals(List.of(matching.getId()), ids(request));
    }

    private Tutor tutor(Float rating, int reviews, Integer acceptanceRate) {
        Tutor tutor = Tutor.create("Tutor", "https://example.com/a.png", null, null,
                Gender.FEMALE, "US", null);
        tutor.updateAcceptanceRate(acceptanceRate);
        em.persist(tutor);
        em.flush();
        // Seed persisted aggregate values without adding production setters for tests.
        em.createQuery("update Tutor t set t.rating = :rating, t.reviewCount = :reviews where t.id = :id")
                .setParameter("rating", rating)
                .setParameter("reviews", reviews)
                .setParameter("id", tutor.getId())
                .executeUpdate();
        em.refresh(tutor);
        return tutor;
    }

    private void slot(Tutor tutor, LocalDateTime start) {
        em.persist(TutorAvailableSlot.create(tutor, start, start.plusMinutes(40)));
    }

    private List<Long> ids(TutorSearchRequest request) {
        return tutors.findAll(TutorSpecifications.basicFilters(1L, request)).stream()
                .map(Tutor::getId).sorted().toList();
    }
}
