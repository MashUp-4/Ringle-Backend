package com.ceos24.mashup4backend.domain.user.service;

import com.ceos24.mashup4backend.domain.learning.entity.LearningGoal;
import com.ceos24.mashup4backend.domain.lesson.entity.Lesson;
import com.ceos24.mashup4backend.domain.lesson.entity.LessonStatus;
import com.ceos24.mashup4backend.domain.lesson.repository.LessonRepository;
import com.ceos24.mashup4backend.domain.tutor.entity.Tutor;
import com.ceos24.mashup4backend.domain.tutor.entity.TutorAvailableSlot;
import com.ceos24.mashup4backend.domain.tutor.enums.SlotStatus;
import com.ceos24.mashup4backend.domain.tutor.repository.TutorAvailableSlotRepository;
import com.ceos24.mashup4backend.domain.tutor.repository.TutorAvailableSlotRepository.EarliestAvailability;
import com.ceos24.mashup4backend.domain.tutor.repository.TutorFavoriteRepository;
import com.ceos24.mashup4backend.domain.tutor.repository.TutorRepository;
import com.ceos24.mashup4backend.domain.user.dto.UserHomeResponse;
import com.ceos24.mashup4backend.domain.user.dto.UserHomeResponse.ClassTutor;
import com.ceos24.mashup4backend.domain.user.dto.UserHomeResponse.RecommendedTutor;
import com.ceos24.mashup4backend.domain.user.dto.UserHomeResponse.RecommendedTutors;
import com.ceos24.mashup4backend.domain.user.dto.UserHomeResponse.UpcomingClass;
import com.ceos24.mashup4backend.domain.user.entity.User;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserHomeService {

    private final UserService userService;
    private final LessonRepository lessonRepository;
    private final TutorRepository tutorRepository;
    private final TutorFavoriteRepository tutorFavoriteRepository;
    private final TutorAvailableSlotRepository tutorAvailableSlotRepository;

    public UserHomeResponse getHome(Long userId, LearningGoal category) {
        User user = userService.getUser(userId);
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        List<Lesson> upcomingLessons = lessonRepository
                .findByUser_IdAndStatusAndSlot_StartAtAfterOrderBySlot_StartAtAscIdAsc(
                        userId, LessonStatus.BOOKED, now);

        if (!upcomingLessons.isEmpty()) {
            return UserHomeResponse.upcoming(upcomingLessons.stream().map(this::toUpcomingClass).toList());
        }

        LearningGoal selectedCategory = category == null ? user.getLearningGoal() : category;
        List<Tutor> tutors = tutorRepository.findHomeRecommendations(
                selectedCategory, user.getFieldOfStudy(), PageRequest.of(0, 3));
        List<RecommendedTutor> recommendations = toRecommendations(userId, tutors, now);

        return UserHomeResponse.recommended(new RecommendedTutors(
                selectedCategory, user.getLearningGoal(),
                user.getFieldOfStudy() == null ? null : user.getFieldOfStudy().getDisplayName(),
                user.hasLessonPass(), recommendations));
    }

    private UpcomingClass toUpcomingClass(Lesson lesson) {
        TutorAvailableSlot slot = lesson.getSlot();
        Tutor tutor = slot.getTutor();
        return new UpcomingClass(
                lesson.getId(),
                slot.getStartAt(),
                Math.toIntExact(Duration.between(slot.getStartAt(), slot.getEndAt()).toMinutes()),
                tutor.getLearningGoal().getDisplayName(),
                tags(tutor),
                new ClassTutor(tutor.getId(), tutor.getName(), tutor.getProfileImageUrl(),
                        tutor.getMajor(), tutor.getUniversity()));
    }

    private List<RecommendedTutor> toRecommendations(Long userId, List<Tutor> tutors, LocalDateTime now) {
        if (tutors.isEmpty()) {
            return List.of();
        }
        List<Long> tutorIds = tutors.stream().map(Tutor::getId).toList();
        Set<Long> favoriteTutorIds = tutorFavoriteRepository.findFavoriteTutorIds(userId, tutorIds);
        Map<Long, LocalDateTime> availabilityByTutor = tutorAvailableSlotRepository
                .findEarliestAvailability(tutorIds, SlotStatus.OPEN, now).stream()
                .collect(Collectors.toMap(EarliestAvailability::getTutorId,
                        EarliestAvailability::getEarliestAvailableAt));

        return tutors.stream().map(tutor -> new RecommendedTutor(
                tutor.getId(), tutor.getName(), tutor.getProfileImageUrl(), tutor.getMajor(), tutor.getUniversity(),
                tutor.getRating() == null ? null : tutor.getRating().doubleValue(),
                (long) tutor.getReviewCount(), tutor.getIntroduction(), tags(tutor),
                favoriteTutorIds.contains(tutor.getId()), availabilityByTutor.get(tutor.getId())
        )).toList();
    }

    private List<String> tags(Tutor tutor) {
        List<String> tags = new ArrayList<>(2);
        if (tutor.getAccent() != null) {
            tags.add(tutor.getAccent().getDisplayName());
        }
        if (tutor.getStrength() != null) {
            tags.add(tutor.getStrength().getDisplayName());
        }
        return List.copyOf(tags);
    }
}
