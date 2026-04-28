package com.plataformas_danos_back.controller;

import com.plataformas_danos_back.model.dto.ValidationRequest;
import com.plataformas_danos_back.model.dto.ValidationResult;
import com.plataformas_danos_back.model.entity.DataInconsistency;
import com.plataformas_danos_back.model.entity.ValidationRule;
import com.plataformas_danos_back.repository.ValidationRuleRepository;
import com.plataformas_danos_back.service.DataValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/data-validation")
public class DataValidationController {

    private final DataValidationService dataValidationService;
    private final ValidationRuleRepository validationRuleRepository;

    @PostMapping("/validate")
    public ResponseEntity<ValidationResult> validate(@Valid @RequestBody ValidationRequest request) {
        ValidationResult result = dataValidationService.validateBatch(
                request.getDataType(),
                request.getRecords(),
                request.getCorrelationId()
        );

        if (result.getInconsistentRecords() > 0) {
            return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(result);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/inconsistencies")
    public ResponseEntity<Page<DataInconsistency>> getInconsistencies(
            @RequestParam(required = false) String dataType,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        int clampedSize = Math.min(size, 100);
        Pageable pageable = PageRequest.of(page, clampedSize);
        Page<DataInconsistency> result = dataValidationService.getInconsistencies(dataType, status, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/rules")
    public ResponseEntity<List<ValidationRule>> getRules(
            @RequestParam(required = false) String dataType,
            @RequestParam(required = false) Boolean enabled) {

        List<ValidationRule> rules;
        if (dataType != null && enabled != null) {
            rules = validationRuleRepository.findByDataTypeAndEnabled(dataType, enabled);
        } else if (dataType != null) {
            rules = validationRuleRepository.findByDataType(dataType);
        } else if (enabled != null) {
            rules = validationRuleRepository.findByEnabled(enabled);
        } else {
            rules = validationRuleRepository.findAll();
        }
        return ResponseEntity.ok(rules);
    }
}
