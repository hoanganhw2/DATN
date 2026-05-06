package com.learning.englishpro.flashcard.repository;

import com.learning.englishpro.flashcard.entity.UserFlashcardProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserFlashcardProgressRepository extends JpaRepository<UserFlashcardProgress, Long> {

    Optional<UserFlashcardProgress> findByUserIdAndFlashcardId(Long userId, Long flashcardId);
    List<UserFlashcardProgress> findByUserIdAndFlashcard_Deck_Id(Long userId, Long deckId);
    void deleteByFlashcard_Deck_Id(Long deckId);

    @Query("SELECT p FROM UserFlashcardProgress p WHERE p.user.id = :userId AND p.flashcard.deck.id = :deckId AND (p.nextReviewDate IS NULL OR p.nextReviewDate <= :today)")
    List<UserFlashcardProgress> findDueCards(@Param("userId") Long userId, @Param("deckId") Long deckId, @Param("today") LocalDate today);

    @Query("SELECT COUNT(p) FROM UserFlashcardProgress p WHERE p.user.id = :userId AND p.flashcard.deck.id = :deckId AND p.lastReviewDate = :today AND p.repetitions = 0")
    int countNewCardsReviewedToday(@Param("userId") Long userId, @Param("deckId") Long deckId, @Param("today") LocalDate today);

    @Query("SELECT COUNT(p) FROM UserFlashcardProgress p WHERE p.user.id = :userId AND p.flashcard.deck.id = :deckId AND (p.nextReviewDate IS NULL OR p.nextReviewDate <= :today)")
    int countTotalDueCards(@Param("userId") Long userId, @Param("deckId") Long deckId, @Param("today") LocalDate today);

    @Query("SELECT COUNT(p) FROM UserFlashcardProgress p WHERE p.user.id = :userId AND p.flashcard.deck.id = :deckId AND p.repetitions >= 1")
    int countLearnedCards(@Param("userId") Long userId, @Param("deckId") Long deckId);

    @Query("SELECT COUNT(p) FROM UserFlashcardProgress p WHERE p.user.id = :userId AND p.flashcard.deck.id = :deckId AND (p.intervalDays >= 21 OR p.lastGrade = :masteredGrade)")
    int countMasteredCards(@Param("userId") Long userId, @Param("deckId") Long deckId, @Param("masteredGrade") com.learning.englishpro.flashcard.entity.FlashcardGrade masteredGrade);
}
