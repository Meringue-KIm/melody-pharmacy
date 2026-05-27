package com.melodypharmacy.dto;

import com.melodypharmacy.entity.Concept;
import lombok.Getter;

@Getter
public class ConceptResponse {
    private final Long id;
    private final String name;
    private final String icon;

    public ConceptResponse(Concept concept) {
        this.id = concept.getId();
        this.name = concept.getName();
        this.icon = concept.getIcon();
    }
}
