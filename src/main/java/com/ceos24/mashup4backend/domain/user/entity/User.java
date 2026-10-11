package com.ceos24.mashup4backend.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.util.Objects;

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

    private User(String name, String image) {
        this.name = Objects.requireNonNull(name, "이름은 필수입니다.");
        this.image = image;
    }

    public static User create(String name, String image) {
        return new User(name, image);
    }

    public void updateProfile(String name, String image) {
        this.name = Objects.requireNonNull(name, "이름은 필수입니다.");
        this.image = image;
    }

    public boolean hasLessonPass() {
        return credit20MinCount > 0 || credit40MinCount > 0;
    }
}
