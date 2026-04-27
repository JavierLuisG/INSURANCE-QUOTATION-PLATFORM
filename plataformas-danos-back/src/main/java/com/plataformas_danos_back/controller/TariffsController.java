package com.plataformas_danos_back.controller;

import com.plataformas_danos_back.model.dto.TariffCatDto;
import com.plataformas_danos_back.model.dto.TariffElectronicEquipmentDto;
import com.plataformas_danos_back.model.dto.TariffFireDto;
import com.plataformas_danos_back.service.TariffsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tariffs")
public class TariffsController {

    private final TariffsService tariffsService;

    @GetMapping("/fire")
    public ResponseEntity<List<TariffFireDto>> getTariffsFire() {
        return ResponseEntity.ok(tariffsService.getTariffsFire());
    }

    @GetMapping("/cat")
    public ResponseEntity<TariffCatDto> getTariffCat(@RequestParam(required = true) String zona) {
        return ResponseEntity.ok(tariffsService.getTariffCat(zona));
    }

    @GetMapping("/electronic-equipment")
    public ResponseEntity<List<TariffElectronicEquipmentDto>> getTariffsElectronicEquipment() {
        return ResponseEntity.ok(tariffsService.getTariffsElectronicEquipment());
    }
}
