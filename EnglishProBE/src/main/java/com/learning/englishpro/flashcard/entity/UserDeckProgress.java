package com.learning.englishpro.flashcard.entity;

import com.learning.englishpro.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "user_deck_progress", indexes = {
        @Index(name = "idx_udp_user_id", columnList = "user_id"),
        @Index(name = "idx_udp_deck_id", columnList = "deck_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(UserDeckProgress.UserDeckProgressId.class)
public class UserDeckProgress {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "deck_id", nullable = false)
    private FlashcardDeck deck;

    @Builder.Default
    private Integer totalCards = 0;

    @Builder.Default
    private Integer learnedCards = 0;

    @Builder.Default
    private Integer masteredCards = 0;

    private Instant lastStudiedAt;

    @Column(nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // ── Composite PK class ──────────────────────────────────────────────────
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDeckProgressId implements Serializable {
        private Long user;
        private Long deck;
    }
}
