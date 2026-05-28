package com.melodypharmacy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GeminiSongDto {
    private String title;
    private String artist;
    @JsonProperty("youtube_id")
    private String youtubeId;
}
