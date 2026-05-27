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
    private String savedSituationIcon;
    private String savedSituationName;
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

    public SongResponse withSavedContext(String sitIcon, String sitName, String conIcon, String conName) {
        this.savedSituationIcon = sitIcon;
        this.savedSituationName = sitName;
        this.savedConceptIcon = conIcon;
        this.savedConceptName = conName;
        return this;
    }
}
