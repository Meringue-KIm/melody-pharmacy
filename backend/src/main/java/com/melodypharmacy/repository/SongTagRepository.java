package com.melodypharmacy.repository;

import com.melodypharmacy.entity.Song;
import com.melodypharmacy.entity.Situation;
import com.melodypharmacy.entity.Concept;
import com.melodypharmacy.entity.SongTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SongTagRepository extends JpaRepository<SongTag, Long> {

    boolean existsBySongAndSituationAndConcept(Song song, Situation situation, Concept concept);

    long countBySituationIdAndConceptId(Long situationId, Long conceptId);

    @Query("SELECT st FROM SongTag st " +
           "WHERE st.situation.id = :sitId AND st.concept.id = :conId " +
           "AND st.addedAt IS NOT NULL AND st.addedAt < :cutoff " +
           "AND (st.song.youtubeViewCount IS NULL OR st.song.youtubeViewCount < :maxViews) " +
           "AND NOT EXISTS (SELECT us FROM UserSong us WHERE us.song = st.song)")
    List<SongTag> findRemovableTags(@Param("sitId") Long sitId,
                                    @Param("conId") Long conId,
                                    @Param("cutoff") LocalDateTime cutoff,
                                    @Param("maxViews") Long maxViews);
}
