package com.plataformas_danos_back.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
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
                .body(Map.of("message", ex.getMessage(), "code", "ZIP_CODE_NOT_FOUND"));
    }

    @ExceptionHandler(ZipCodeServiceUnavailableException.class)
    public ResponseEntity<Map<String, String>> handleZipCodeServiceUnavailable(ZipCodeServiceUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", ex.getMessage(), "code", "ZIP_CODE_SERVICE_UNAVAILABLE"));
    }

    @ExceptionHandler(TariffNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleTariffNotFound(TariffNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage(), "code", "TARIFF_NOT_FOUND"));
    }

    @ExceptionHandler(TariffServiceUnavailableException.class)
    public ResponseEntity<Map<String, String>> handleTariffServiceUnavailable(TariffServiceUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", ex.getMessage(), "code", "TARIFF_SERVICE_UNAVAILABLE"));
    }

    @ExceptionHandler(InvalidZipCodeFormatException.class)
    public ResponseEntity<Map<String, String>> handleInvalidZipCodeFormat(InvalidZipCodeFormatException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage(), "code", "INVALID_ZIP_CODE_FORMAT"));
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

    @ExceptionHandler(ParametroNoDisponibleException.class)
    public ResponseEntity<Map<String, String>> handleParametroNoDisponible(ParametroNoDisponibleException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", ex.getMessage(), "code", "PARAMETRO_NO_DISPONIBLE"));
    }

    @ExceptionHandler(IngestEnProgresoException.class)
    public ResponseEntity<Map<String, String>> handleIngestEnProgreso(IngestEnProgresoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", ex.getMessage(), "code", "INGEST_EN_PROGRESO"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", message, "code", "INVALID_REQUEST"));
    }

    @ExceptionHandler(CotizacionNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCotizacionNotFound(CotizacionNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage(), "code", "COTIZACION_NOT_FOUND"));
    }

    @ExceptionHandler(CotizacionConflictException.class)
    public ResponseEntity<Map<String, String>> handleCotizacionConflict(CotizacionConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", ex.getMessage(), "code", "COTIZACION_VERSION_CONFLICT"));
    }

    @ExceptionHandler(FolioServiceUnavailableException.class)
    public ResponseEntity<Map<String, String>> handleFolioServiceUnavailable(FolioServiceUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", ex.getMessage(), "code", "FOLIO_SERVICE_UNAVAILABLE"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "Acceso denegado: No tiene permisos para esta acción", "code", "ACCESS_DENIED"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthentication(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Token ausente o expirado", "code", "UNAUTHORIZED"));
    }
}
