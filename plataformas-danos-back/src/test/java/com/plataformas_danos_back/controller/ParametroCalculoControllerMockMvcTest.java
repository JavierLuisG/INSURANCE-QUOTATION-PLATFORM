package com.plataformas_danos_back.controller;

import com.plataformas_danos_back.exception.GlobalExceptionHandler;
import com.plataformas_danos_back.exception.IngestEnProgresoException;
import com.plataformas_danos_back.exception.ParametroNoDisponibleException;
import com.plataformas_danos_back.model.dto.CatalogoCPZonasResponse;
import com.plataformas_danos_back.model.dto.IngestStatusResponse;
import com.plataformas_danos_back.model.dto.ParametrosStatusResponse;
import com.plataformas_danos_back.model.dto.TarifaCATResponse;
import com.plataformas_danos_back.model.dto.TarifaIncendioResponse;
import com.plataformas_danos_back.service.IngestorParametrosService;
import com.plataformas_danos_back.service.ParametroCalculoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// SPEC-010 — Tests de controlador MockMvc para ParametroCalculoController
@ExtendWith(MockitoExtension.class)
class ParametroCalculoControllerMockMvcTest {

    @Mock
    private ParametroCalculoService parametroCalculoService;

    @Mock
    private IngestorParametrosService ingestorParametrosService;

    @InjectMocks
    private ParametroCalculoController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ─── POST /tarifas-incendio/load ──────────────────────────────────────────

    @Test
    void cargarTarifasIncendio_requestValido_returns202() throws Exception {
        // GIVEN
        IngestStatusResponse respuesta = IngestStatusResponse.builder()
                .requestId("req-001")
                .status("COMPLETADA")
                .mensaje("Carga completada. Registros procesados: 6")
                .timestamp(Instant.now())
                .build();
        when(ingestorParametrosService.ingestarTarifasIncendio(any())).thenReturn(respuesta);

        // WHEN / THEN
        mockMvc.perform(post("/api/v1/parameters/tarifas-incendio/load")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"origenForzado\":\"SIMULACION\"}"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("COMPLETADA"))
                .andExpect(jsonPath("$.requestId").value("req-001"));
    }

    @Test
    void cargarTarifasIncendio_ingestEnProgreso_returns409() throws Exception {
        // GIVEN
        when(ingestorParametrosService.ingestarTarifasIncendio(any()))
                .thenThrow(new IngestEnProgresoException("Ya hay una ingestión de tarifas de incendio en progreso"));

        // WHEN / THEN
        mockMvc.perform(post("/api/v1/parameters/tarifas-incendio/load")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"origenForzado\":\"SIMULACION\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INGEST_EN_PROGRESO"))
                .andExpect(jsonPath("$.message").exists());
    }

    // ─── GET /tarifas-incendio ────────────────────────────────────────────────

    @Test
    void obtenerTarifasIncendio_conDatos_returns200() throws Exception {
        // GIVEN
        TarifaIncendioResponse tarifa = TarifaIncendioResponse.builder()
                .id("uuid-i-001")
                .zonaGeografica("Zona_A")
                .tipoInmueble("Residencial Urbano")
                .tarifaBase(new BigDecimal("0.0050"))
                .fechaVigenciaInicio(LocalDate.of(2026, 1, 1))
                .fechaVigenciaFin(LocalDate.of(2026, 12, 31))
                .origen("SIMULACION")
                .build();
        when(parametroCalculoService.obtenerTarifasIncendioVigentes()).thenReturn(List.of(tarifa));

        // WHEN / THEN
        mockMvc.perform(get("/api/v1/parameters/tarifas-incendio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].zonaGeografica").value("Zona_A"))
                .andExpect(jsonPath("$[0].origen").value("SIMULACION"));
    }

    @Test
    void obtenerTarifasIncendio_parametrosNoDisponibles_returns503() throws Exception {
        // GIVEN
        when(parametroCalculoService.obtenerTarifasIncendioVigentes())
                .thenThrow(new ParametroNoDisponibleException("No hay tarifas de incendio vigentes disponibles"));

        // WHEN / THEN
        mockMvc.perform(get("/api/v1/parameters/tarifas-incendio"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("PARAMETRO_NO_DISPONIBLE"))
                .andExpect(jsonPath("$.message").value("No hay tarifas de incendio vigentes disponibles"));
    }

    // ─── GET /tarifas-cat ─────────────────────────────────────────────────────

    @Test
    void obtenerTarifasCAT_sinFiltro_returns200ConLista() throws Exception {
        // GIVEN
        TarifaCATResponse tarifaA = TarifaCATResponse.builder()
                .id("uuid-cat-a")
                .zonaCAT("ZONA_A")
                .factorCAT(new BigDecimal("1.10"))
                .fechaVigenciaInicio(LocalDate.of(2026, 1, 1))
                .fechaVigenciaFin(LocalDate.of(2026, 12, 31))
                .origen("SIMULACION")
                .build();
        TarifaCATResponse tarifaB = TarifaCATResponse.builder()
                .id("uuid-cat-b")
                .zonaCAT("ZONA_B")
                .factorCAT(new BigDecimal("1.25"))
                .fechaVigenciaInicio(LocalDate.of(2026, 1, 1))
                .fechaVigenciaFin(LocalDate.of(2026, 12, 31))
                .origen("SIMULACION")
                .build();
        when(parametroCalculoService.obtenerTarifasCATVigentes()).thenReturn(List.of(tarifaA, tarifaB));

        // WHEN / THEN
        mockMvc.perform(get("/api/v1/parameters/tarifas-cat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void obtenerTarifasCAT_conFiltroZona_returns200FiltradoPorZona() throws Exception {
        // GIVEN — el servicio retorna ZONA_A y ZONA_B; el controller filtra por ?zona=ZONA_A
        TarifaCATResponse tarifaA = TarifaCATResponse.builder()
                .id("uuid-cat-a")
                .zonaCAT("ZONA_A")
                .factorCAT(new BigDecimal("1.10"))
                .fechaVigenciaInicio(LocalDate.of(2026, 1, 1))
                .fechaVigenciaFin(LocalDate.of(2026, 12, 31))
                .origen("SIMULACION")
                .build();
        TarifaCATResponse tarifaB = TarifaCATResponse.builder()
                .id("uuid-cat-b")
                .zonaCAT("ZONA_B")
                .factorCAT(new BigDecimal("1.25"))
                .fechaVigenciaInicio(LocalDate.of(2026, 1, 1))
                .fechaVigenciaFin(LocalDate.of(2026, 12, 31))
                .origen("SIMULACION")
                .build();
        when(parametroCalculoService.obtenerTarifasCATVigentes()).thenReturn(List.of(tarifaA, tarifaB));

        // WHEN / THEN — solo debe retornar ZONA_A tras aplicar el filtro en el controller
        mockMvc.perform(get("/api/v1/parameters/tarifas-cat").param("zona", "ZONA_A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].zonaCAT").value("ZONA_A"));
    }

    // ─── GET /catalogo-cp-zonas/{codigoPostal} ────────────────────────────────

    @Test
    void obtenerZonaPorCodigoPostal_cpExistente_returns200() throws Exception {
        // GIVEN
        CatalogoCPZonasResponse zona = CatalogoCPZonasResponse.builder()
                .codigoPostal("28001")
                .zonaCAT("ZONA_A")
                .nivelTecnico("ALTO")
                .fechaCarga(Instant.now())
                .build();
        when(parametroCalculoService.obtenerZonaPorCP("28001")).thenReturn(Optional.of(zona));

        // WHEN / THEN
        mockMvc.perform(get("/api/v1/parameters/catalogo-cp-zonas/28001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoPostal").value("28001"))
                .andExpect(jsonPath("$.zonaCAT").value("ZONA_A"))
                .andExpect(jsonPath("$.nivelTecnico").value("ALTO"));
    }

    @Test
    void obtenerZonaPorCodigoPostal_cpNoExistente_returns404() throws Exception {
        // GIVEN
        when(parametroCalculoService.obtenerZonaPorCP("99999")).thenReturn(Optional.empty());

        // WHEN / THEN
        mockMvc.perform(get("/api/v1/parameters/catalogo-cp-zonas/99999"))
                .andExpect(status().isNotFound());
    }

    // ─── GET /status ──────────────────────────────────────────────────────────

    @Test
    void obtenerEstado_returns200ConStatusGlobal() throws Exception {
        // GIVEN
        ParametrosStatusResponse.TarifasStatus statusIncendio = ParametrosStatusResponse.TarifasStatus.builder()
                .disponible(true)
                .cantidad(6L)
                .ultimaActualizacion(Instant.now())
                .build();
        ParametrosStatusResponse.TarifasStatus statusCAT = ParametrosStatusResponse.TarifasStatus.builder()
                .disponible(true)
                .cantidad(4L)
                .ultimaActualizacion(Instant.now())
                .build();
        ParametrosStatusResponse.TarifaFHMStatus statusFHM = ParametrosStatusResponse.TarifaFHMStatus.builder()
                .disponible(true)
                .ultimaActualizacion(Instant.now())
                .build();
        ParametrosStatusResponse.TarifasStatus statusCP = ParametrosStatusResponse.TarifasStatus.builder()
                .disponible(true)
                .cantidad(7L)
                .ultimaActualizacion(Instant.now())
                .build();
        ParametrosStatusResponse estado = ParametrosStatusResponse.builder()
                .tarifasIncendio(statusIncendio)
                .tarifasCAT(statusCAT)
                .tarifasFHM(statusFHM)
                .catalogoCPZonas(statusCP)
                .circuitBreakerStatus("CLOSED")
                .build();
        when(parametroCalculoService.obtenerEstado()).thenReturn(estado);

        // WHEN / THEN
        mockMvc.perform(get("/api/v1/parameters/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.circuitBreakerStatus").value("CLOSED"))
                .andExpect(jsonPath("$.tarifasIncendio.disponible").value(true))
                .andExpect(jsonPath("$.tarifasCAT.disponible").value(true))
                .andExpect(jsonPath("$.tarifasFHM.disponible").value(true))
                .andExpect(jsonPath("$.catalogoCPZonas.disponible").value(true));
    }
}
