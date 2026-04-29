package com.plataformas_danos_back.controller;

import com.plataformas_danos_back.exception.GlobalExceptionHandler;
import com.plataformas_danos_back.exception.InvalidZipCodeFormatException;
import com.plataformas_danos_back.exception.ZipCodeNotFoundException;
import com.plataformas_danos_back.model.dto.ZipCodeDto;
import com.plataformas_danos_back.service.ZipCodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// SPEC-009 — TC-21, TC-22: verifica HTTP 400/404 y body JSON a través de GlobalExceptionHandler
@ExtendWith(MockitoExtension.class)
class ZipCodeControllerMockMvcTest {

    @Mock
    private ZipCodeService zipCodeService;

    @InjectMocks
    private ZipCodeController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // TC-21: formato inválido → HTTP 400 + code=INVALID_ZIP_CODE_FORMAT
    @Test
    void getByZipCode_invalidFormat_returns400() throws Exception {
        // GIVEN
        when(zipCodeService.getByZipCode("1234"))
                .thenThrow(new InvalidZipCodeFormatException(
                        "Formato de código postal inválido. Debe ser exactamente 5 dígitos numéricos."));

        // WHEN / THEN
        mockMvc.perform(get("/api/v1/zip-codes/1234"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_ZIP_CODE_FORMAT"))
                .andExpect(jsonPath("$.message").exists());
    }

    // TC-22: CP no encontrado → HTTP 404 + code=ZIP_CODE_NOT_FOUND
    @Test
    void getByZipCode_notFound_returns404() throws Exception {
        // GIVEN
        when(zipCodeService.getByZipCode("00000"))
                .thenThrow(new ZipCodeNotFoundException("Código postal no encontrado"));

        // WHEN / THEN
        mockMvc.perform(get("/api/v1/zip-codes/00000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ZIP_CODE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Código postal no encontrado"));
    }

    // Happy path → HTTP 200 con campos del DTO
    @Test
    void getByZipCode_validCode_returns200() throws Exception {
        // GIVEN
        var dto = new ZipCodeDto("06600", "ZONA_A", "ALTO", "Ciudad de México", "Cuauhtémoc", "CDMX");
        when(zipCodeService.getByZipCode("06600")).thenReturn(dto);

        // WHEN / THEN
        mockMvc.perform(get("/api/v1/zip-codes/06600"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoPostal").value("06600"))
                .andExpect(jsonPath("$.zonaCAT").value("ZONA_A"));
    }
}
