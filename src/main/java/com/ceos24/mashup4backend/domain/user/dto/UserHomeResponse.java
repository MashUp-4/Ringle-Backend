package com.ceos24.mashup4backend.domain.user.dto;

import com.ceos24.mashup4backend.domain.learning.entity.LearningGoal;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "홈 화면. type에 해당하는 응답 객체만 포함합니다.")
public record UserHomeResponse(
        HomeType type,
        UpcomingClasses upcomingClasses,
        RecommendedTutors recommendedTutors
) {
    public enum HomeType {
        UPCOMING_CLASS,
        RECOMMENDED_TUTORS
    }

    public static UserHomeResponse upcoming(List<UpcomingClass> classes) {
        return new UserHomeResponse(HomeType.UPCOMING_CLASS, new UpcomingClasses(classes.size(), classes), null);
    }

    public static UserHomeResponse recommended(RecommendedTutors recommendedTutors) {
        return new UserHomeResponse(HomeType.RECOMMENDED_TUTORS, null, recommendedTutors);
    }

    public record UpcomingClasses(
            Integer totalCount,
            List<UpcomingClass> classes
    ) {
    }

    public record UpcomingClass(
            Long classId,
            LocalDateTime startsAt,
            Integer durationMinutes,
            String classType,
            List<String> tags,
            ClassTutor tutor
    ) {
    }

    @JsonInclude(JsonInclude.Include.ALWAYS)
    public record ClassTutor(
            Long tutorId,
            String name,
            String imageUrl,
            String major,
            String university
    ) {
    }

    @JsonInclude(JsonInclude.Include.ALWAYS)
    public record RecommendedTutors(
            LearningGoal selectedCategory,
            LearningGoal signupCategory,
            String interestField,
            Boolean hasLessonPass,
            List<RecommendedTutor> tutors
    ) {
    }

    @JsonInclude(JsonInclude.Include.ALWAYS)
    public record RecommendedTutor(
            Long tutorId,
            String name,
            String imageUrl,
            String major,
            String university,
            Double averageRating,
            Long reviewCount,
            String introduction,
            List<String> tags,
            Boolean isMyTutor,
            LocalDateTime earliestAvailableAt
    ) {
    }
}
