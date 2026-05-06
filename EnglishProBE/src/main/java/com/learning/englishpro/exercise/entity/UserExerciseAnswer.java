package com.learning.englishpro.exercise.entity;

import com.learning.englishpro.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
    name = "user_exercise_answers",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_user_exercise_question",
        columnNames = {"user_id", "exercise_id", "question_id"}
    ),
    indexes = {
        @Index(name = "idx_uea_user_id", columnList = "user_id"),
        @Index(name = "idx_uea_exercise_id", columnList = "exercise_id")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserExerciseAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private ExerciseQuestion question;

    @Column(columnDefinition = "TEXT")
    private String answerText;

    private Boolean isCorrect;

    @Column(precision = 8, scale = 2)
    private BigDecimal score;

    @Column(nullable = false)
    @Builder.Default
    private Instant submittedAt = Instant.now();
}
