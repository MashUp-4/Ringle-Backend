package com.ceos24.mashup4backend.domain.tutor;

import com.ceos24.mashup4backend.domain.interest.entity.*;
import com.ceos24.mashup4backend.domain.lesson.entity.Lesson;
import com.ceos24.mashup4backend.domain.tutor.entity.*;
import com.ceos24.mashup4backend.domain.tutor.enums.*;
import com.ceos24.mashup4backend.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.*;
import java.time.temporal.TemporalAdjusters;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TutorSearchApiTest {
    @Autowired EntityManager em;
    @Autowired WebApplicationContext context;
    MockMvc mvc;
    Tutor alice;
    Tutor bob;
    Tutor carol;

    @BeforeEach
    void seed() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
        // Controller currently uses user ID 1. Explicit ID avoids AUTO_INCREMENT drift.
        em.createNativeQuery("insert into users (id, name, time_zone, credit_20_min_count, credit_40_min_count, points, unused_coupon_count) values (1, 'Filter Test User', 'Asia/Seoul', 0, 0, 0, 0)").executeUpdate();
        User user = em.find(User.class, 1L);
        User other = User.create("Other Test User", null);
        em.persist(other);
        alice = tutor("Alice", "Computer Science", Gender.FEMALE, FieldOfStudy.ENGINEERING,
                Accent.AMERICAN, ExperienceTag.IT_DEVELOPMENT, StrengthTag.BUSINESS_ENGLISH, true, 4.5f, 5, 90);
        bob = tutor("Bob_100%", "History", Gender.MALE, FieldOfStudy.HUMANITIES,
                Accent.BRITISH, ExperienceTag.EDUCATION, StrengthTag.WRITING, false, 4.4f, 4, 89);
        carol = tutor("Carol", "Economics", Gender.MALE, FieldOfStudy.BUSINESS,
                Accent.AMERICAN, ExperienceTag.FINANCE_ACCOUNTING, StrengthTag.INTERVIEW, false, 4.9f, 20, 100);
        Interest travel = interest(InterestType.TRAVEL);
        Interest music = interest(InterestType.MUSIC);
        em.persist(TutorInterest.create(alice, travel));
        em.persist(TutorInterest.create(alice, music));
        em.persist(TutorInterest.create(bob, music));
        em.persist(TutorFavorite.create(user, alice));
        em.persist(TutorFavorite.create(other, bob));
        LocalDateTime monday = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY)).toLocalDate().atTime(9, 0);
        slot(alice, monday);
        slot(alice, monday.plusHours(1));
        slot(bob, monday.plusDays(1).withHour(19));
        slot(carol, monday).book();
        Lesson completed = Lesson.create(user, slot(alice, monday.minusWeeks(2)));
        completed.complete();
        em.persist(completed);
        TutorAvailableSlot reserved = slot(bob, monday.plusDays(2));
        reserved.book();
        em.persist(Lesson.create(user, reserved));
        Lesson cancelled = Lesson.create(user, slot(bob, monday.minusWeeks(3)));
        cancelled.cancel();
        em.persist(cancelled);
        Lesson othersLesson = Lesson.create(other, slot(carol, monday.minusWeeks(2)));
        othersLesson.complete();
        em.persist(othersLesson);
        em.flush();
        em.clear();
    }

    @Test
    void eachFilterSelectsExpectedTutor() throws Exception {
        String[][] filters = {
                {"search", "  aLiCe  "}, {"search", "computer"},
                {"isMyTutor", "true"}, {"isPreviousTutor", "true"}, {"isPointback", "true"},
                {"strengthTags", "BUSINESS_ENGLISH"}, {"experienceTags", "IT_DEVELOPMENT"},
                {"fieldOfStudy", "ENGINEERING"}, {"personalInterestTags", "TRAVEL"},
                {"genders", "FEMALE"}, {"weekdays", "MON"}, {"timeSlots", "TIME_09_12"}
        };
        for (String[] filter : filters) {
            expect(get("/tutors").param(filter[0], filter[1]), alice);
        }
        expect(get("/tutors").param("accents", "BRITISH"), bob);
        expect(get("/tutors").param("isFeatured", "true"), alice, carol);
    }

    @Test
    void repeatedListParametersUseOrWithoutDuplicateTutors() throws Exception {
        expect(get("/tutors?personalInterestTags=TRAVEL&personalInterestTags=MUSIC"), alice, bob);
        expect(get("/tutors?weekdays=MON&weekdays=TUE&timeSlots=TIME_09_12&timeSlots=TIME_19_21"), alice, bob);
        expect(get("/tutors?genders=MALE&genders=FEMALE"), alice, bob, carol);
    }

    @Test
    void compoundFiltersAndParameterOrderProduceSameResults() throws Exception {
        expect(get("/tutors?isFeatured=true&isMyTutor=true&weekdays=MON&timeSlots=TIME_09_12"), alice);
        expect(get("/tutors?timeSlots=TIME_09_12&weekdays=MON&isMyTutor=true&isFeatured=true"), alice);
        expect(get("/tutors?isFeatured=true&genders=MALE&weekdays=MON"));
        expect(get("/tutors?weekdays=MON&timeSlots=TIME_19_21"));
    }

    @Test
    void absentFalseBlankAndLiteralSearchWork() throws Exception {
        expect(get("/tutors"), alice, bob, carol);
        expect(get("/tutors?isFeatured=false&isMyTutor=false&isPreviousTutor=false&isPointback=false"), alice, bob, carol);
        expect(get("/tutors").param("search", "  "), alice, bob, carol);
        expect(get("/tutors").param("search", "%"), bob);
        expect(get("/tutors").param("search", "_"), bob);
        expect(get("/tutors").param("search", "no-such-tutor"));
    }

    @Test
    void responseIncludesInterestsFavoriteAndCount() throws Exception {
        mvc.perform(get("/tutors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.count").value(3))
                .andExpect(jsonPath("$.data.tutors[0].isMyTutor").value(true))
                .andExpect(jsonPath("$.data.tutors[1].isMyTutor").value(false))
                .andExpect(jsonPath("$.data.tutors[0].interests[0]").value("여행"))
                .andExpect(jsonPath("$.data.tutors[0].interests[1]").value("음악"))
                .andExpect(jsonPath("$.data.tutors[0].primaryKeyword").value("IT_DEVELOPMENT"));
    }

    @Test
    void invalidEnumAndBooleanReturnBadRequest() throws Exception {
        for (String query : new String[]{"weekdays=INVALID", "timeSlots=INVALID", "genders=INVALID", "isFeatured=INVALID"}) {
            mvc.perform(get("/tutors?" + query)).andExpect(status().isBadRequest());
        }
    }

    private void expect(MockHttpServletRequestBuilder request, Tutor... expected) throws Exception {
        var result = mvc.perform(request).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.count").value(expected.length))
                .andExpect(jsonPath("$.data.tutors.length()").value(expected.length));
        for (int i = 0; i < expected.length; i++) {
            result.andExpect(jsonPath("$.data.tutors[" + i + "].id").value(expected[i].getId().intValue()));
        }
    }

    private Tutor tutor(String name, String major, Gender gender, FieldOfStudy field,
                        Accent accent, ExperienceTag experience, StrengthTag strength,
                        boolean pointback, float rating, int reviews, int acceptance) {
        Tutor tutor = Tutor.create(name, "https://example.com/test.png", "Test University", major, gender, "US", null);
        tutor.updateClassification(field, accent, experience);
        tutor.updateStrength(strength);
        tutor.updatePointback(pointback);
        tutor.updateAcceptanceRate(acceptance);
        em.persist(tutor);
        em.flush();
        em.createQuery("update Tutor t set t.rating = :rating, t.reviewCount = :reviews where t.id = :id")
                .setParameter("rating", rating).setParameter("reviews", reviews)
                .setParameter("id", tutor.getId()).executeUpdate();
        em.refresh(tutor);
        return tutor;
    }

    private Interest interest(InterestType type) {
        Interest interest = Interest.create(type);
        em.persist(interest);
        return interest;
    }

    private TutorAvailableSlot slot(Tutor tutor, LocalDateTime at) {
        TutorAvailableSlot slot = TutorAvailableSlot.create(tutor, at, at.plusMinutes(40));
        em.persist(slot);
        return slot;
    }
}
