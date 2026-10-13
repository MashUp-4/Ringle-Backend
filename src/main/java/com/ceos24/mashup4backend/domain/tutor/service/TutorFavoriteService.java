package com.ceos24.mashup4backend.domain.tutor.service;

import com.ceos24.mashup4backend.domain.tutor.entity.Tutor;
import com.ceos24.mashup4backend.domain.tutor.entity.TutorFavorite;
import com.ceos24.mashup4backend.domain.tutor.repository.TutorFavoriteRepository;
import com.ceos24.mashup4backend.domain.user.entity.User;
import com.ceos24.mashup4backend.domain.user.service.UserService;
import com.ceos24.mashup4backend.global.exception.BusinessException;
import com.ceos24.mashup4backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TutorFavoriteService {

    private final UserService userService;
    private final TutorService tutorService;
    private final TutorFavoriteRepository tutorFavoriteRepository;

    @Transactional
    public void addFavorite(Long userId, Long tutorId) {
        User user = userService.getUser(userId);
        Tutor tutor = tutorService.getTutor(tutorId);

        if (tutorFavoriteRepository.existsByUserIdAndTutorId(userId, tutorId)) {
            throw new BusinessException(ErrorCode.FAVORITE_TUTOR_ALREADY_EXISTS);
        }

        tutorFavoriteRepository.save(TutorFavorite.create(user, tutor));
    }

    @Transactional
    public void removeFavorite(Long userId, Long tutorId) {
        userService.getUser(userId);
        tutorService.getTutor(tutorId);

        TutorFavorite tutorFavorite = tutorFavoriteRepository.findByUserIdAndTutorId(userId, tutorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FAVORITE_TUTOR_NOT_FOUND));

        tutorFavoriteRepository.delete(tutorFavorite);
    }
}
