package com.ceos24.mashup4backend.domain.tutor.repository;

import com.ceos24.mashup4backend.domain.interest.entity.InterestType;
import com.ceos24.mashup4backend.domain.lesson.entity.Lesson;
import com.ceos24.mashup4backend.domain.lesson.entity.LessonStatus;
import com.ceos24.mashup4backend.domain.tutor.dto.TutorSearchRequest;
import com.ceos24.mashup4backend.domain.tutor.entity.Tutor;
import com.ceos24.mashup4backend.domain.tutor.entity.TutorFavorite;
import com.ceos24.mashup4backend.domain.tutor.entity.TutorInterest;
import com.ceos24.mashup4backend.domain.tutor.entity.TutorAvailableSlot;
import com.ceos24.mashup4backend.domain.tutor.enums.SlotStatus;
import com.ceos24.mashup4backend.domain.tutor.enums.TimeSlotFilter;
import com.ceos24.mashup4backend.domain.tutor.enums.Weekday;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * 튜터 조회 조건을 만들어주는 클래스
 */
public final class TutorSpecifications {

    private static final float FEATURED_MIN_RATING = 4.5f;
    private static final int FEATURED_MIN_REVIEW_COUNT = 5;
    private static final int FEATURED_MIN_ACCEPTANCE_RATE = 90;

    private TutorSpecifications() {
    }

    // 현재 구현된 검색 조건을 조합
    public static Specification<Tutor> basicFilters(
            Long userId, // 누구의 찜, 수업 기록을 기준으로 검색할지
            TutorSearchRequest request // 사용자가 선택한 검색 조건
    ) {
        // 검색어 조건(search)으로 시작
        Specification<Tutor> spec = searchContains(request.getSearch());

        spec = spec
                .and(inValues("gender", request.getGenders()))
                .and(inValues("accent", request.getAccents()))
                .and(inValues("fieldOfStudy", request.getFieldOfStudy()))
                .and(inValues("strength", request.getStrengthTags()))
                .and(inValues("primaryKeyword", request.getExperienceTags()))
                .and(hasAnyInterest(request.getPersonalInterestTags()))
                .and(hasAvailableSlot(request.getWeekdays(), request.getTimeSlots(),
                        LocalDateTime.now(ZoneId.of("Asia/Seoul"))));

        if (Boolean.TRUE.equals(request.getIsFeatured())) {
            spec = spec.and(isFeatured());
        }

        if (Boolean.TRUE.equals(request.getIsPointback())) {
            spec = spec.and(isPointback());
        }

        if (Boolean.TRUE.equals(request.getIsMyTutor())) {
            spec = spec.and(isFavoriteOf(userId));
        }

        if (Boolean.TRUE.equals(request.getIsPreviousTutor())) {
            spec = spec.and(hasCompletedLessonWith(userId));
        }

        return spec;
    }

    // 이름 또는 상세 전공으로 검색
    public static Specification<Tutor> searchContains(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return cb.conjunction();
            }

            String keyword = escapeLike(
                    search.trim().toLowerCase(Locale.ROOT)
            );

            String pattern = "%" + keyword + "%";

            return cb.or(
                    cb.like(
                            cb.lower(root.<String>get("name")),
                            pattern,
                            '!'
                    ),
                    cb.like(
                            cb.lower(root.<String>get("major")),
                            pattern,
                            '!'
                    )
            );
        };
    }

    // 선택된 enum 값 중 하나에 해당하는지 검사
    private static Specification<Tutor> inValues(
            String field,
            Collection<?> values
    ) {
        return (root, query, cb) -> {
            if (values == null || values.isEmpty()) {
                return cb.conjunction();
            }

            return root.get(field).in(values);
        };
    }

    // 세 기준을 모두 만족해야 추천한다. NULL은 비교 조건을 만족하지 않아 제외된다.
    public static Specification<Tutor> isFeatured() {
        return (root, query, cb) -> cb.and(
                cb.ge(root.<Float>get("rating"), FEATURED_MIN_RATING),
                cb.ge(root.<Integer>get("reviewCount"), FEATURED_MIN_REVIEW_COUNT),
                cb.ge(root.<Integer>get("acceptanceRate"), FEATURED_MIN_ACCEPTANCE_RATE)
        );
    }

    public static Specification<Tutor> isPointback() {
        return (root, query, cb) ->
                cb.isTrue(root.<Boolean>get("pointback"));
    }

    // 선택한 관심사 중 하나라도 연결된 튜터
    public static Specification<Tutor> hasAnyInterest(
            List<InterestType> types
    ) {
        return (root, query, cb) -> {
            if (types == null || types.isEmpty()) {
                return cb.conjunction();
            }

            Subquery<Integer> subquery = query.subquery(Integer.class);
            Root<TutorInterest> interest =
                    subquery.from(TutorInterest.class);

            subquery.select(cb.literal(1));

            subquery.where(
                    cb.equal(
                            interest.get("tutor").get("id"),
                            root.get("id")
                    ),
                    interest.get("interest").get("type").in(types)
            );

            return cb.exists(subquery);
        };
    }

    // 현재 사용자가 찜한 튜터
    public static Specification<Tutor> isFavoriteOf(Long userId) {
        return (root, query, cb) -> {
            Subquery<Integer> subquery = query.subquery(Integer.class);
            Root<TutorFavorite> favorite =
                    subquery.from(TutorFavorite.class);

            subquery.select(cb.literal(1));

            subquery.where(
                    cb.equal(
                            favorite.get("tutor").get("id"),
                            root.get("id")
                    ),
                    cb.equal(
                            favorite.get("user").get("id"),
                            userId
                    )
            );

            return cb.exists(subquery);
        };
    }

    // 현재 사용자와 완료된 수업이 있는 튜터
    public static Specification<Tutor> hasCompletedLessonWith(
            Long userId
    ) {
        return (root, query, cb) -> {
            Subquery<Integer> subquery = query.subquery(Integer.class);
            Root<Lesson> lesson = subquery.from(Lesson.class);

            subquery.select(cb.literal(1));

            subquery.where(
                    cb.equal(
                            lesson.get("slot").get("tutor").get("id"),
                            root.get("id")
                    ),
                    cb.equal(
                            lesson.get("user").get("id"),
                            userId
                    ),
                    cb.equal(
                            lesson.get("status"),
                            LessonStatus.COMPLETED
                    )
            );

            return cb.exists(subquery);
        };
    }

    /**
     * 슬롯은 한국 시간의 LocalDateTime으로 저장한다고 가정한다.
     * 시작 시각 기준 [시작 시간, 종료 시간), 기간 상한 없이 미래 OPEN 슬롯을 검색한다.
     * 요일과 시간대는 반드시 같은 슬롯에서 만족해야 한다.
     */
    public static Specification<Tutor> hasAvailableSlot(
            List<Weekday> weekdays,
            List<TimeSlotFilter> timeSlots,
            LocalDateTime now
    ) {
        return (root, query, cb) -> {
            boolean filterWeekday = weekdays != null && !weekdays.isEmpty();
            boolean filterTime = timeSlots != null && !timeSlots.isEmpty();
            if (!filterWeekday && !filterTime) {
                return cb.conjunction();
            }

            Subquery<Integer> subquery = query.subquery(Integer.class);
            Root<TutorAvailableSlot> slot = subquery.from(TutorAvailableSlot.class);
            List<Predicate> conditions = new ArrayList<>();
            conditions.add(cb.equal(slot.get("tutor").get("id"), root.get("id")));
            conditions.add(cb.equal(slot.get("status"), SlotStatus.OPEN));
            conditions.add(cb.greaterThan(slot.<LocalDateTime>get("startAt"), now));

            if (filterWeekday) {
                // MySQL DAYOFWEEK: SUN=1 ... SAT=7 (Java: MON=1 ... SUN=7)
                List<Integer> days = weekdays.stream()
                        .map(day -> day.getDayOfWeek().getValue() % 7 + 1)
                        .toList();
                conditions.add(cb.function("dayofweek", Integer.class,
                        slot.get("startAt")).in(days));
            }

            if (filterTime) {
                var hour = cb.function("hour", Integer.class, slot.get("startAt"));
                Predicate[] ranges = timeSlots.stream()
                        .map(time -> cb.and(
                                cb.ge(hour, time.getStartHour()),
                                cb.lt(hour, time.getEndHour())))
                        .toArray(Predicate[]::new);
                conditions.add(cb.or(ranges));
            }

            subquery.select(cb.literal(1))
                    .where(conditions.toArray(Predicate[]::new));
            return cb.exists(subquery);
        };
    }

    // 검색어의 %, _를 LIKE 와일드카드가 아닌 문자로 취급
    private static String escapeLike(String value) {
        return value
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");
    }
}
