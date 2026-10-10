package com.ceos24.mashup4backend.domain.tutor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tutor")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(
            name = "profile_image_url",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String profileImageUrl;

    @Column(name = "university")
    private String university;

    @Column(name = "major")
    private String major;

    @Column(name = "gender", nullable = false, length = 10)
    private String gender;

    @Column(name = "country", nullable = false, length = 50)
    private String country;

    @Column(name = "introduction", columnDefinition = "TEXT")
    private String introduction;

    @Column(name = "acceptance_rate")
    private Float acceptanceRate;

    @Column(name = "lesson_count", nullable = false)
    private int lessonCount = 0;

    @Column(name = "rating")
    private Float rating;

    @Column(name = "review_count", nullable = false)
    private int reviewCount = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    private Tutor(
            String name,
            String profileImageUrl,
            String university,
            String major,
            String gender,
            String country,
            String introduction
    ) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.university = university;
        this.major = major;
        this.gender = gender;
        this.country = country;
        this.introduction = introduction;
    }

    public static Tutor create(
            String name,
            String profileImageUrl,
            String university,
            String major,
            String gender,
            String country,
            String introduction
    ) {
        return new Tutor(
                name,
                profileImageUrl,
                university,
                major,
                gender,
                country,
                introduction
        );
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
