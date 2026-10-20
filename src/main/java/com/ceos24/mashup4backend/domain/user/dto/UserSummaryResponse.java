package com.ceos24.mashup4backend.domain.user.dto;

import com.ceos24.mashup4backend.domain.user.entity.User;

public record UserSummaryResponse(
        Long id,
        String name,
        String timeZone,
        Integer credit20MinCount,
        Integer credit40MinCount,
        Long points,
        Integer unusedCouponCount
) {
    public static UserSummaryResponse from(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getName(),
                user.getTimeZone(),
                user.getCredit20MinCount(),
                user.getCredit40MinCount(),
                user.getPoints(),
                user.getUnusedCouponCount()
        );
    }
}
