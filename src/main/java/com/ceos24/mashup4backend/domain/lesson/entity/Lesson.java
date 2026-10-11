package com.ceos24.mashup4backend.domain.lesson.entity;

import com.ceos24.mashup4backend.domain.tutor.entity.TutorAvailableSlot;
import com.ceos24.mashup4backend.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "lesson")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "slot_id", nullable = false)
    private TutorAvailableSlot slot;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private LessonStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private Lesson(User user, TutorAvailableSlot slot) {
        if (user == null || slot == null) {
            throw new IllegalArgumentException(
                    "사용자와 예약할 슬롯은 필수입니다."
            );
        }

        this.user = user;
        this.slot = slot;
        this.status = LessonStatus.BOOKED;
    }

    public static Lesson create(User user, TutorAvailableSlot slot) {
        return new Lesson(user, slot);
    }

    public void cancel() {
        validateBooked();
        this.status = LessonStatus.CANCELLED;
    }

    public void complete() {
        validateBooked();
        this.status = LessonStatus.COMPLETED;
    }

    private void validateBooked() {
        if (this.status != LessonStatus.BOOKED) {
            throw new IllegalStateException("예약 상태의 수업만 변경할 수 있습니다.");
        }
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}