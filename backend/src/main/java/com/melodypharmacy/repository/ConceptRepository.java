package com.melodypharmacy.repository;

import com.melodypharmacy.entity.Concept;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConceptRepository extends JpaRepository<Concept, Long> {
    java.util.Optional<Concept> findByName(String name);
}
