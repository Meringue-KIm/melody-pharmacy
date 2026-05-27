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

    public SongResponse(Song song) {
        this.id = song.getId();
        this.title = song.getTitle();
        this.artist = song.getArtist();
        this.youtubeUrl = song.getYoutubeUrl();
        this.thumbnailUrl = song.getThumbnailUrl();
    }

    public SongResponse(Song song, boolean saved) {
        this(song);
        this.saved = saved;
    }
}
