package com.ceos24.mashup4backend.domain.tutor.dto;

import java.util.List;

// 튜터 카드 한 개
public record TutorResponse(
        Long id,
        String name,
        String imageUrl,
        String school,
        String major,
        String primaryKeyword,
        Double averageRating,
        long reviewCount,
        Integer acceptRate,
        List<String> interests,
        boolean isMyTutor
) {
}
