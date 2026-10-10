package com.ceos24.mashup4backend.domain.tutor.entity;

import com.ceos24.mashup4backend.domain.user.entity.User;
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
        name = "tutor_favorite",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_tutor_favorite_user_tutor",
                columnNames = {"user_id", "tutor_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TutorFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;

    private TutorFavorite(User user, Tutor tutor) {
        this.user = user;
        this.tutor = tutor;
    }

    public static TutorFavorite create(User user, Tutor tutor) {
        return new TutorFavorite(user, tutor);
    }
}
