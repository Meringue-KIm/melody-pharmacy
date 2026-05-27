package com.melodypharmacy.repository;

import com.melodypharmacy.entity.SongTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongTagRepository extends JpaRepository<SongTag, Long> {
}
