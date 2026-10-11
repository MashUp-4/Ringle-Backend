package com.ceos24.mashup4backend.domain.tutor.entity;

import com.ceos24.mashup4backend.domain.interest.entity.Interest;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "tutor_interest",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_tutor_interest_tutor_interest",
                columnNames = {"tutor_id", "interest_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TutorInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "interest_id", nullable = false)
    private Interest interest;

    private TutorInterest(Tutor tutor, Interest interest) {
        this.tutor = tutor;
        this.interest = interest;
    }

    public static TutorInterest create(Tutor tutor, Interest interest) {
        return new TutorInterest(tutor, interest);
    }
}
