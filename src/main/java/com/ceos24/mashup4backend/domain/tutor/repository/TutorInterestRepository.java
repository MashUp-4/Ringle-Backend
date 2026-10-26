package com.ceos24.mashup4backend.domain.tutor.repository;

import com.ceos24.mashup4backend.domain.tutor.entity.TutorInterest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;

public interface TutorInterestRepository extends JpaRepository<TutorInterest, Long> {
    @EntityGraph(attributePaths = {"interest", "tutor"})
    List<TutorInterest> findByTutorIdInOrderByIdAsc(Collection<Long> tutorIds);
}
