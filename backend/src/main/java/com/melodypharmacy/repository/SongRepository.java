package com.melodypharmacy.repository;

import com.melodypharmacy.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {

    @Query("SELECT st.song FROM SongTag st WHERE st.situation.id = :situationId AND st.concept.id = :conceptId ORDER BY RAND()")
    List<Song> findRandomBySituationAndConcept(@Param("situationId") Long situationId,
                                               @Param("conceptId") Long conceptId);

    boolean existsByTitleAndArtist(String title, String artist);
}
