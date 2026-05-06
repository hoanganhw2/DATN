package com.learning.englishpro.flashcard.entity;

import com.learning.englishpro.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
    name = "user_flashcard_progress",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_user_flashcard",
        columnNames = {"user_id", "flashcard_id"}
    ),
    indexes = {
        @Index(name = "idx_ufp_user_id",          columnList = "user_id"),
        @Index(name = "idx_ufp_next_review_date",  columnList = "next_review_date")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFlashcardProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "flashcard_id", nullable = false)
    private Flashcard flashcard;

    @Builder.Default
    private Integer repetitions = 0;

    /** SM-2 ease factor */
    @Column(precision = 4, scale = 2)
    @Builder.Default
    private BigDecimal easeFactor = new BigDecimal("2.50");

    @Builder.Default
    private Integer intervalDays = 1;

    private LocalDate nextReviewDate;
    private LocalDate lastReviewDate;

    @Enumerated(EnumType.STRING)
    private FlashcardGrade lastGrade;

    @Builder.Default
    private Integer totalReviews = 0;

    @Builder.Default
    private Integer correctStreak = 0;
}
