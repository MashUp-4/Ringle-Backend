package com.ceos24.mashup4backend.domain.tutor.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 강점
@Getter
@RequiredArgsConstructor
public enum StrengthTag {
    BEGINNER_ENGLISH("기초 영어"),
    BUSINESS_ENGLISH("비즈니스 영어"),
    INTERVIEW("모의 인터뷰"),
    ENGLISH_EXAM("영어 시험"),
    WRITING("영문 첨삭"),
    ADVANCED_DISCUSSION("심화 토론");

    private final String displayName;
}
