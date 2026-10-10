package com.ceos24.mashup4backend.domain.tutor.entity;

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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tutor_available_slot")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TutorAvailableSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private SlotStatus status;

    private TutorAvailableSlot(
            Tutor tutor,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        if (tutor == null) {
            throw new IllegalArgumentException("튜터는 필수입니다.");
        }

        if (startAt == null || endAt == null) {
            throw new IllegalArgumentException("시작 시간과 종료 시간은 필수입니다.");
        }

        if (!startAt.isBefore(endAt)) {
            throw new IllegalArgumentException(
                    "종료 시간은 시작 시간보다 늦어야 합니다."
            );
        }

        this.tutor = tutor;
        this.startAt = startAt;
        this.endAt = endAt;
        this.status = SlotStatus.OPEN;
    }

    public static TutorAvailableSlot create(
            Tutor tutor,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        return new TutorAvailableSlot(tutor, startAt, endAt);
    }

    public void book() {
        if (this.status != SlotStatus.OPEN) {
            throw new IllegalStateException("예약 가능한 슬롯이 아닙니다.");
        }

        this.status = SlotStatus.BOOKED;
    }
}