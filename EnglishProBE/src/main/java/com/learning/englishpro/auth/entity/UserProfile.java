package com.learning.englishpro.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Tất cả thông tin cá nhân của người dùng.
 * Dùng shared PK (@MapsId) với bảng users — quan hệ 1-1.
 */
@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    private Long id; // shared PK với bảng users

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 100)
    private String fullName;

    @Enumerated(EnumType.STRING)
    private UserGender gender;

    @Column(length = 20)
    private String phone;

    @Column(length = 500)
    private String avatarUrl;

    private LocalDate dateOfBirth;

    @Column(length = 100)
    private String address;

    @Column(length = 100)
    private String country;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(columnDefinition = "TEXT")
    private String learningGoal;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
