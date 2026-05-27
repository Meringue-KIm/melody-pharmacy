package com.melodypharmacy.controller;

import com.melodypharmacy.dto.ConceptResponse;
import com.melodypharmacy.service.ConceptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/concepts")
@RequiredArgsConstructor
public class ConceptController {

    private final ConceptService conceptService;

    @GetMapping
    public ResponseEntity<List<ConceptResponse>> getAll() {
        return ResponseEntity.ok(conceptService.getAll());
    }
}
