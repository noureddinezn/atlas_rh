package com.atlasrh.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 : la ressource demandee est introuvable.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException exception) {
        ApiError error = new ApiError(404, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 409 : cette action est incompatible avec la situation actuelle.
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleConflict(IllegalStateException exception) {
        ApiError error = new ApiError(409, exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // 400 : une valeur envoyee est incorrecte.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException exception) {
        ApiError error = new ApiError(400, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 400 : retourner le message du premier champ invalide.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
        String message = "Validation failed";
        FieldError fieldError = exception.getBindingResult().getFieldError();

        if (fieldError != null) {
            message = fieldError.getDefaultMessage();
        }
        ApiError error = new ApiError(400, message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 400 : JSON incorrect, parametre manquant ou mauvais type de valeur.
    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ApiError> handleInvalidInput(Exception exception) {
        ApiError error = new ApiError(400, "Invalid JSON or parameter value");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 409 : conflit de donnees ou modifications simultanees.
    @ExceptionHandler({
            DataIntegrityViolationException.class,
            OptimisticLockingFailureException.class,
            PessimisticLockingFailureException.class
    })
    public ResponseEntity<ApiError> handleDatabaseConflict(Exception exception) {
        ApiError error = new ApiError(409, "Data conflict or concurrent modification");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // 404 : cette adresse API est introuvable.
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleEndpointNotFound(NoResourceFoundException exception) {
        ApiError error = new ApiError(404, "Endpoint not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 405 : la methode HTTP utilisee est incorrecte.
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleUnsupportedMethod(HttpRequestMethodNotSupportedException exception) {
        ApiError error = new ApiError(405, "Method not supported");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    // 415 : le format du contenu envoye est incorrect.
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException exception) {
        ApiError error = new ApiError(415, "Use application/json");
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(error);
    }

    // 500 : enregistrer une erreur inattendue et retourner un message general.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedError(Exception exception) {
        log.error("Unexpected request failure", exception);
        ApiError error = new ApiError(500, "Unexpected server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
