package com.plataformas_danos_back.controller;

import com.plataformas_danos_back.model.dto.CargarTarifasRequest;
import com.plataformas_danos_back.model.dto.CatalogoCPZonasResponse;
import com.plataformas_danos_back.model.dto.IngestStatusResponse;
import com.plataformas_danos_back.model.dto.ParametrosStatusResponse;
import com.plataformas_danos_back.model.dto.TarifaCATResponse;
import com.plataformas_danos_back.model.dto.TarifaFHMResponse;
import com.plataformas_danos_back.model.dto.TarifaIncendioResponse;
import com.plataformas_danos_back.service.IngestorParametrosService;
import com.plataformas_danos_back.service.ParametroCalculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/parameters")
public class ParametroCalculoController {

    private final ParametroCalculoService parametroCalculoService;
    private final IngestorParametrosService ingestorParametrosService;

    // ─── Endpoints de ingestión (POST) ────────────────────────────────────────

    @PostMapping("/tarifas-incendio/load")
    public ResponseEntity<IngestStatusResponse> cargarTarifasIncendio(
            @RequestBody(required = false) CargarTarifasRequest request) {
        IngestStatusResponse response = ingestorParametrosService.ingestarTarifasIncendio(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping("/tarifas-cat/load")
    public ResponseEntity<IngestStatusResponse> cargarTarifasCAT(
            @RequestBody(required = false) CargarTarifasRequest request) {
        IngestStatusResponse response = ingestorParametrosService.ingestarTarifasCAT(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping("/tarifas-fhm/load")
    public ResponseEntity<IngestStatusResponse> cargarTarifasFHM(
            @RequestBody(required = false) CargarTarifasRequest request) {
        IngestStatusResponse response = ingestorParametrosService.ingestarTarifasFHM(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping("/catalogo-cp-zonas/load")
    public ResponseEntity<IngestStatusResponse> cargarCatalogoCPZonas(
            @RequestBody(required = false) CargarTarifasRequest request) {
        IngestStatusResponse response = ingestorParametrosService.ingestarCatalogoCPZonas(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    // ─── Endpoints de consulta (GET) ──────────────────────────────────────────

    @GetMapping("/tarifas-incendio")
    public ResponseEntity<List<TarifaIncendioResponse>> obtenerTarifasIncendio() {
        List<TarifaIncendioResponse> tarifas = parametroCalculoService.obtenerTarifasIncendioVigentes();
        return ResponseEntity.ok(tarifas);
    }

    @GetMapping("/tarifas-cat")
    public ResponseEntity<List<TarifaCATResponse>> obtenerTarifasCAT(
            @RequestParam(required = false) String zona) {
        List<TarifaCATResponse> tarifas = parametroCalculoService.obtenerTarifasCATVigentes();
        if (zona != null && !zona.isBlank()) {
            tarifas = tarifas.stream()
                    .filter(t -> zona.equalsIgnoreCase(t.getZonaCAT()))
                    .toList();
        }
        return ResponseEntity.ok(tarifas);
    }

    @GetMapping("/tarifas-fhm")
    public ResponseEntity<TarifaFHMResponse> obtenerTarifaFHM() {
        Optional<TarifaFHMResponse> tarifa = parametroCalculoService.obtenerTarifaFHMVigente();
        return tarifa.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/catalogo-cp-zonas/{codigoPostal}")
    public ResponseEntity<CatalogoCPZonasResponse> obtenerZonaPorCodigoPostal(
            @PathVariable String codigoPostal) {
        Optional<CatalogoCPZonasResponse> zona = parametroCalculoService.obtenerZonaPorCP(codigoPostal);
        return zona.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status")
    public ResponseEntity<ParametrosStatusResponse> obtenerEstado() {
        ParametrosStatusResponse estado = parametroCalculoService.obtenerEstado();
        return ResponseEntity.ok(estado);
    }
}
