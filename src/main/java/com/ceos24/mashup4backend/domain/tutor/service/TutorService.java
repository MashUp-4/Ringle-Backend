package com.ceos24.mashup4backend.domain.tutor.service;

import com.ceos24.mashup4backend.domain.tutor.dto.TutorListResponse;
import com.ceos24.mashup4backend.domain.tutor.dto.TutorResponse;
import com.ceos24.mashup4backend.domain.tutor.dto.TutorSearchRequest;
import com.ceos24.mashup4backend.domain.tutor.entity.Tutor;
import com.ceos24.mashup4backend.domain.tutor.repository.TutorRepository;
import com.ceos24.mashup4backend.domain.tutor.repository.TutorSpecifications;
import com.ceos24.mashup4backend.domain.tutor.repository.TutorInterestRepository;
import com.ceos24.mashup4backend.domain.tutor.repository.TutorFavoriteRepository;
import org.springframework.data.domain.Sort;
import java.util.stream.Collectors;
import com.ceos24.mashup4backend.domain.user.service.UserService;
import com.ceos24.mashup4backend.global.exception.BusinessException;
import com.ceos24.mashup4backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TutorService {

    private final TutorRepository tutorRepository;
    private final UserService userService;
    private final TutorInterestRepository tutorInterestRepository;
    private final TutorFavoriteRepository tutorFavoriteRepository;

    public Tutor getTutor(Long tutorId) {
        return tutorRepository.findById(tutorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TUTOR_NOT_FOUND));
    }

    public TutorListResponse getTutors(
            Long userId,
            TutorSearchRequest request
    ) {
        // 1. 사용자 확인
        userService.getUser(userId);

        // 2. 검색 조건 구성 및 튜터 조회
        List<Tutor> tutors = tutorRepository.findAll(
                TutorSpecifications.basicFilters(userId, request), Sort.by("id"));

        if (tutors.isEmpty()) {
            return new TutorListResponse(0L, List.of());
        }

        // 3. 조회된 튜터 ID 추출
        List<Long> tutorIds = tutors.stream()
                .map(Tutor::getId)
                .toList();

        // 4. 관심사 일괄 조회
        Map<Long, List<String>> interestsByTutorId = tutorInterestRepository
                .findByTutorIdInOrderByIdAsc(tutorIds).stream()
                .collect(Collectors.groupingBy(
                        relation -> relation.getTutor().getId(),
                        Collectors.mapping(relation -> relation.getInterest().getType().getDisplayName(),
                                Collectors.toList())));

        // 5. 현재 사용자가 찜한 튜터 ID 일괄 조회
        Set<Long> favoriteTutorIds = tutorFavoriteRepository.findFavoriteTutorIds(userId, tutorIds);

        // 6. 응답 DTO 조합
        List<TutorResponse> responses = tutors.stream()
                .map(tutor -> new TutorResponse(
                        tutor.getId(),
                        tutor.getName(),
                        tutor.getProfileImageUrl(),
                        tutor.getUniversity(),
                        tutor.getMajor(),
                        tutor.getPrimaryKeyword() == null
                                ? null
                                : tutor.getPrimaryKeyword().name(),
                        tutor.getRating() == null
                                ? null
                                : tutor.getRating().doubleValue(),
                        tutor.getReviewCount(),
                        tutor.getAcceptanceRate(),
                        interestsByTutorId.getOrDefault(tutor.getId(), List.of()),
                        favoriteTutorIds.contains(tutor.getId())
                ))
                .toList();

        // 페이지네이션 없이 전체 결과를 반환하는 경우
        return new TutorListResponse(responses.size(), responses);
    }
}
