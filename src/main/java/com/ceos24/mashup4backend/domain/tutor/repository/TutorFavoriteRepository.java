package com.ceos24.mashup4backend.domain.tutor.repository;

import com.ceos24.mashup4backend.domain.tutor.entity.TutorFavorite;
import java.util.Optional;
import java.util.Collection;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TutorFavoriteRepository extends JpaRepository<TutorFavorite, Long> {

    @Query("select f.tutor.id from TutorFavorite f where f.user.id = :userId and f.tutor.id in :tutorIds")
    Set<Long> findFavoriteTutorIds(
            @Param("userId") Long userId,
            @Param("tutorIds") Collection<Long> tutorIds);

    boolean existsByUserIdAndTutorId(Long userId, Long tutorId);

    Optional<TutorFavorite> findByUserIdAndTutorId(Long userId, Long tutorId);
}
