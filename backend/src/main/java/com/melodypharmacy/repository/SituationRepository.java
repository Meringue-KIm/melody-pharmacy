package com.melodypharmacy.repository;

import com.melodypharmacy.entity.Situation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SituationRepository extends JpaRepository<Situation, Long> {
    java.util.Optional<Situation> findByName(String name);
}
