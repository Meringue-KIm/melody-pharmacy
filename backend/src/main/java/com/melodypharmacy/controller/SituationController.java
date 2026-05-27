package com.melodypharmacy.controller;

import com.melodypharmacy.dto.SituationResponse;
import com.melodypharmacy.service.SituationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/situations")
@RequiredArgsConstructor
public class SituationController {

    private final SituationService situationService;

    @GetMapping
    public ResponseEntity<List<SituationResponse>> getAll() {
        return ResponseEntity.ok(situationService.getAll());
    }
}
