package com.ceos24.mashup4backend.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "예정된 수업 수 응답")
public record UserClassCountResponse(
        @Schema(description = "아직 시작하지 않은 예약 완료 수업 수", example = "3")
        Long classCount
) {
}
