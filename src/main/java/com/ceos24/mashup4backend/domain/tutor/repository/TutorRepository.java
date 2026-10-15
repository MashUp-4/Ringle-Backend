package com.ceos24.mashup4backend.domain.tutor.repository;

import com.ceos24.mashup4backend.domain.tutor.entity.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutorRepository extends JpaRepository<Tutor, Long> {
}
