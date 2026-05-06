package com.learning.englishpro.exam.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "essay_submissions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EssaySubmission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Student who submitted the essay. In a real scenario, this would a ManyToOne relation to User.
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "prompt", columnDefinition = "TEXT")
    private String prompt;

    @Column(name = "student_essay", columnDefinition = "TEXT")
    private String studentEssay;

    @Column(name = "estimated_band_score")
    private Double estimatedBandScore;

    // We can store the full grading feedback as JSON in a TEXT column
    @Column(name = "grading_result_json", columnDefinition = "TEXT")
    private String gradingResultJson;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
