package com.ceos24.mashup4backend.domain.learning.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LearningGoal {

    CAREER_INTERVIEW("커리어/인터뷰"),
    BUSINESS_ENGLISH("비즈니스 영어"),
    DAILY_DISCUSSION("일상/토론"),
    STUDY_ABROAD_EXAM("유학/시험");

    private final String displayName;
}
