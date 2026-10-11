package com.ceos24.mashup4backend.domain.tutor.entity;

import com.ceos24.mashup4backend.domain.tutor.enums.*;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

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

    // 화면에 표시하는 상세 전공
    @Column(name = "major")
    private String major;

    // 검색에 사용하는 전공 계열
    @Enumerated(EnumType.STRING)
    @Column(name = "field_of_study", length = 30)
    private FieldOfStudy fieldOfStudy;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private Gender gender;

    @Column(name = "country", nullable = false, length = 50)
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(name = "accent", length = 20)
    private Accent accent;

    @Column(name = "introduction", columnDefinition = "TEXT")
    private String introduction;

    // 경험 분류 중 대표값 하나라는 가정
    @Enumerated(EnumType.STRING)
    @Column(name = "primary_keyword", length = 50)
    private ExperienceTag primaryKeyword;

    @Enumerated(EnumType.STRING)
    @Column(name = "strength", length = 50)
    private StrengthTag strength;

    @Column(name = "is_pointback", nullable = false)
    private boolean pointback = false;

    // 0~100 정수 퍼센트, 데이터 없으면 null
    @Column(name = "acceptance_rate")
    private Integer acceptanceRate;

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
            Gender gender,
            String country,
            String introduction
    ) {
        this.name = Objects.requireNonNull(name, "이름은 필수입니다.");
        this.profileImageUrl = Objects.requireNonNull(
                profileImageUrl, "프로필 이미지 URL은 필수입니다."
        );
        this.university = university;
        this.major = major;
        this.gender = Objects.requireNonNull(gender, "성별은 필수입니다.");
        this.country = Objects.requireNonNull(country, "국가는 필수입니다.");
        this.introduction = introduction;
    }

    public static Tutor create(
            String name,
            String profileImageUrl,
            String university,
            String major,
            Gender gender,
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

    public void updateClassification(
            FieldOfStudy fieldOfStudy,
            Accent accent,
            ExperienceTag primaryKeyword
    ) {
        this.fieldOfStudy = fieldOfStudy;
        this.accent = accent;
        this.primaryKeyword = primaryKeyword;
    }

    public void updatePointback(boolean pointback) {
        this.pointback = pointback;
    }

    public void updateAcceptanceRate(Integer acceptanceRate) {
        if (acceptanceRate != null
                && (acceptanceRate < 0 || acceptanceRate > 100)) {
            throw new IllegalArgumentException(
                    "수락률은 0 이상 100 이하여야 합니다."
            );
        }

        this.acceptanceRate = acceptanceRate;
    }

    public void updateStrength(StrengthTag strength) {
        this.strength = strength;
    }

    @PrePersist
    private void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}