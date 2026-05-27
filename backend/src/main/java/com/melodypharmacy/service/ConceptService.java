package com.melodypharmacy.service;

import com.melodypharmacy.dto.ConceptResponse;
import com.melodypharmacy.repository.ConceptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConceptService {

    private final ConceptRepository conceptRepository;

    @Transactional(readOnly = true)
    public List<ConceptResponse> getAll() {
        return conceptRepository.findAll().stream()
                .map(ConceptResponse::new)
                .toList();
    }
}
