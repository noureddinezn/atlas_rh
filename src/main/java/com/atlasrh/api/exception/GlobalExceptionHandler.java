package com.atlasrh.api.exception;

import java.time.Instant;
import java.util.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.*;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
@Slf4j @RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<ErrorResponseDTO> error(HttpStatus status, String message, HttpServletRequest r, Map<String,String> fields) {
        return ResponseEntity.status(status).body(new ErrorResponseDTO(Instant.now(), status.value(), status.getReasonPhrase(), message, r.getRequestURI(), fields));
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ErrorResponseDTO> missing(ResourceNotFoundException e, HttpServletRequest r) { return error(HttpStatus.NOT_FOUND,e.getMessage(),r,Map.of()); }
    @ExceptionHandler(BusinessConflictException.class)
    ResponseEntity<ErrorResponseDTO> conflict(BusinessConflictException e, HttpServletRequest r) { return error(HttpStatus.CONFLICT,e.getMessage(),r,Map.of()); }
    @ExceptionHandler(BadRequestException.class)
    ResponseEntity<ErrorResponseDTO> bad(BadRequestException e, HttpServletRequest r) { return error(HttpStatus.BAD_REQUEST,e.getMessage(),r,Map.of()); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponseDTO> validation(MethodArgumentNotValidException e, HttpServletRequest r) {
        Map<String,String> fields = new TreeMap<>();
        e.getBindingResult().getFieldErrors().forEach(f -> fields.put(f.getField(),f.getDefaultMessage()));
        return error(HttpStatus.BAD_REQUEST,"Validation failed",r,fields);
    }
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class, org.springframework.web.bind.MissingServletRequestParameterException.class})
    ResponseEntity<ErrorResponseDTO> malformed(Exception e, HttpServletRequest r) { return error(HttpStatus.BAD_REQUEST,"Invalid JSON, parameter, date or enum value",r,Map.of()); }
    @ExceptionHandler({DataIntegrityViolationException.class, OptimisticLockingFailureException.class, PessimisticLockingFailureException.class})
    ResponseEntity<ErrorResponseDTO> integrity(Exception e, HttpServletRequest r) { return error(HttpStatus.CONFLICT,"Data conflict or concurrent modification",r,Map.of()); }
    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ErrorResponseDTO> route(Exception e, HttpServletRequest r) { return error(HttpStatus.NOT_FOUND,"Endpoint not found",r,Map.of()); }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ErrorResponseDTO> method(Exception e, HttpServletRequest r) { return error(HttpStatus.METHOD_NOT_ALLOWED,"Method not supported",r,Map.of()); }
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    ResponseEntity<ErrorResponseDTO> media(Exception e, HttpServletRequest r) { return error(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"Use application/json",r,Map.of()); }
    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponseDTO> unexpected(Exception e, HttpServletRequest r) {
        log.error("Unexpected request failure",e);
        return error(HttpStatus.INTERNAL_SERVER_ERROR,"Unexpected server error",r,Map.of());
    }
}
