package com.learning.englishpro.exercise.entity;

import com.learning.englishpro.common.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exercise_questions", indexes = {
        @Index(name = "idx_exercise_questions_exercise_id", columnList = "exercise_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class ExerciseQuestion extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionText;

    /** Hình ảnh hoặc audio đính kèm câu hỏi */
    @Column(columnDefinition = "TEXT")
    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType questionType;

    /**
     * JSON array of answer options, e.g. ["A","B","C","D"]
     * Store as TEXT; handle serialization in service layer.
     */
    @Column(columnDefinition = "JSON")
    private String options;

    @Column(columnDefinition = "TEXT")
    private String correctAnswer;

    /** Giải thích đáp án đúng — hỗ trợ học viên hiểu sâu hơn */
    @Column(columnDefinition = "TEXT")
    private String explanation;

    private Integer points;

    @Column(name = "order_index")
    private Integer orderIndex;
}
