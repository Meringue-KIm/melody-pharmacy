package com.melodypharmacy.dto;

import com.melodypharmacy.entity.Situation;
import lombok.Getter;

@Getter
public class SituationResponse {
    private final Long id;
    private final String name;
    private final String icon;

    public SituationResponse(Situation situation) {
        this.id = situation.getId();
        this.name = situation.getName();
        this.icon = situation.getIcon();
    }
}
