package com.learning.englishpro.exam.entity;

import com.learning.englishpro.common.entity.BaseAuditEntity;
import com.learning.englishpro.exercise.entity.QuestionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exam_questions", indexes = {
        @Index(name = "idx_exam_questions_section_id", columnList = "section_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class ExamQuestion extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "section_id", nullable = false)
    private ExamSection section;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(columnDefinition = "TEXT")
    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType questionType;

    /** JSON array of answer choices, e.g. ["A","B","C","D"] */
    @Column(columnDefinition = "JSON")
    private String options;

    @Column(columnDefinition = "TEXT")
    private String correctAnswer;

    /** Giải thích tại sao đáp án đúng — hỗ trợ học viên review sau bài thi */
    @Column(columnDefinition = "TEXT")
    private String explanation;

    private Integer points;

    @Column(name = "order_index")
    private Integer orderIndex;
}
