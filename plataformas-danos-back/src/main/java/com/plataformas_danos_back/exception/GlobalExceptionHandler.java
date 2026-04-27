package com.plataformas_danos_back.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

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
}
