package com.melodypharmacy.service;

import com.melodypharmacy.dto.SituationResponse;
import com.melodypharmacy.repository.SituationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SituationService {

    private final SituationRepository situationRepository;

    @Transactional(readOnly = true)
    public List<SituationResponse> getAll() {
        return situationRepository.findAll().stream()
                .map(SituationResponse::new)
                .toList();
    }
}
