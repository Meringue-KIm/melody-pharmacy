package com.melodypharmacy.repository;

import com.melodypharmacy.entity.UserSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserSongRepository extends JpaRepository<UserSong, Long> {
    List<UserSong> findByUserId(Long userId);
    List<UserSong> findByUserIdAndSituationIdAndConceptId(Long userId, Long situationId, Long conceptId);
    Optional<UserSong> findByUserIdAndSongId(Long userId, Long songId);
    boolean existsByUserIdAndSongId(Long userId, Long songId);

    @Query("SELECT us.song.id FROM UserSong us WHERE us.user.id = :userId")
    Set<Long> findSongIdsByUserId(@Param("userId") Long userId);
}
