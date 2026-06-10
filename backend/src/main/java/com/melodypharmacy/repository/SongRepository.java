package com.melodypharmacy.repository;

import com.melodypharmacy.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {

    @Query("SELECT st.song FROM SongTag st WHERE st.situation.id = :situationId AND st.concept.id = :conceptId ORDER BY RAND()")
    List<Song> findRandomBySituationAndConcept(@Param("situationId") Long situationId,
                                               @Param("conceptId") Long conceptId);

    @Query("SELECT st.song FROM SongTag st WHERE st.situation.id = :situationId AND st.concept.id = :conceptId " +
           "AND st.song.id NOT IN (" +
           "  SELECT ph.song.id FROM PlayHistory ph WHERE ph.user.id = :userId " +
           "  AND ph.situation.id = :situationId AND ph.concept.id = :conceptId" +
           ") ORDER BY RAND()")
    List<Song> findRandomExcludingPlayed(@Param("situationId") Long situationId,
                                         @Param("conceptId") Long conceptId,
                                         @Param("userId") Long userId);

    boolean existsByTitleAndArtist(String title, String artist);
    java.util.Optional<Song> findFirstByTitleAndArtist(String title, String artist);
    java.util.Optional<Song> findFirstByTitleIgnoreCaseAndArtistIgnoreCase(String title, String artist);

    @Transactional
    @Modifying
    @Query("UPDATE Song s SET s.youtubeViewCount = :viewCount, s.statsUpdatedAt = :updatedAt WHERE s.id = :id")
    void updateViewCount(@Param("id") Long id,
                         @Param("viewCount") Long viewCount,
                         @Param("updatedAt") LocalDateTime updatedAt);

    @Transactional
    @Modifying
    @Query("UPDATE Song s SET s.youtubeUrl = :youtubeUrl, s.thumbnailUrl = :thumbnailUrl WHERE s.id = :id")
    void updateYoutubeInfo(@Param("id") Long id,
                           @Param("youtubeUrl") String youtubeUrl,
                           @Param("thumbnailUrl") String thumbnailUrl);
}
