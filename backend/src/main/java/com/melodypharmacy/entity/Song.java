package com.melodypharmacy.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "songs", uniqueConstraints = {
    @UniqueConstraint(name = "uk_songs_title_artist", columnNames = {"title", "artist"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String artist;

    @Column(name = "youtube_url", nullable = false)
    private String youtubeUrl;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "youtube_view_count")
    private Long youtubeViewCount;

    @Column(name = "stats_updated_at")
    private LocalDateTime statsUpdatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
