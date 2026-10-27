package com.ceos24.mashup4backend.domain.tutor.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 전공
@Getter
@RequiredArgsConstructor
public enum FieldOfStudy {
    SOCIAL_SCIENCE("사회과학"),
    HUMANITIES("인문학"),
    BUSINESS("경영"),
    NATURAL_SCIENCE("자연과학"),
    ENGINEERING("공학"),
    MEDICINE("의학"),
    LAW("법학"),
    ARTS("예술");

    private final String displayName;
}
