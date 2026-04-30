package com.plataformas_danos_back.service;

import com.plataformas_danos_back.client.CotizadorCatalogosClient;
import com.plataformas_danos_back.client.FoliosClient;
import com.plataformas_danos_back.exception.CotizacionConflictException;
import com.plataformas_danos_back.exception.CotizacionNotFoundException;
import com.plataformas_danos_back.exception.FolioServiceUnavailableException;
import com.plataformas_danos_back.exception.ValidationException;
import com.plataformas_danos_back.model.dto.CotizacionRequest;
import com.plataformas_danos_back.model.dto.CotizacionResponse;
import com.plataformas_danos_back.model.entity.Cotizacion;
import com.plataformas_danos_back.repository.CotizacionRepository;
import com.plataformas_danos_back.validator.RfcValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CotizacionServiceImpl implements CotizacionService {

    private static final String ESTADO_INCOMPLETA = "INCOMPLETA";
    private static final String ESTADO_COMPLETA = "COMPLETA";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final CotizacionRepository cotizacionRepository;
    private final FoliosClient foliosClient;
    private final CotizadorCatalogosClient cotizadorCatalogosClient;
    private final RfcValidator rfcValidator;

    @Override
    public CotizacionResponse iniciar() {
        log.info("Iniciando nueva cotización — solicitando folio al servicio externo");
        try {
            String folio = foliosClient.generarFolio();
            log.info("Folio obtenido del servicio de folios: {}", folio);

            Instant ahora = Instant.now();
            Cotizacion cotizacion = Cotizacion.builder()
                    .folio(folio)
                    .estadoValidacion(ESTADO_INCOMPLETA)
                    .createdAt(ahora)
                    .updatedAt(ahora)
                    .build();

            Cotizacion guardada = cotizacionRepository.save(cotizacion);
            log.info("Cotización creada exitosamente: folio={}, id={}", guardada.getFolio(), guardada.getId());
            return toResponse(guardada);
        } catch (FolioServiceUnavailableException e) {
            throw e;
        } catch (Exception ex) {
            log.error("ERROR: No se pudo iniciar la cotización. Detalle: {}", ex.getMessage());
            throw new FolioServiceUnavailableException(
                    "No se pudo iniciar la cotización. Inténtelo de nuevo más tarde.", ex);
        }
    }

    @Override
    public CotizacionResponse obtenerPorFolio(String folio) {
        log.debug("Buscando cotización por folio: {}", folio);
        Cotizacion cotizacion = cotizacionRepository.findByFolio(folio)
                .orElseThrow(() -> {
                    log.warn("Cotización no encontrada: folio={}", folio);
                    return new CotizacionNotFoundException("No se encontró la cotización con folio: " + folio);
                });
        return toResponse(cotizacion);
    }

    @Override
    public CotizacionResponse actualizar(String folio, CotizacionRequest request) {
        log.debug("Actualizando cotización: folio={}", folio);

        Cotizacion cotizacion = cotizacionRepository.findByFolio(folio)
                .orElseThrow(() -> new CotizacionNotFoundException("No se encontró la cotización con folio: " + folio));

        validarVersionOptimista(cotizacion, request.getVersion());
        validarYAplicarCampos(cotizacion, request);

        cotizacion.setUpdatedAt(Instant.now());
        cotizacion.setEstadoValidacion(calcularEstado(cotizacion));

        try {
            Cotizacion actualizada = cotizacionRepository.save(cotizacion);
            log.info("Cotización actualizada exitosamente: folio={}, version={}", actualizada.getFolio(), actualizada.getVersion());
            return toResponse(actualizada);
        } catch (OptimisticLockingFailureException e) {
            log.warn("Conflicto de versión al actualizar cotización: folio={}", folio);
            throw new CotizacionConflictException(
                    "La cotización fue modificada por otro usuario. Recargue e intente de nuevo.");
        }
    }

    // ─── Métodos privados de validación ──────────────────────────────────────

    private void validarVersionOptimista(Cotizacion cotizacion, Long versionRequest) {
        if (versionRequest != null && !versionRequest.equals(cotizacion.getVersion())) {
            log.warn("Conflicto de versión: folio={}, versionActual={}, versionRequest={}",
                    cotizacion.getFolio(), cotizacion.getVersion(), versionRequest);
            throw new CotizacionConflictException(
                    "La cotización fue modificada por otro usuario. Recargue e intente de nuevo.");
        }
    }

    private void validarYAplicarCampos(Cotizacion cotizacion, CotizacionRequest request) {
        // Nombre del asegurado
        if (request.getNombreAsegurado() != null) {
            String nombre = request.getNombreAsegurado().trim();
            if (nombre.isBlank()) {
                throw new ValidationException("El nombre del asegurado no puede estar vacío");
            }
            if (nombre.length() > 255) {
                throw new ValidationException("El nombre del asegurado no puede exceder 255 caracteres");
            }
            if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ\\s'\\-\\.]+$")) {
                throw new ValidationException("El nombre del asegurado contiene caracteres no permitidos");
            }
            cotizacion.setNombreAsegurado(nombre);
        }

        // RFC
        if (request.getRfcAsegurado() != null) {
            String rfc = request.getRfcAsegurado().trim();
            if (!rfcValidator.isValid(rfc)) {
                throw new ValidationException(
                        "El formato del RFC introducido no es válido. Por favor, verifique.");
            }
            cotizacion.setRfcAsegurado(rfc.toUpperCase());
        }

        // Fechas de vigencia
        String fechaInicio = request.getFechaInicioVigencia() != null
                ? request.getFechaInicioVigencia()
                : cotizacion.getFechaInicioVigencia();
        String fechaFin = request.getFechaFinVigencia() != null
                ? request.getFechaFinVigencia()
                : cotizacion.getFechaFinVigencia();

        if (request.getFechaInicioVigencia() != null) {
            validarFormatoFecha(request.getFechaInicioVigencia());
            cotizacion.setFechaInicioVigencia(request.getFechaInicioVigencia());
        }
        if (request.getFechaFinVigencia() != null) {
            validarFormatoFecha(request.getFechaFinVigencia());
            cotizacion.setFechaFinVigencia(request.getFechaFinVigencia());
        }

        if (fechaInicio != null && fechaFin != null) {
            validarRangoFechas(fechaInicio, fechaFin);
        }

        // IDs de catálogos
        if (request.getTipoSeguroId() != null) {
            validarExistenciaEnCatalogo("tipoSeguro", request.getTipoSeguroId());
            cotizacion.setTipoSeguroId(request.getTipoSeguroId());
        }
        if (request.getMonedaId() != null) {
            validarExistenciaEnCatalogo("moneda", request.getMonedaId());
            cotizacion.setMonedaId(request.getMonedaId());
        }
        if (request.getCanalVentaId() != null) {
            validarExistenciaEnCatalogo("canalVenta", request.getCanalVentaId());
            cotizacion.setCanalVentaId(request.getCanalVentaId());
        }
    }

    private void validarFormatoFecha(String fecha) {
        try {
            LocalDate.parse(fecha, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ValidationException(
                    "Formato de fecha inválido. Por favor, use el formato AAAA-MM-DD.");
        }
    }

    private void validarRangoFechas(String fechaInicio, String fechaFin) {
        LocalDate inicio = LocalDate.parse(fechaInicio, DATE_FORMATTER);
        LocalDate fin = LocalDate.parse(fechaFin, DATE_FORMATTER);
        if (fin.isBefore(inicio)) {
            throw new ValidationException(
                    "La fecha de fin de vigencia no puede ser anterior a la fecha de inicio");
        }
    }

    private void validarExistenciaEnCatalogo(String tipoCatalogo, String id) {
        try {
            boolean existe = cotizadorCatalogosClient.existsInCatalog(tipoCatalogo, id);
            if (!existe) {
                throw new ValidationException(
                        "El valor '" + id + "' no existe en el catálogo '" + tipoCatalogo + "'");
            }
        } catch (ValidationException e) {
            throw e;
        } catch (Exception ex) {
            log.error("ERROR: Servicio de catálogos no disponible para validar tipoCatalogo={}, id={}. Detalle: {}",
                    tipoCatalogo, id, ex.getMessage());
            throw new FolioServiceUnavailableException(
                    "No se pudieron cargar las opciones. Intente de nuevo más tarde.", ex);
        }
    }

    private String calcularEstado(Cotizacion cotizacion) {
        boolean completa = cotizacion.getNombreAsegurado() != null
                && !cotizacion.getNombreAsegurado().isBlank()
                && cotizacion.getRfcAsegurado() != null
                && !cotizacion.getRfcAsegurado().isBlank()
                && cotizacion.getTipoSeguroId() != null
                && !cotizacion.getTipoSeguroId().isBlank()
                && cotizacion.getMonedaId() != null
                && !cotizacion.getMonedaId().isBlank()
                && cotizacion.getCanalVentaId() != null
                && !cotizacion.getCanalVentaId().isBlank()
                && cotizacion.getFechaInicioVigencia() != null
                && !cotizacion.getFechaInicioVigencia().isBlank()
                && cotizacion.getFechaFinVigencia() != null
                && !cotizacion.getFechaFinVigencia().isBlank();
        return completa ? ESTADO_COMPLETA : ESTADO_INCOMPLETA;
    }

    // ─── Mapper ──────────────────────────────────────────────────────────────

    private CotizacionResponse toResponse(Cotizacion cotizacion) {
        return CotizacionResponse.builder()
                .folio(cotizacion.getFolio())
                .estadoValidacion(cotizacion.getEstadoValidacion())
                .nombreAsegurado(cotizacion.getNombreAsegurado())
                .rfcAsegurado(cotizacion.getRfcAsegurado())
                .tipoSeguroId(cotizacion.getTipoSeguroId())
                .monedaId(cotizacion.getMonedaId())
                .canalVentaId(cotizacion.getCanalVentaId())
                .fechaInicioVigencia(cotizacion.getFechaInicioVigencia())
                .fechaFinVigencia(cotizacion.getFechaFinVigencia())
                .createdAt(cotizacion.getCreatedAt())
                .updatedAt(cotizacion.getUpdatedAt())
                .version(cotizacion.getVersion())
                .build();
    }
}
