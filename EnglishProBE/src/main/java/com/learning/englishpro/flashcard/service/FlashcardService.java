package com.learning.englishpro.flashcard.service;

import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.auth.repository.UserRepository;
import com.learning.englishpro.common.exception.AppException;
import com.learning.englishpro.common.exception.ErrorCode;
import com.learning.englishpro.course.entity.Course;
import com.learning.englishpro.course.entity.Lesson;
import com.learning.englishpro.course.repository.CourseRepository;
import com.learning.englishpro.course.repository.LessonRepository;
import com.learning.englishpro.flashcard.dto.request.AddFlashcardsRequest;
import com.learning.englishpro.flashcard.dto.request.CreateFlashcardDeckRequest;
import com.learning.englishpro.flashcard.dto.request.UpdateFlashcardRequest;
import com.learning.englishpro.flashcard.dto.response.FlashcardDeckDetailResponse;
import com.learning.englishpro.flashcard.dto.response.FlashcardDeckSummaryResponse;
import com.learning.englishpro.flashcard.dto.response.FlashcardResponse;
import com.learning.englishpro.flashcard.entity.Flashcard;
import com.learning.englishpro.flashcard.entity.FlashcardDeck;
import com.learning.englishpro.flashcard.entity.FlashcardGrade;
import com.learning.englishpro.flashcard.entity.UserFlashcardProgress;
import com.learning.englishpro.flashcard.repository.FlashcardDeckRepository;
import com.learning.englishpro.flashcard.repository.FlashcardRepository;
import com.learning.englishpro.flashcard.repository.UserFlashcardProgressRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FlashcardService {

    private final FlashcardDeckRepository flashcardDeckRepository;
    private final FlashcardRepository flashcardRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;

    private final UserFlashcardProgressRepository userFlashcardProgressRepository;

    private User resolveUser(UserDetails principal) {
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private void checkDeckOwnership(FlashcardDeck deck, User teacher) {
        if (!deck.getTeacher().getId().equals(teacher.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
    }

    @Transactional(readOnly = true)
    public Page<FlashcardDeckSummaryResponse> getPublicDecks(Long courseId, Pageable pageable) {
        return flashcardDeckRepository.findPublicDecks(true, courseId, pageable)
                .map(deck -> new FlashcardDeckSummaryResponse(
                        deck.getId(),
                        deck.getCourse() != null ? deck.getCourse().getId() : null,
                        deck.getLesson() != null ? deck.getLesson().getId() : null,
                        deck.getTeacher().getId(),
                        deck.getTitle(),
                        deck.getDescription(),
                        deck.getIsPublic(),
                        deck.getTotalCards(),
                        0, 0, 0
                ));
    }

    /** Lấy danh sách bộ thẻ của chính user đang đăng nhập */
    @Transactional(readOnly = true)
    public Page<FlashcardDeckSummaryResponse> getMyDecks(UserDetails principal, Pageable pageable) {
        User user = resolveUser(principal);
        return flashcardDeckRepository.findByTeacherOrderByCreatedAtDesc(user, pageable)
                .map(deck -> {
                    int learned = userFlashcardProgressRepository.countLearnedCards(user.getId(), deck.getId());
                    int mastered = userFlashcardProgressRepository.countMasteredCards(user.getId(), deck.getId(), FlashcardGrade.EASY);
                    int dueToday = userFlashcardProgressRepository.countTotalDueCards(user.getId(), deck.getId(), java.time.LocalDate.now());
                    return new FlashcardDeckSummaryResponse(
                            deck.getId(),
                            deck.getCourse() != null ? deck.getCourse().getId() : null,
                            deck.getLesson() != null ? deck.getLesson().getId() : null,
                            deck.getTeacher().getId(),
                            deck.getTitle(),
                            deck.getDescription(),
                            deck.getIsPublic(),
                            deck.getTotalCards(),
                            learned,
                            mastered,
                            dueToday
                    );
                });
    }

    @Transactional(readOnly = true)
    public FlashcardDeckDetailResponse getDeckDetail(Long deckId, UserDetails principal) {
        User user = resolveUser(principal);
        FlashcardDeck deck = flashcardDeckRepository.findByIdWithFlashcards(deckId)
                .orElseThrow(() -> new AppException(ErrorCode.FLASHCARD_DECK_NOT_FOUND));
                
        // Fetch all progress for this user in this deck
        List<UserFlashcardProgress> progressList = userFlashcardProgressRepository.findByUserIdAndFlashcard_Deck_Id(user.getId(), deckId);
        
        Map<Long, String> statusMap = progressList.stream().collect(Collectors.toMap(
            p -> p.getFlashcard().getId(),
            this::calculateStatus
        ));

        return FlashcardDeckDetailResponse.from(deck, statusMap);
    }

    private String calculateStatus(UserFlashcardProgress p) {
        if (p == null || p.getRepetitions() == 0) return "new";
        if (p.getIntervalDays() >= 21 || p.getLastGrade() == FlashcardGrade.EASY) return "mastered";
        // If it's due today or overdue, we can call it 'review'
        if (p.getNextReviewDate() != null && !p.getNextReviewDate().isAfter(java.time.LocalDate.now())) {
            return "review";
        }
        return "learning";
    }

    public FlashcardDeckSummaryResponse createDeck(CreateFlashcardDeckRequest request, UserDetails principal) {
        User owner = resolveUser(principal);
        
        Course course = null;
        if (request.courseId() != null) {
            course = courseRepository.findById(request.courseId())
                    .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        }
        
        Lesson lesson = null;
        if (request.lessonId() != null) {
            lesson = lessonRepository.findById(request.lessonId())
                    .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
        }

        FlashcardDeck deck = FlashcardDeck.builder()
                .title(request.title())
                .description(request.description())
                .course(course)
                .lesson(lesson)
                .teacher(owner)
                .isPublic(request.isPublic() != null ? request.isPublic() : false)
                .totalCards(0)
                .build();

        return FlashcardDeckSummaryResponse.from(flashcardDeckRepository.save(deck));
    }

    public FlashcardDeckDetailResponse addFlashcards(Long deckId, AddFlashcardsRequest request, UserDetails principal) {
        User teacher = resolveUser(principal);
        FlashcardDeck deck = flashcardDeckRepository.findById(deckId)
                .orElseThrow(() -> new AppException(ErrorCode.FLASHCARD_DECK_NOT_FOUND));
                
        checkDeckOwnership(deck, teacher);

        // ── Hard limit: max 50 cards per deck ────────────────────────────
        final int MAX_CARDS_PER_DECK = 50;
        int currentCount = deck.getTotalCards();
        int incomingCount = request.cards().size();

        if (currentCount >= MAX_CARDS_PER_DECK) {
            throw new AppException(ErrorCode.FLASHCARD_DECK_FULL);
        }

        // If adding all would exceed the limit, only take what fits
        int allowedCount = Math.min(incomingCount, MAX_CARDS_PER_DECK - currentCount);
        List<AddFlashcardsRequest.FlashcardRequest> cardsToAdd = request.cards().subList(0, allowedCount);
        // ─────────────────────────────────────────────────────────────────

        int addedCount = 0;
        int currentMaxOrder = deck.getFlashcards().stream().mapToInt(Flashcard::getOrderIndex).max().orElse(-1);
        
        for (AddFlashcardsRequest.FlashcardRequest cardRequest : cardsToAdd) {
            int orderIndex = cardRequest.orderIndex() != null ? cardRequest.orderIndex() : ++currentMaxOrder;
            
            Flashcard card = Flashcard.builder()
                    .deck(deck)
                    .frontText(cardRequest.frontText())
                    .backText(cardRequest.backText())
                    .exampleSentence(cardRequest.exampleSentence())
                    .frontImageUrl(cardRequest.frontImageUrl())
                    .backImageUrl(cardRequest.backImageUrl())
                    .audioUrl(cardRequest.audioUrl())
                    .orderIndex(orderIndex)
                    .build();
            flashcardRepository.save(card);
            deck.getFlashcards().add(card); // For response rebuilding
            addedCount++;
        }
        
        deck.setTotalCards(deck.getTotalCards() + addedCount);
        flashcardDeckRepository.save(deck);
        
        return getDeckDetail(deck.getId(), principal);
    }

    public FlashcardResponse updateFlashcard(Long flashcardId, UpdateFlashcardRequest request, UserDetails principal) {
        User teacher = resolveUser(principal);
        Flashcard card = flashcardRepository.findById(flashcardId)
                .orElseThrow(() -> new AppException(ErrorCode.FLASHCARD_NOT_FOUND));
                
        checkDeckOwnership(card.getDeck(), teacher);
        
        if (request.frontText() != null) card.setFrontText(request.frontText());
        if (request.backText() != null) card.setBackText(request.backText());
        if (request.exampleSentence() != null) card.setExampleSentence(request.exampleSentence());
        if (request.orderIndex() != null) card.setOrderIndex(request.orderIndex());
        
        Flashcard savedCard = flashcardRepository.save(card);
        UserFlashcardProgress progress = userFlashcardProgressRepository
                .findByUserIdAndFlashcardId(teacher.getId(), savedCard.getId())
                .orElse(null);
                
        return FlashcardResponse.from(savedCard, calculateStatus(progress));
    }

    public void deleteFlashcard(Long flashcardId, UserDetails principal) {
        User teacher = resolveUser(principal);
        Flashcard card = flashcardRepository.findById(flashcardId)
                .orElseThrow(() -> new AppException(ErrorCode.FLASHCARD_NOT_FOUND));
                
        FlashcardDeck deck = card.getDeck();
        checkDeckOwnership(deck, teacher);
        
        deck.setTotalCards(deck.getTotalCards() - 1);
        flashcardDeckRepository.save(deck);
        flashcardRepository.delete(card);
    }

    public void deleteDeck(Long deckId, UserDetails principal) {
        User teacher = resolveUser(principal);
        FlashcardDeck deck = flashcardDeckRepository.findById(deckId)
                .orElseThrow(() -> new AppException(ErrorCode.FLASHCARD_DECK_NOT_FOUND));

        checkDeckOwnership(deck, teacher);

        // First, delete progress associated with this deck to avoid FK violations
        userFlashcardProgressRepository.deleteByFlashcard_Deck_Id(deckId);
        
        // Then delete the deck
        flashcardDeckRepository.delete(deck);
    }
}
