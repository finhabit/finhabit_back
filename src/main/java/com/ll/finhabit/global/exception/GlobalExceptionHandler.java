package com.ll.finhabit.global.exception;

import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ResponseStatusException 처리
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
            ResponseStatusException ex, WebRequest request) {

        log.error(
                "ResponseStatusException 발생: status={}, reason={}",
                ex.getStatusCode(),
                ex.getReason(),
                ex);

        ErrorResponse errorResponse =
                ErrorResponse.of(
                        ex.getStatusCode().value(),
                        ex.getStatusCode().toString(),
                        ex.getReason() != null ? ex.getReason() : "오류가 발생했습니다.",
                        extractPath(request));

        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }

    // IllegalStateException 처리 - QuizService, MissionService 등에서 사용
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException ex, WebRequest request) {

        log.error("IllegalStateException 발생: message={}", ex.getMessage(), ex);

        ErrorResponse errorResponse =
                ErrorResponse.of(
                        HttpStatus.BAD_REQUEST.value(),
                        "Bad Request",
                        ex.getMessage(),
                        extractPath(request));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // IllegalArgumentException 처리 - 잘못된 파라미터, 존재하지 않는 ID 등
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        log.error("IllegalArgumentException 발생: message={}", ex.getMessage(), ex);

        ErrorResponse errorResponse =
                ErrorResponse.of(
                        HttpStatus.BAD_REQUEST.value(),
                        "Bad Request",
                        ex.getMessage(),
                        extractPath(request));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Validation 실패 처리 - @Valid, @Validated 어노테이션 사용 시 - LoginRequest, SignupRequest 등의 DTO 검증
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {

        String errorMessage =
                ex.getBindingResult().getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.joining(", "));

        log.warn("Validation 실패: {}", errorMessage);

        ErrorResponse errorResponse =
                ErrorResponse.of(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation Failed",
                        errorMessage,
                        extractPath(request));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // 그 외 모든 예외 처리 (최후의 안전망)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {

        log.error("예상치 못한 예외 발생: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse =
                ErrorResponse.of(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Internal Server Error",
                        "서버 내부 오류가 발생했습니다.  잠시 후 다시 시도해주세요.",
                        extractPath(request));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    // WebRequest에서 실제 요청 경로 추출
    private String extractPath(WebRequest request) {
        String description = request.getDescription(false);
        // "uri=/api/auth/login" 형식에서 경로만 추출
        return description.replace("uri=", "");
    }
}
