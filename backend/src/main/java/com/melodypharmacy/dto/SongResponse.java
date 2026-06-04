package com.melodypharmacy.dto;

import com.melodypharmacy.entity.Song;
import lombok.Getter;

@Getter
public class SongResponse {
    private final Long id;
    private final String title;
    private final String artist;
    private final String youtubeUrl;
    private final String thumbnailUrl;
    private boolean saved;
    private Long savedSituationId;
    private String savedSituationIcon;
    private String savedSituationName;
    private Long savedConceptId;
    private String savedConceptIcon;
    private String savedConceptName;

    public SongResponse(Song song, boolean saved) {
        this.id = song.getId();
        this.title = song.getTitle();
        this.artist = song.getArtist();
        this.youtubeUrl = song.getYoutubeUrl();
        this.thumbnailUrl = song.getThumbnailUrl();
        this.saved = saved;
    }

    public SongResponse withSavedContext(Long sitId, String sitIcon, String sitName, Long conId, String conIcon, String conName) {
        this.savedSituationId = sitId;
        this.savedSituationIcon = sitIcon;
        this.savedSituationName = sitName;
        this.savedConceptId = conId;
        this.savedConceptIcon = conIcon;
        this.savedConceptName = conName;
        return this;
    }
}
