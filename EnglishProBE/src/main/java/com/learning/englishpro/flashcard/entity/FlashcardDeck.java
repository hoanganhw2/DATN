package com.learning.englishpro.flashcard.entity;

import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.common.entity.BaseAuditEntity;
import com.learning.englishpro.course.entity.Course;
import com.learning.englishpro.course.entity.Lesson;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "flashcard_decks", indexes = {
        @Index(name = "idx_decks_teacher_id", columnList = "teacher_id"),
        @Index(name = "idx_decks_lesson_id",  columnList = "lesson_id"),
        @Index(name = "idx_decks_course_id",  columnList = "course_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class FlashcardDeck extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nullable: deck có thể thuộc lesson, course, hoặc độc lập */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    /** Nullable: deck có thể thuộc trực tiếp một course */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPublic = false;

    /** Cached count — đồng bộ mỗi khi thêm/xoá flashcard */
    @Column(nullable = false)
    @Builder.Default
    private Integer totalCards = 0;

    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @Builder.Default
    private List<Flashcard> flashcards = new ArrayList<>();
}
