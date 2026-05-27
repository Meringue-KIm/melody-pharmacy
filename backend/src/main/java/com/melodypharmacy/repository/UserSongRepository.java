package com.melodypharmacy.repository;

import com.melodypharmacy.entity.UserSong;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserSongRepository extends JpaRepository<UserSong, Long> {
    List<UserSong> findByUserId(Long userId);
    List<UserSong> findByUserIdAndSituationIdAndConceptId(Long userId, Long situationId, Long conceptId);
    Optional<UserSong> findByUserIdAndSongId(Long userId, Long songId);
    boolean existsByUserIdAndSongId(Long userId, Long songId);
}
