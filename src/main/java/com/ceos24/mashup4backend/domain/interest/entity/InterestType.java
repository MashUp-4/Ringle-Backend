package com.ceos24.mashup4backend.domain.interest.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InterestType {

    TRAVEL("여행"),
    MUSIC("음악"),
    SPORTS("스포츠"),
    CULTURE("문화"),
    GAME("게임"),
    TECH_IT("기술/IT"),
    FASHION_BEAUTY("패션/뷰티"),
    SOCIAL_MEDIA("소셜 미디어"),
    CAREER_DEVELOPMENT("커리어 개발"),
    ECONOMY_INVESTMENT("경제/투자");

    private final String displayName;
}
