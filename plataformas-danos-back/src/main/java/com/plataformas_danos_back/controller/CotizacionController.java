package com.plataformas_danos_back.controller;

import com.plataformas_danos_back.model.dto.CotizacionRequest;
import com.plataformas_danos_back.model.dto.CotizacionResponse;
import com.plataformas_danos_back.service.CotizacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cotizaciones")
public class CotizacionController {

    private final CotizacionService cotizacionService;

    /**
     * POST /api/v1/cotizaciones
     * Inicia una nueva cotización con folio asignado automáticamente.
     * Roles: creador_cotizacion, agente_ventas
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('creador_cotizacion', 'agente_ventas')")
    public ResponseEntity<CotizacionResponse> iniciar() {
        log.info("POST /api/v1/cotizaciones — iniciando nueva cotización");
        CotizacionResponse response = cotizacionService.iniciar();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/cotizaciones/{folio}
     * Obtiene los datos de una cotización por folio.
     * Roles: todos los roles autorizados
     */
    @GetMapping("/{folio}")
    @PreAuthorize("hasAnyRole('creador_cotizacion', 'agente_ventas', 'vendedor', 'administrador_ventas', 'editor_cotizaciones')")
    public ResponseEntity<CotizacionResponse> obtenerPorFolio(@PathVariable String folio) {
        log.debug("GET /api/v1/cotizaciones/{} — buscando cotización", folio);
        CotizacionResponse response = cotizacionService.obtenerPorFolio(folio);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/v1/cotizaciones/{folio}
     * Actualiza los datos generales de una cotización existente.
     * Roles: agente_ventas, vendedor, administrador_ventas, editor_cotizaciones
     */
    @PutMapping("/{folio}")
    @PreAuthorize("hasAnyRole('agente_ventas', 'vendedor', 'administrador_ventas', 'editor_cotizaciones')")
    public ResponseEntity<CotizacionResponse> actualizar(
            @PathVariable String folio,
            @Valid @RequestBody CotizacionRequest request) {
        log.debug("PUT /api/v1/cotizaciones/{} — actualizando cotización", folio);
        CotizacionResponse response = cotizacionService.actualizar(folio, request);
        return ResponseEntity.ok(response);
    }
}
