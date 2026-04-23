/*
 * patterns.java — Patrones de referencia para el agente backend-developer.
 * Este archivo NO se compila directamente. Es una referencia para que el agente
 * genere código consistente con la arquitectura Java 21 + Spring Boot 4.0.4.
 */

// ─── ENTITY PATTERN ─────────────────────────────────────────────────────────

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "quotations")
public class Quotation {

    @Id
    private String id;           // MongoDB ObjectId — nunca exponer en API

    private String folio;        // COT-AAAA-NNNNNN — ID de negocio público
    private String clientId;
    private String estadoValidacion;  // COMPLETA | INCOMPLETA | INACTIVA

    private Instant createdAt;
    private Instant updatedAt;

    @Version
    private Long version;        // versionado optimista
}

// ─── DTO PATTERN ────────────────────────────────────────────────────────────

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QuotationRequest {

    @NotBlank(message = "clientId es requerido")
    private String clientId;

    @NotBlank
    private String estadoValidacion;
}

@Data
public class QuotationResponse {
    private String folio;
    private String clientId;
    private String estadoValidacion;
    private Instant createdAt;
    // Nunca incluir el _id interno de MongoDB
}

// ─── REPOSITORY PATTERN ─────────────────────────────────────────────────────

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
import java.util.List;

public interface QuotationRepository extends MongoRepository<Quotation, String> {

    Optional<Quotation> findByFolio(String folio);

    List<Quotation> findByClientIdAndEstadoValidacion(String clientId, String estadoValidacion);
}

// ─── SERVICE PATTERN ────────────────────────────────────────────────────────

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuotationServiceImpl implements QuotationService {

    private final QuotationRepository quotationRepository;
    private final FolioGenerator folioGenerator;  // dependencia inyectada

    @Override
    public QuotationResponse create(QuotationRequest request) {
        var quotation = new Quotation();
        quotation.setFolio(folioGenerator.next());
        quotation.setClientId(request.getClientId());
        quotation.setEstadoValidacion(request.getEstadoValidacion());
        quotation.setCreatedAt(Instant.now());
        quotation.setUpdatedAt(Instant.now());

        var saved = quotationRepository.save(quotation);
        return toResponse(saved);
    }

    private QuotationResponse toResponse(Quotation q) {
        var response = new QuotationResponse();
        response.setFolio(q.getFolio());
        response.setClientId(q.getClientId());
        response.setEstadoValidacion(q.getEstadoValidacion());
        response.setCreatedAt(q.getCreatedAt());
        return response;
    }
}

// ─── CONTROLLER PATTERN ─────────────────────────────────────────────────────

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/quotations")
public class QuotationController {

    private final QuotationService quotationService;

    @PostMapping
    public ResponseEntity<QuotationResponse> create(@Valid @RequestBody QuotationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quotationService.create(request));
    }

    @GetMapping("/{folio}")
    public ResponseEntity<QuotationResponse> getByFolio(@PathVariable String folio) {
        return ResponseEntity.ok(quotationService.getByFolio(folio));
    }
}
