package com.ceos24.mashup4backend.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "오류 상세 정보. 값이 없는 필드는 응답에서 제외됩니다.")
public record ErrorResponse(
        @Schema(description = "입력값 검증에 실패한 필드 목록")
        List<FieldError> details,
        @Schema(description = "서버 오류 추적 ID")
        String traceId
) {

    public static ErrorResponse validation(List<FieldError> details) {
        return new ErrorResponse(List.copyOf(details), null);
    }

    public static ErrorResponse server(String traceId) {
        return new ErrorResponse(null, traceId);
    }

    @Schema(description = "필드별 입력값 검증 오류")
    public record FieldError(
            @Schema(description = "검증에 실패한 필드 이름", example = "tutorId")
            String field,
            @Schema(description = "검증 실패 사유", example = "0보다 커야 합니다")
            String reason
    ) {
    }
}
