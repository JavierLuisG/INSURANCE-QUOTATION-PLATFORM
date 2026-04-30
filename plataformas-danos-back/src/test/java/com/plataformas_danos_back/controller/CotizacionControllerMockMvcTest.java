package com.plataformas_danos_back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plataformas_danos_back.exception.CotizacionConflictException;
import com.plataformas_danos_back.exception.CotizacionNotFoundException;
import com.plataformas_danos_back.exception.GlobalExceptionHandler;
import com.plataformas_danos_back.model.dto.CotizacionRequest;
import com.plataformas_danos_back.model.dto.CotizacionResponse;
import com.plataformas_danos_back.service.CotizacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// SPEC-011 — Tests MockMvc del controlador de cotizaciones (HU-001 a HU-005)
@ExtendWith(MockitoExtension.class)
class CotizacionControllerMockMvcTest {

    @Mock
    private CotizacionService cotizacionService;

    @InjectMocks
    private CotizacionController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        // Limpiar contexto de seguridad entre tests
        SecurityContextHolder.clearContext();
    }

    // ─── POST /api/v1/cotizaciones ────────────────────────────────────────────

    @Test
    void POST_retorna201_conFolioAsignado() throws Exception {
        // GIVEN
        CotizacionResponse respuesta = CotizacionResponse.builder()
                .folio("COT-2026-000001")
                .estadoValidacion("INCOMPLETA")
                .createdAt(Instant.parse("2026-04-29T10:00:00Z"))
                .updatedAt(Instant.parse("2026-04-29T10:00:00Z"))
                .version(0L)
                .build();
        autenticarConRoles("creador_cotizacion");
        when(cotizacionService.iniciar()).thenReturn(respuesta);

        // WHEN / THEN
        mockMvc.perform(post("/api/v1/cotizaciones"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.folio").value("COT-2026-000001"))
                .andExpect(jsonPath("$.estadoValidacion").value("INCOMPLETA"))
                .andExpect(jsonPath("$.version").value(0));
    }

    @Test
    void POST_sinToken_retorna401() throws Exception {
        // GIVEN — SecurityContextHolder vacío: sin autenticación
        // El standaloneSetup no activa el filtro JWT; simulamos que el servicio
        // lanza AuthenticationException al no haber principal en el contexto.
        // En el contexto real Spring Security bloquea antes de llegar al controller.
        // Aquí forzamos el escenario vaciando el contexto y verificando que
        // el servicio no fue llamado; el 401 lo devuelve el advice al recibir
        // org.springframework.security.authentication.InsufficientAuthenticationException.
        // Como el standaloneSetup no ejecuta filtros de seguridad, usamos el handler
        // para simular el comportamiento esperado a través de la excepción correspondiente.
        when(cotizacionService.iniciar())
                .thenThrow(new org.springframework.security.authentication.InsufficientAuthenticationException(
                        "Token ausente o expirado"));

        // WHEN / THEN
        mockMvc.perform(post("/api/v1/cotizaciones"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    // ─── GET /api/v1/cotizaciones/{folio} ─────────────────────────────────────

    @Test
    void GET_folioExistente_retorna200() throws Exception {
        // GIVEN
        String folio = "COT-2026-000001";
        CotizacionResponse respuesta = CotizacionResponse.builder()
                .folio(folio)
                .estadoValidacion("INCOMPLETA")
                .nombreAsegurado("Juan Pérez García")
                .rfcAsegurado("PEPJ800326IG0")
                .createdAt(Instant.parse("2026-04-29T10:00:00Z"))
                .updatedAt(Instant.parse("2026-04-29T10:30:00Z"))
                .version(2L)
                .build();
        autenticarConRoles("agente_ventas");
        when(cotizacionService.obtenerPorFolio(folio)).thenReturn(respuesta);

        // WHEN / THEN
        mockMvc.perform(get("/api/v1/cotizaciones/{folio}", folio))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.folio").value(folio))
                .andExpect(jsonPath("$.nombreAsegurado").value("Juan Pérez García"))
                .andExpect(jsonPath("$.rfcAsegurado").value("PEPJ800326IG0"))
                .andExpect(jsonPath("$.version").value(2));
    }

    @Test
    void GET_folioInexistente_retorna404() throws Exception {
        // GIVEN
        String folioInexistente = "COT-2026-999999";
        autenticarConRoles("agente_ventas");
        when(cotizacionService.obtenerPorFolio(folioInexistente))
                .thenThrow(new CotizacionNotFoundException(
                        "No se encontró la cotización con folio: " + folioInexistente));

        // WHEN / THEN
        mockMvc.perform(get("/api/v1/cotizaciones/{folio}", folioInexistente))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("COTIZACION_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(
                        "No se encontró la cotización con folio: " + folioInexistente));
    }

    // ─── PUT /api/v1/cotizaciones/{folio} ─────────────────────────────────────

    @Test
    void PUT_datosValidos_retorna200() throws Exception {
        // GIVEN
        String folio = "COT-2026-000001";
        CotizacionRequest request = CotizacionRequest.builder()
                .nombreAsegurado("Juan Pérez García")
                .rfcAsegurado("PEPJ800326IG0")
                .fechaInicioVigencia("2026-05-01")
                .fechaFinVigencia("2026-05-31")
                .version(1L)
                .build();
        CotizacionResponse respuesta = CotizacionResponse.builder()
                .folio(folio)
                .estadoValidacion("INCOMPLETA")
                .nombreAsegurado("Juan Pérez García")
                .rfcAsegurado("PEPJ800326IG0")
                .fechaInicioVigencia("2026-05-01")
                .fechaFinVigencia("2026-05-31")
                .createdAt(Instant.parse("2026-04-29T10:00:00Z"))
                .updatedAt(Instant.parse("2026-04-29T10:30:00Z"))
                .version(2L)
                .build();
        autenticarConRoles("agente_ventas");
        when(cotizacionService.actualizar(eq(folio), any(CotizacionRequest.class))).thenReturn(respuesta);

        // WHEN / THEN
        mockMvc.perform(put("/api/v1/cotizaciones/{folio}", folio)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.folio").value(folio))
                .andExpect(jsonPath("$.nombreAsegurado").value("Juan Pérez García"))
                .andExpect(jsonPath("$.version").value(2));
    }

    @Test
    void PUT_rfcInvalido_retorna400() throws Exception {
        // GIVEN
        String folio = "COT-2026-000001";
        CotizacionRequest request = CotizacionRequest.builder()
                .rfcAsegurado("RFCINVALIDO123")
                .version(0L)
                .build();
        autenticarConRoles("agente_ventas");
        when(cotizacionService.actualizar(eq(folio), any(CotizacionRequest.class)))
                .thenThrow(new com.plataformas_danos_back.exception.ValidationException(
                        "El formato del RFC introducido no es válido. Por favor, verifique."));

        // WHEN / THEN
        mockMvc.perform(put("/api/v1/cotizaciones/{folio}", folio)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value(
                        "El formato del RFC introducido no es válido. Por favor, verifique."));
    }

    @Test
    void PUT_conflictoVersion_retorna409() throws Exception {
        // GIVEN
        String folio = "COT-2026-000001";
        CotizacionRequest request = CotizacionRequest.builder()
                .nombreAsegurado("Usuario B")
                .version(1L)
                .build();
        autenticarConRoles("editor_cotizaciones");
        when(cotizacionService.actualizar(eq(folio), any(CotizacionRequest.class)))
                .thenThrow(new CotizacionConflictException(
                        "La cotización fue modificada por otro usuario. Recargue e intente de nuevo."));

        // WHEN / THEN
        mockMvc.perform(put("/api/v1/cotizaciones/{folio}", folio)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("COTIZACION_VERSION_CONFLICT"))
                .andExpect(jsonPath("$.message").value(
                        "La cotización fue modificada por otro usuario. Recargue e intente de nuevo."));
    }

    @Test
    void PUT_sinRolEdicion_retorna403() throws Exception {
        // GIVEN — rol creador_cotizacion NO tiene permiso de PUT (solo agente_ventas,
        // vendedor, administrador_ventas, editor_cotizaciones están autorizados)
        String folio = "COT-2026-000001";
        CotizacionRequest request = CotizacionRequest.builder()
                .nombreAsegurado("Intento No Autorizado")
                .version(0L)
                .build();
        autenticarConRoles("creador_cotizacion");
        when(cotizacionService.actualizar(eq(folio), any(CotizacionRequest.class)))
                .thenThrow(new AccessDeniedException("Acceso denegado"));

        // WHEN / THEN
        mockMvc.perform(put("/api/v1/cotizaciones/{folio}", folio)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Carga roles en el SecurityContextHolder para simular un usuario autenticado
     * sin pasar por el filtro JWT (standaloneSetup no activa filtros de seguridad).
     */
    private void autenticarConRoles(String... roles) {
        var authorities = java.util.Arrays.stream(roles)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
        var auth = new UsernamePasswordAuthenticationToken("usuario-test", null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
