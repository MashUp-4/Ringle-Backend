package com.ceos24.mashup4backend.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.ALWAYS)
@Schema(description = "API 공통 응답")
public record ApiResponse<T>(
        @Schema(description = "요청 처리 성공 여부", example = "true")
        boolean success,
        @Schema(description = "처리 결과 코드", example = "SUCCESS")
        String code,
        @Schema(description = "처리 결과 메시지", example = "회원 정보를 조회했습니다.")
        String message,
        @Schema(description = "응답 데이터. 반환할 데이터가 없거나 요청이 실패하면 null")
        T data,
        @Schema(description = "오류 상세 정보. 성공하거나 상세 정보가 없으면 null")
        ErrorResponse error
) {

    public static <T> ApiResponse<T> success(String message, T data) {
        return success("SUCCESS", message, data);
    }

    public static <T> ApiResponse<T> success(String code, String message, T data) {
        return new ApiResponse<>(true, code, message, data, null);
    }

    public static ApiResponse<Void> failure(String code, String message) {
        return failure(code, message, null);
    }

    public static ApiResponse<Void> failure(String code, String message, ErrorResponse error) {
        return new ApiResponse<>(false, code, message, null, error);
    }
}
