package com.plataformas_danos_back.controller;

import com.plataformas_danos_back.exception.CatalogServiceUnavailableException;
import com.plataformas_danos_back.exception.GlobalExceptionHandler;
import com.plataformas_danos_back.model.dto.SubscriberDto;
import com.plataformas_danos_back.service.CatalogsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// SPEC-009 — TC-20, TC-24: verifica códigos HTTP y campos JSON a través de GlobalExceptionHandler
@ExtendWith(MockitoExtension.class)
class CatalogsControllerMockMvcTest {

    @Mock
    private CatalogsService catalogsService;

    @InjectMocks
    private CatalogsController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // TC-20: GET /api/v1/catalogs/subscribers → HTTP 200 con array JSON
    @Test
    void getSubscribers_returns200() throws Exception {
        // GIVEN
        var subscriber = new SubscriberDto("SUB-001", "Empresa ABC S.A. de C.V.", "ABC", true);
        when(catalogsService.getSubscribers()).thenReturn(List.of(subscriber));

        // WHEN / THEN
        mockMvc.perform(get("/api/v1/catalogs/subscribers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("SUB-001"))
                .andExpect(jsonPath("$[0].nombre").value("Empresa ABC S.A. de C.V."));
    }

    // TC-24: service unavailable → HTTP 503 + code=CATALOG_SERVICE_UNAVAILABLE
    @Test
    void getSubscribers_serviceUnavailable_returns503() throws Exception {
        // GIVEN
        when(catalogsService.getSubscribers())
                .thenThrow(new CatalogServiceUnavailableException("Servicio de catálogos no disponible"));

        // WHEN / THEN
        mockMvc.perform(get("/api/v1/catalogs/subscribers"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("CATALOG_SERVICE_UNAVAILABLE"))
                .andExpect(jsonPath("$.message").value("Servicio de catálogos no disponible"));
    }
}
