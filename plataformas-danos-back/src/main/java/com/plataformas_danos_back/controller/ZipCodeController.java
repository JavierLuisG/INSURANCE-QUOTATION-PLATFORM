package com.plataformas_danos_back.controller;

import com.plataformas_danos_back.model.dto.ZipCodeDto;
import com.plataformas_danos_back.service.ZipCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/postal-codes")
public class ZipCodeController {

    private final ZipCodeService zipCodeService;

    @GetMapping("/{codigoPostal}")
    public ResponseEntity<ZipCodeDto> getByZipCode(@PathVariable String codigoPostal) {
        return ResponseEntity.ok(zipCodeService.getByZipCode(codigoPostal));
    }
}
