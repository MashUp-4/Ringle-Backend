package com.ceos24.mashup4backend.domain.tutor.repository;

import com.ceos24.mashup4backend.domain.tutor.entity.Tutor;
import com.ceos24.mashup4backend.domain.learning.entity.LearningGoal;
import com.ceos24.mashup4backend.domain.tutor.enums.FieldOfStudy;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TutorRepository extends JpaRepository<Tutor, Long>, JpaSpecificationExecutor<Tutor> {

    @Query("""
            select t from Tutor t
            where t.learningGoal = :category
            order by case when t.fieldOfStudy = :interestField then 0 else 1 end,
                     case when t.rating is null then 1 else 0 end,
                     t.rating desc, t.reviewCount desc, t.id asc
            """)
    List<Tutor> findHomeRecommendations(
            @Param("category") LearningGoal category,
            @Param("interestField") FieldOfStudy interestField,
            Pageable pageable);
}
