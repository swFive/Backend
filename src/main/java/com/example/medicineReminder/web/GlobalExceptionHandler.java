// src/main/java/com/example/medicineReminder/web/GlobalExceptionHandler.java
package com.example.medicineReminder.web;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    // 400 - Bean Validation: @Valid 본문 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e)
    {
        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", OffsetDateTime.now().toString(),
                "status", 400,
                "error", "Bad Request",
                "message", "validation failed"
        ));
    }

    // 400 - 메서드 파라미터 수준의 제약 위반(@NotNull 등)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException e)
    {
        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", OffsetDateTime.now().toString(),
                "status", 400,
                "error", "Bad Request",
                "message", "validation failed"
        ));
    }

    // 400/422 - 잘못된 요청 형식/타입
    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<Map<String, Object>> handleBadInput(Exception e)
    {
        String cause = Optional.ofNullable(e.getCause()).map(Throwable::getMessage).orElse(e.getMessage());
        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", OffsetDateTime.now().toString(),
                "status", 400,
                "error", "Bad Request",
                "message", "invalid request: " + cause
        ));
    }

    // 400 - 비즈니스 로직에서의 잘못된 인자
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e)
    {
        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", OffsetDateTime.now().toString(),
                "status", 400,
                "error", "Bad Request",
                "message", e.getMessage()
        ));
    }

    // 404 - 조회 대상 없음 표준화
    @ExceptionHandler({NoSuchElementException.class, EntityNotFoundException.class})
    public ResponseEntity<Map<String, Object>> handleNotFound(RuntimeException e)
    {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "timestamp", OffsetDateTime.now().toString(),
                "status", 404,
                "error", "Not Found",
                "message", e.getMessage() != null ? e.getMessage() : "resource not found"
        ));
    }

    // 409 - DB 제약 위반
    @ExceptionHandler({DataIntegrityViolationException.class, SQLIntegrityConstraintViolationException.class})
    public ResponseEntity<Map<String, Object>> handleIntegrity(Exception e)
    {
        String cause = (e instanceof DataIntegrityViolationException div)
                ? Optional.ofNullable(div.getMostSpecificCause()).map(Throwable::getMessage).orElse(div.getMessage())
                : Optional.ofNullable(e.getCause()).map(Throwable::getMessage).orElse(e.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "timestamp", OffsetDateTime.now().toString(),
                "status", 409,
                "error", "Conflict",
                "message", "constraint violation: " + cause
        ));
    }

    // 403 - 접근 거부
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException e)
    {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "timestamp", OffsetDateTime.now().toString(),
                "status", 403,
                "error", "Forbidden",
                "message", "access denied"
        ));
    }

    // 500 - 최종 안전망
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnknown(Exception e)
    {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "timestamp", OffsetDateTime.now().toString(),
                "status", 500,
                "error", "Internal Server Error",
                "message",  "unexpected error"
        ));
    }
}
