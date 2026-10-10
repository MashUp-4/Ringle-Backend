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

    @Column(name = "has_lesson_pass", nullable = false)
    private boolean hasLessonPass = false;

    public User(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public void updateProfile(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public void grantLessonPass() {
        this.hasLessonPass = true;
    }
}
