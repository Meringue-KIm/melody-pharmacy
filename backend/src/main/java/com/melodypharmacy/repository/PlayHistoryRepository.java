package com.melodypharmacy.repository;

import com.melodypharmacy.entity.PlayHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlayHistoryRepository extends JpaRepository<PlayHistory, Long> {
    List<PlayHistory> findByUserIdOrderByPlayedAtDesc(Long userId);
}
