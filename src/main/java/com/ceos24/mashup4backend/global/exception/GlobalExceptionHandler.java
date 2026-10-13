package com.ceos24.mashup4backend.global.exception;

import com.ceos24.mashup4backend.global.response.ApiResponse;
import com.ceos24.mashup4backend.global.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException exception) {
        ErrorCode code = exception.getErrorCode();
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.failure(code.name(), code.getMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        var details = exception.getBindingResult().getAllErrors().stream()
                .map(error -> new ErrorResponse.FieldError(
                        error instanceof org.springframework.validation.FieldError fieldError
                                ? fieldError.getField() : error.getObjectName(),
                        error.getDefaultMessage()))
                .toList();
        ErrorCode code = ErrorCode.INVALID_PARAMETER;
        return handleExceptionInternal(exception,
                ApiResponse.failure(code.name(), code.getMessage(), ErrorResponse.validation(details)),
                headers,
                status,
                request
        );
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception exception,
            Object body,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        if (!(body instanceof ApiResponse<?>)) {
            if (status.is5xxServerError()) {
                body = serverError(exception);
            } else if (status.value() == HttpStatus.BAD_REQUEST.value()) {
                ErrorCode code = ErrorCode.INVALID_PARAMETER;
                body = ApiResponse.failure(code.name(), code.getMessage());
            } else {
                HttpStatus httpStatus = HttpStatus.resolve(status.value());
                body = ApiResponse.failure(httpStatus == null ? "HTTP_ERROR" : httpStatus.name(),
                        httpStatus == null ? "요청을 처리할 수 없습니다." : httpStatus.getReasonPhrase());
            }
        }
        return super.handleExceptionInternal(exception, body, headers, status, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(serverError(exception));
    }

    private ApiResponse<Void> serverError(Exception exception) {
        String traceId = UUID.randomUUID().toString();
        log.error("서버 오류가 발생했습니다. traceId={}", traceId, exception);
        ErrorCode code = ErrorCode.INTERNAL_SERVER_ERROR;
        return ApiResponse.failure(code.name(), code.getMessage(), ErrorResponse.server(traceId));
    }
}
