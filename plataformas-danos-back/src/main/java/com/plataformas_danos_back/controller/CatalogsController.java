package com.plataformas_danos_back.controller;

import com.plataformas_danos_back.model.dto.AgentDto;
import com.plataformas_danos_back.model.dto.BusinessLineDto;
import com.plataformas_danos_back.model.dto.SubscriberDto;
import com.plataformas_danos_back.service.CatalogsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/catalogs")
public class CatalogsController {

    private final CatalogsService catalogsService;

    @GetMapping("/subscribers")
    public ResponseEntity<List<SubscriberDto>> getSubscribers() {
        return ResponseEntity.ok(catalogsService.getSubscribers());
    }

    @GetMapping("/agents")
    public ResponseEntity<List<AgentDto>> getAgents() {
        return ResponseEntity.ok(catalogsService.getAgents());
    }

    @GetMapping("/business-lines")
    public ResponseEntity<List<BusinessLineDto>> getBusinessLines() {
        return ResponseEntity.ok(catalogsService.getBusinessLines());
    }
}
