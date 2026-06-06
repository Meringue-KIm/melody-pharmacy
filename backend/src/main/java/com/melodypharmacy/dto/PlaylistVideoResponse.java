package com.melodypharmacy.dto;

import com.melodypharmacy.entity.PlaylistVideo;
import lombok.Getter;

@Getter
public class PlaylistVideoResponse {
    private final Long id;
    private final String youtubeVideoId;
    private final String title;
    private final String thumbnailUrl;

    public PlaylistVideoResponse(PlaylistVideo pv) {
        this.id = pv.getId();
        this.youtubeVideoId = pv.getYoutubeVideoId();
        this.title = pv.getTitle();
        this.thumbnailUrl = pv.getThumbnailUrl();
    }
}
