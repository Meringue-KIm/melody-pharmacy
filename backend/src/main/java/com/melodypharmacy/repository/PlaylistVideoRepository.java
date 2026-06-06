package com.melodypharmacy.repository;

import com.melodypharmacy.entity.PlaylistVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistVideoRepository extends JpaRepository<PlaylistVideo, Long> {
    List<PlaylistVideo> findBySituationIdAndConceptId(Long situationId, Long conceptId);
    boolean existsBySituationIdAndConceptIdAndYoutubeVideoId(Long situationId, Long conceptId, String videoId);
}
