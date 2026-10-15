package com.ceos24.mashup4backend.domain.tutor.repository;

import com.ceos24.mashup4backend.domain.tutor.entity.TutorFavorite;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutorFavoriteRepository extends JpaRepository<TutorFavorite, Long> {

    boolean existsByUserIdAndTutorId(Long userId, Long tutorId);

    Optional<TutorFavorite> findByUserIdAndTutorId(Long userId, Long tutorId);
}
