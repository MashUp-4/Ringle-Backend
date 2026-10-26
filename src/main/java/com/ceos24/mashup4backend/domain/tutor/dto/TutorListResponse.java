package com.ceos24.mashup4backend.domain.tutor.dto;

import java.util.List;

// 전체 개수와 튜터 목록
public record TutorListResponse(
        long count,
        List<TutorResponse> tutors
) {
}
