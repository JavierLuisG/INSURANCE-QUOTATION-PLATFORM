/*
 * Plantilla para tests de integración de Repository con Testcontainers + MongoDB real.
 * Copiar a: plataformas-danos-back/src/test/java/repository/<Feature>RepositoryIT.java
 */
package com.sofka.insurance.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// import com.sofka.insurance.model.entity.Feature;

@DataMongoTest
@Testcontainers
class FeatureRepositoryIT {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7");

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @Autowired
    private FeatureRepository repository;

    // ─── Happy Path ─────────────────────────────────────────────────────────

    @Test
    void save_validEntity_persistsAndReturnsWithId() {
        // GIVEN
        // var feature = new Feature();
        // feature.setFolio("COT-2026-000001");
        // feature.setClientId("client-123");

        // WHEN
        // var saved = repository.save(feature);

        // THEN
        // assertThat(saved.getId()).isNotNull();
        // assertThat(saved.getFolio()).isEqualTo("COT-2026-000001");
    }

    @Test
    void findByFolio_existingFolio_returnsEntity() {
        // GIVEN
        // var feature = new Feature();
        // feature.setFolio("COT-2026-000002");
        // repository.save(feature);

        // WHEN
        // Optional<Feature> result = repository.findByFolio("COT-2026-000002");

        // THEN
        // assertThat(result).isPresent();
        // assertThat(result.get().getFolio()).isEqualTo("COT-2026-000002");
    }

    // ─── Error Path ─────────────────────────────────────────────────────────

    @Test
    void findByFolio_nonExistentFolio_returnsEmpty() {
        // WHEN
        // Optional<Feature> result = repository.findByFolio("COT-0000-000000");

        // THEN
        // assertThat(result).isEmpty();
    }
}
