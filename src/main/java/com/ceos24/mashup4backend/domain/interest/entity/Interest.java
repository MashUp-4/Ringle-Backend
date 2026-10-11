package com.ceos24.mashup4backend.domain.interest.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "interest")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Interest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, unique = true, length = 50)
    private InterestType type;

    private Interest(InterestType type) {
        this.type = Objects.requireNonNull(type);
    }

    public static Interest create(InterestType type) {
        return new Interest(type);
    }
}
