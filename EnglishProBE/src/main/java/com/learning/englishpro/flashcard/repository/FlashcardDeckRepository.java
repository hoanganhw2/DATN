package com.learning.englishpro.flashcard.repository;

import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.flashcard.entity.FlashcardDeck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FlashcardDeckRepository extends JpaRepository<FlashcardDeck, Long> {

    @Query("SELECT d FROM FlashcardDeck d WHERE d.isPublic = :isPublic AND (:courseId IS NULL OR d.course.id = :courseId)")
    Page<FlashcardDeck> findPublicDecks(@Param("isPublic") Boolean isPublic, @Param("courseId") Long courseId,
            Pageable pageable);

    @Query("SELECT d FROM FlashcardDeck d LEFT JOIN FETCH d.flashcards WHERE d.id = :id")
    Optional<FlashcardDeck> findByIdWithFlashcards(@Param("id") Long id);

    /** Lấy tất cả bộ thẻ của một user (user_id), sắp xếp theo ngày tạo mới nhất */
    Page<FlashcardDeck> findByTeacherOrderByCreatedAtDesc(User teacher, Pageable pageable);
}
