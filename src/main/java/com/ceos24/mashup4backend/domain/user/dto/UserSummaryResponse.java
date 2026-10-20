package com.ceos24.mashup4backend.domain.user.dto;

import com.ceos24.mashup4backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 정보 요약 응답")
public record UserSummaryResponse(
        @Schema(description = "회원 ID", example = "1")
        Long id,
        @Schema(description = "회원 이름", example = "홍길동")
        String name,
        @Schema(description = "회원이 설정한 시간대", example = "Asia/Seoul")
        String timeZone,
        @Schema(description = "보유한 20분 수업권 수", example = "3")
        Integer credit20MinCount,
        @Schema(description = "보유한 40분 수업권 수", example = "2")
        Integer credit40MinCount,
        @Schema(description = "보유 포인트", example = "1000")
        Long points,
        @Schema(description = "미사용 쿠폰 수", example = "1")
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
