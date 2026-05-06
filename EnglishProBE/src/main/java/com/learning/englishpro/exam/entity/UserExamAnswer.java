package com.learning.englishpro.exam.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "user_exam_answers", indexes = {
        @Index(name = "idx_uea_exam_result_id", columnList = "exam_result_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserExamAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_result_id", nullable = false)
    private UserExamResult examResult;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private ExamQuestion question;

    @Column(columnDefinition = "TEXT")
    private String answerText;

    private Boolean isCorrect;

    @Column(precision = 8, scale = 2)
    private BigDecimal pointsEarned;
}
