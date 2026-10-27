package com.ceos24.mashup4backend.domain.tutor.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Accent {
    AMERICAN("미국 영어"),
    BRITISH("영국 영어");

    private final String displayName;
}
