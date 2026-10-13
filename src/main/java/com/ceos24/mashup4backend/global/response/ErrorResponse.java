package com.ceos24.mashup4backend.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        List<FieldError> details,
        String traceId
) {

    public static ErrorResponse validation(List<FieldError> details) {
        return new ErrorResponse(List.copyOf(details), null);
    }

    public static ErrorResponse server(String traceId) {
        return new ErrorResponse(null, traceId);
    }

    public record FieldError(
            String field,
            String reason
    ) {
    }
}
