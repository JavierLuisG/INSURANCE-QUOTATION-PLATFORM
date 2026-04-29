package com.plataformas_danos_back.controller;

import com.plataformas_danos_back.exception.GlobalExceptionHandler;
import com.plataformas_danos_back.exception.TariffNotFoundException;
import com.plataformas_danos_back.model.dto.TariffCatDto;
import com.plataformas_danos_back.model.dto.TariffFireDto;
import com.plataformas_danos_back.service.TariffsService;
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

// SPEC-009 — TC-23: verifica HTTP 404 y body JSON a través de GlobalExceptionHandler
@ExtendWith(MockitoExtension.class)
class TariffsControllerMockMvcTest {

    @Mock
    private TariffsService tariffsService;

    @InjectMocks
    private TariffsController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // TC-23: zona inexistente → HTTP 404 + code=TARIFF_NOT_FOUND
    @Test
    void getTariffCat_notFound_returns404() throws Exception {
        // GIVEN
        when(tariffsService.getTariffCat("ZONA_INEXISTENTE"))
                .thenThrow(new TariffNotFoundException("Tarifa CAT no encontrada para la zona indicada"));

        // WHEN / THEN
        mockMvc.perform(get("/api/v1/tariffs/cat/ZONA_INEXISTENTE"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TARIFF_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Tarifa CAT no encontrada para la zona indicada"));
    }

    // Happy path — GET /api/v1/tariffs/fire → HTTP 200 con array JSON
    @Test
    void getTariffsFire_returns200WithList() throws Exception {
        // GIVEN
        var tariff = new TariffFireDto("ZONA_A", "Concreto", 0.0012, 1.15);
        when(tariffsService.getTariffsFire()).thenReturn(List.of(tariff));

        // WHEN / THEN
        mockMvc.perform(get("/api/v1/tariffs/fire"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].zonaRiesgo").value("ZONA_A"));
    }

    // Happy path — GET /api/v1/tariffs/cat/{zona} → HTTP 200 con DTO
    @Test
    void getTariffCat_validZona_returns200() throws Exception {
        // GIVEN
        var catDto = new TariffCatDto("ZONA_A", 0.0015, 0.0008);
        when(tariffsService.getTariffCat("ZONA_A")).thenReturn(catDto);

        // WHEN / THEN
        mockMvc.perform(get("/api/v1/tariffs/cat/ZONA_A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.zona").value("ZONA_A"))
                .andExpect(jsonPath("$.factorTEV").value(0.0015))
                .andExpect(jsonPath("$.factorFHM").value(0.0008));
    }
}
