package com.plataformas_danos_back.repository;

import com.plataformas_danos_back.model.entity.Cotizacion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// SPEC-011 — Test de integración del repositorio de cotizaciones con MongoDB local
@SpringBootTest
@ActiveProfiles("test")
class CotizacionRepositoryTest {

    @Autowired
    private CotizacionRepository cotizacionRepository;

    @AfterEach
    void limpiarDatos() {
        cotizacionRepository.findByFolio("COT-TEST-REPO-001")
                .ifPresent(c -> cotizacionRepository.deleteById(c.getId()));
    }

    // ─── findByFolio ──────────────────────────────────────────────────────────

    @Test
    void findByFolio_cuandoExiste_retornaDocumento() {
        // GIVEN
        String folioEsperado = "COT-TEST-REPO-001";
        Instant ahora = Instant.now();
        Cotizacion cotizacion = Cotizacion.builder()
                .folio(folioEsperado)
                .estadoValidacion("INCOMPLETA")
                .createdAt(ahora)
                .updatedAt(ahora)
                .build();
        cotizacionRepository.save(cotizacion);

        // WHEN
        Optional<Cotizacion> resultado = cotizacionRepository.findByFolio(folioEsperado);

        // THEN
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getFolio()).isEqualTo(folioEsperado);
        assertThat(resultado.get().getEstadoValidacion()).isEqualTo("INCOMPLETA");
        assertThat(resultado.get().getId()).isNotNull();
    }
}
