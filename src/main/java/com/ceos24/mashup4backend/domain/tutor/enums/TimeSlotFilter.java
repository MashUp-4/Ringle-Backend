package com.ceos24.mashup4backend.domain.tutor.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TimeSlotFilter {

    TIME_00_03(0, 3),
    TIME_05_07(5, 7),
    TIME_07_09(7, 9),
    TIME_09_12(9, 12),
    TIME_12_15(12, 15),
    TIME_15_17(15, 17),
    TIME_19_21(19, 21),
    TIME_21_24(21, 24);

    private final int startHour;
    private final int endHour;
}