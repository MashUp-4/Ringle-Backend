package com.ceos24.mashup4backend.domain.user.entity;

import com.ceos24.mashup4backend.domain.learning.entity.LearningGoal;
import com.ceos24.mashup4backend.domain.tutor.enums.FieldOfStudy;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "image", columnDefinition = "TEXT")
    private String image;

    @Enumerated(EnumType.STRING)
    @Column(name = "learning_goal", nullable = false, length = 30)
    private LearningGoal learningGoal;

    @Enumerated(EnumType.STRING)
    @Column(name = "field_of_study", length = 30)
    private FieldOfStudy fieldOfStudy;

    @Column(name = "time_zone", nullable = false, length = 50)
    private String timeZone = "Asia/Seoul";

    @Column(name = "credit_20_min_count", nullable = false)
    private int credit20MinCount = 0;

    @Column(name = "credit_40_min_count", nullable = false)
    private int credit40MinCount = 0;

    @Column(name = "points", nullable = false)
    private long points = 0L;

    @Column(name = "unused_coupon_count", nullable = false)
    private int unusedCouponCount = 0;

    private User(String name, String image, LearningGoal learningGoal) {
        this.name = Objects.requireNonNull(name, "이름은 필수입니다.");
        this.image = image;
        this.learningGoal = Objects.requireNonNull(learningGoal, "학습 목적은 필수입니다.");
    }

    public static User create(String name, String image, LearningGoal learningGoal) {
        return new User(name, image, learningGoal);
    }

    public void updateProfile(String name, String image) {
        this.name = Objects.requireNonNull(name, "이름은 필수입니다.");
        this.image = image;
    }

    public boolean hasLessonPass() {
        return credit20MinCount > 0 || credit40MinCount > 0;
    }

    public void updateFieldOfStudy(FieldOfStudy fieldOfStudy) {
        this.fieldOfStudy = fieldOfStudy;
    }
}
