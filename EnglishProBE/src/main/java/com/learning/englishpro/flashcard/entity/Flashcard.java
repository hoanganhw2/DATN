package com.learning.englishpro.flashcard.entity;

import com.learning.englishpro.common.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "flashcards", indexes = {
        @Index(name = "idx_flashcards_deck_id", columnList = "deck_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Flashcard extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "deck_id", nullable = false)
    private FlashcardDeck deck;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String frontText;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String backText;

    @Column(length = 500)
    private String frontImageUrl;

    @Column(length = 500)
    private String backImageUrl;

    @Column(length = 500)
    private String audioUrl;

    @Column(columnDefinition = "TEXT")
    private String exampleSentence;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;
}
