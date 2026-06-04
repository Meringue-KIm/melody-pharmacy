package com.melodypharmacy.repository;

import com.melodypharmacy.entity.PlayHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface PlayHistoryRepository extends JpaRepository<PlayHistory, Long> {
    List<PlayHistory> findByUserIdOrderByPlayedAtDesc(Long userId);

    boolean existsBySongId(Long songId);

    @Query("SELECT DISTINCT ph.song.id FROM PlayHistory ph WHERE ph.user.id = :userId AND ph.playedAt >= :since")
    Set<Long> findRecentSongIdsByUserId(@Param("userId") Long userId, @Param("since") LocalDateTime since);
}
