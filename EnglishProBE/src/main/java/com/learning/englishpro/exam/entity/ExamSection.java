package com.learning.englishpro.exam.entity;

import com.learning.englishpro.common.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exam_sections", indexes = {
        @Index(name = "idx_exam_sections_exam_id", columnList = "exam_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class ExamSection extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    /** e.g. Listening, Reading, Writing, Speaking */
    @Column(nullable = false, length = 100)
    private String sectionName;

    private Integer sectionOrder;

    /** Duration in seconds */
    private Integer duration;

    private Integer totalQuestions;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @Builder.Default
    private List<ExamQuestion> questions = new ArrayList<>();
}
