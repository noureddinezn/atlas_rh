package com.atlasrh.exception;

import java.time.Instant;
import java.util.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;

@Slf4j @RestControllerAdvice
public class GlobalExceptionHandler {
    public record ApiError(Instant timestamp, int status, String error, String message, String path, Map<String, String> fields) {}
    private ResponseEntity<ApiError> error(HttpStatus status, String message, HttpServletRequest request, Map<String,String> fields) {
        return ResponseEntity.status(status).body(new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, request.getRequestURI(), fields));
    }
    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ApiError> missing(NotFoundException e, HttpServletRequest r) { return error(HttpStatus.NOT_FOUND, e.getMessage(), r, Map.of()); }
    @ExceptionHandler(ConflictException.class)
    ResponseEntity<ApiError> conflict(ConflictException e, HttpServletRequest r) { return error(HttpStatus.CONFLICT, e.getMessage(), r, Map.of()); }
    @ExceptionHandler(BadRequestException.class)
    ResponseEntity<ApiError> bad(BadRequestException e, HttpServletRequest r) { return error(HttpStatus.BAD_REQUEST, e.getMessage(), r, Map.of()); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> validation(MethodArgumentNotValidException e, HttpServletRequest r) {
        Map<String,String> fields = new TreeMap<>();
        e.getBindingResult().getFieldErrors().forEach(f -> fields.put(f.getField(), f.getDefaultMessage()));
        return error(HttpStatus.BAD_REQUEST, "Validation failed", r, fields);
    }
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class, org.springframework.web.bind.MissingServletRequestParameterException.class})
    ResponseEntity<ApiError> malformed(Exception e, HttpServletRequest r) { return error(HttpStatus.BAD_REQUEST, "Invalid request, date, or enum value", r, Map.of()); }
    @ExceptionHandler({DataIntegrityViolationException.class, OptimisticLockingFailureException.class, PessimisticLockingFailureException.class})
    ResponseEntity<ApiError> integrity(Exception e, HttpServletRequest r) { return error(HttpStatus.CONFLICT, "Conflicting data or concurrent modification; reload and retry", r, Map.of()); }
    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ApiError> route(Exception e, HttpServletRequest r) { return error(HttpStatus.NOT_FOUND, "Endpoint not found", r, Map.of()); }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ApiError> method(Exception e, HttpServletRequest r) { return error(HttpStatus.METHOD_NOT_ALLOWED, "Method not supported", r, Map.of()); }
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    ResponseEntity<ApiError> media(Exception e, HttpServletRequest r) { return error(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Use application/json", r, Map.of()); }
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> unexpected(Exception e, HttpServletRequest r) {
        log.error("Unexpected request failure", e);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", r, Map.of());
    }
}
