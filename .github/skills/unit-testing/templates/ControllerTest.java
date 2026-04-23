/*
 * Plantilla para tests de Controllers con MockMvc (Spring Boot Test).
 * Copiar a: plataformas-danos-back/src/test/java/controller/<Feature>ControllerTest.java
 */
package com.sofka.insurance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// import com.sofka.insurance.service.FeatureService;
// import com.sofka.insurance.model.dto.FeatureRequest;
// import com.sofka.insurance.model.dto.FeatureResponse;

@WebMvcTest(FeatureController.class)
class FeatureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FeatureService featureService;

    // ─── Happy Path ─────────────────────────────────────────────────────────

    @Test
    void post_validRequest_returns201() throws Exception {
        // GIVEN
        // var request = new FeatureRequest("clientId-123", "COMPLETA");
        // var response = new FeatureResponse("COT-2026-000001", "clientId-123", "COMPLETA");
        // when(featureService.create(any())).thenReturn(response);

        // WHEN / THEN
        // mockMvc.perform(post("/api/v1/features")
        //         .contentType(MediaType.APPLICATION_JSON)
        //         .header("Authorization", "Bearer valid-token")
        //         .content(objectMapper.writeValueAsString(request)))
        //     .andExpect(status().isCreated())
        //     .andExpect(jsonPath("$.folio").value("COT-2026-000001"));
    }

    // ─── Error Path ─────────────────────────────────────────────────────────

    @Test
    void post_missingRequiredField_returns400() throws Exception {
        // GIVEN — request con campo requerido vacío
        // var invalidRequest = new FeatureRequest("", "COMPLETA");

        // WHEN / THEN
        // mockMvc.perform(post("/api/v1/features")
        //         .contentType(MediaType.APPLICATION_JSON)
        //         .header("Authorization", "Bearer valid-token")
        //         .content(objectMapper.writeValueAsString(invalidRequest)))
        //     .andExpect(status().isBadRequest());
    }

    @Test
    void post_missingAuthHeader_returns401() throws Exception {
        // WHEN / THEN
        // mockMvc.perform(post("/api/v1/features")
        //         .contentType(MediaType.APPLICATION_JSON)
        //         .content("{}"))
        //     .andExpect(status().isUnauthorized());
    }

    @Test
    void get_nonExistentFolio_returns404() throws Exception {
        // GIVEN
        // when(featureService.getByFolio("COT-0000-000000"))
        //     .thenThrow(new EntityNotFoundException("Not found"));

        // WHEN / THEN
        // mockMvc.perform(get("/api/v1/features/COT-0000-000000")
        //         .header("Authorization", "Bearer valid-token"))
        //     .andExpect(status().isNotFound());
    }
}
