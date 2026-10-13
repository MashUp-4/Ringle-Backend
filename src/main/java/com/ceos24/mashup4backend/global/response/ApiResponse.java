package com.ceos24.mashup4backend.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.ALWAYS)
public record ApiResponse<T>(
        boolean success,
        String code,
        String message,
        T data,
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
