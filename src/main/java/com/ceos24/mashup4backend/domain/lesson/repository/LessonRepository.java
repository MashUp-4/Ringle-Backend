package com.ceos24.mashup4backend.domain.lesson.repository;

import com.ceos24.mashup4backend.domain.lesson.entity.Lesson;
import com.ceos24.mashup4backend.domain.lesson.entity.LessonStatus;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    long countByUser_IdAndStatusAndSlot_StartAtAfter(Long userId, LessonStatus status, LocalDateTime now);
}
