package com.plataformas_danos_back.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CacheNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCacheNotFound(CacheNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage(), "code", "CACHE_NOT_FOUND"));
    }

    @ExceptionHandler(CatalogServiceUnavailableException.class)
    public ResponseEntity<Map<String, String>> handleCatalogUnavailable(CatalogServiceUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", ex.getMessage(), "code", "CATALOG_SERVICE_UNAVAILABLE"));
    }

    @ExceptionHandler(ZipCodeNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleZipCodeNotFound(ZipCodeNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage(), "code", "ZIP_NOT_FOUND"));
    }

    @ExceptionHandler(TariffNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleTariffNotFound(TariffNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage(), "code", "TARIFF_NOT_FOUND"));
    }

    @ExceptionHandler(InvalidZipCodeFormatException.class)
    public ResponseEntity<Map<String, String>> handleInvalidZipCodeFormat(InvalidZipCodeFormatException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage(), "code", "INVALID_ZIP_FORMAT"));
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Map<String, String>> handleHttpClientError(HttpClientErrorException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of(
                        "message", "El servicio externo retornó un error: " + ex.getStatusCode(),
                        "code", "EXTERNAL_CLIENT_ERROR"
                ));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(ValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage(), "code", "VALIDATION_ERROR"));
    }

    @ExceptionHandler(InvalidRuleException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRule(InvalidRuleException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage(), "code", "INVALID_RULE"));
    }

    @ExceptionHandler(InconsistencyRecordException.class)
    public ResponseEntity<Map<String, String>> handleInconsistencyRecord(InconsistencyRecordException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", ex.getMessage(), "code", "INCONSISTENCY_RECORD_ERROR"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", message, "code", "INVALID_REQUEST"));
    }
}
