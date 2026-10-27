package com.ceos24.mashup4backend.domain.lesson.service;

import com.ceos24.mashup4backend.domain.lesson.entity.LessonStatus;
import com.ceos24.mashup4backend.domain.lesson.repository.LessonRepository;
import com.ceos24.mashup4backend.domain.user.dto.UserClassCountResponse;
import com.ceos24.mashup4backend.domain.user.service.UserService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LessonService {

    private final LessonRepository lessonRepository;
    private final UserService userService;

    public UserClassCountResponse getClassCounts(Long userId) {
        userService.getUser(userId);
        long classCount = lessonRepository.countByUser_IdAndStatusAndSlot_StartAtAfter(userId, LessonStatus.BOOKED,
                LocalDateTime.now(ZoneId.of("Asia/Seoul")));
        return new UserClassCountResponse(classCount);
    }
}
