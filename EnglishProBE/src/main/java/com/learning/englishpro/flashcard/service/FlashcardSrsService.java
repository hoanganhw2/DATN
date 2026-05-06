package com.learning.englishpro.flashcard.service;

import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.auth.repository.UserRepository;
import com.learning.englishpro.common.exception.AppException;
import com.learning.englishpro.common.exception.ErrorCode;
import com.learning.englishpro.flashcard.dto.request.FlashcardReviewRequest;
import com.learning.englishpro.flashcard.dto.response.DeckProgressResponse;
import com.learning.englishpro.flashcard.dto.response.FlashcardReviewTodayResponse;
import com.learning.englishpro.flashcard.entity.*;
import com.learning.englishpro.flashcard.repository.FlashcardDeckRepository;
import com.learning.englishpro.flashcard.repository.FlashcardRepository;
import com.learning.englishpro.flashcard.repository.UserDeckProgressRepository;
import com.learning.englishpro.flashcard.repository.UserFlashcardProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FlashcardSrsService {

    private final FlashcardDeckRepository flashcardDeckRepository;
    private final FlashcardRepository flashcardRepository;
    private final UserFlashcardProgressRepository userFlashcardProgressRepository;
    private final UserDeckProgressRepository userDeckProgressRepository;
    private final UserRepository userRepository;

    private User resolveUser(UserDetails principal) {
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public FlashcardReviewTodayResponse getReviewToday(Long deckId, UserDetails principal) {
        User user = resolveUser(principal);
        FlashcardDeck deck = flashcardDeckRepository.findByIdWithFlashcards(deckId)
                .orElseThrow(() -> new AppException(ErrorCode.FLASHCARD_DECK_NOT_FOUND));

        LocalDate today = LocalDate.now();

        // Lấy tất cả progress của deck mà nextReviewDate <= today hoặc chưa có
        // nextReviewDate
        List<UserFlashcardProgress> dueProgresses = userFlashcardProgressRepository.findDueCards(user.getId(),
                deck.getId(), today);
        Map<Long, UserFlashcardProgress> progressMap = dueProgresses.stream()
                .collect(Collectors.toMap(p -> p.getFlashcard().getId(), p -> p));

        int newCount = 0;
        int dueCount = 0;

        List<FlashcardReviewTodayResponse.CardToReview> cardsToReview = new ArrayList<>();

        for (Flashcard card : deck.getFlashcards()) {
            UserFlashcardProgress progress = progressMap.get(card.getId());

            boolean isNew = false;
            if (progress == null) {
                isNew = true;
            } else if (progress.getLastReviewDate() == null || !progress.getLastReviewDate().equals(today)) {
                if (progress.getRepetitions() == 0) {
                    isNew = true;
                }
            }

            if (progress != null && progress.getNextReviewDate() != null
                    && progress.getNextReviewDate().isAfter(today)) {
                continue; // Not due today, just in case
            }

            if (isNew) {
                if (newCount < 20) {
                    newCount++;
                    cardsToReview.add(buildCardToReview(card, null, true));
                }
            } else {
                dueCount++;
                cardsToReview.add(buildCardToReview(card, progress, false));
            }
        }

        // Sắp xếp: quá hạn trước (nextReviewDate null -> mới -> để sau, có date -> sort
        // by date asc), sau đó đến thẻ mới
        cardsToReview.sort((c1, c2) -> {
            LocalDate d1 = c1.progress() != null ? c1.progress().nextReviewDate() : null;
            LocalDate d2 = c2.progress() != null ? c2.progress().nextReviewDate() : null;

            if (d1 != null && d2 != null) {
                return d1.compareTo(d2);
            } else if (d1 != null) {
                return -1; // quá hạn lên trước thẻ mới
            } else if (d2 != null) {
                return 1;
            } else {
                return 0;
            }
        });

        int totalToReview = newCount + dueCount;

        return new FlashcardReviewTodayResponse(dueCount, newCount, totalToReview, cardsToReview);
    }

    private FlashcardReviewTodayResponse.CardToReview buildCardToReview(Flashcard card, UserFlashcardProgress progress,
            boolean isNew) {
        FlashcardReviewTodayResponse.ProgressSummary summary = null;
        if (progress != null) {
            summary = new FlashcardReviewTodayResponse.ProgressSummary(
                    progress.getIntervalDays(),
                    progress.getNextReviewDate(),
                    progress.getLastGrade() != null ? progress.getLastGrade().name() : null);
        }

        return new FlashcardReviewTodayResponse.CardToReview(
                card.getId(),
                card.getFrontText(),
                card.getBackText(),
                card.getAudioUrl(),
                isNew,
                summary);
    }

    public void reviewFlashcard(Long flashcardId, FlashcardReviewRequest request, UserDetails principal) {
        User user = resolveUser(principal);
        Flashcard card = flashcardRepository.findById(flashcardId)
                .orElseThrow(() -> new AppException(ErrorCode.FLASHCARD_NOT_FOUND));

        FlashcardGrade grade = FlashcardGrade.valueOf(request.grade().toUpperCase());

        UserFlashcardProgress progress = userFlashcardProgressRepository
                .findByUserIdAndFlashcardId(user.getId(), card.getId())
                .orElseGet(() -> UserFlashcardProgress.builder()
                        .user(user)
                        .flashcard(card)
                        .build());

        // Default SM-2
        int rep = progress.getRepetitions();
        BigDecimal ef = progress.getEaseFactor();
        int interval = progress.getIntervalDays();
        int streak = progress.getCorrectStreak();

        switch (grade) {
            case AGAIN:
                rep = 0;
                interval = 1;
                streak = 0;
                break;
            case HARD:
                interval = Math.max(1, (int) Math.round(interval * 1.2));
                ef = ef.subtract(new BigDecimal("0.15")).max(new BigDecimal("1.30"));
                streak = 0;
                break;
            case GOOD:
                if (rep == 0) {
                    interval = 1;
                } else if (rep == 1) {
                    interval = 6;
                } else {
                    interval = (int) Math.round(interval * ef.doubleValue());
                }
                rep++;
                streak++;
                break;
            case EASY:
                // Nếu chọn Dễ (EASY), nhảy thẳng lên 21 ngày (Thành thạo) nếu đang ở giai đoạn
                // học
                if (interval < 21) {
                    interval = 21;
                } else {
                    interval = (int) Math.round(interval * ef.doubleValue() * 1.3);
                }
                ef = ef.add(new BigDecimal("0.15")).min(new BigDecimal("4.00"));
                rep++;
                streak++;
                break;
        }

        progress.setRepetitions(rep);
        progress.setEaseFactor(ef);
        progress.setIntervalDays(interval);
        progress.setCorrectStreak(streak);
        progress.setLastGrade(grade);
        progress.setTotalReviews(progress.getTotalReviews() + 1);
        progress.setLastReviewDate(LocalDate.now());
        progress.setNextReviewDate(LocalDate.now().plusDays(interval));

        userFlashcardProgressRepository.save(progress);

        // Update Deck Progress
        FlashcardDeck deck = card.getDeck();
        UserDeckProgress.UserDeckProgressId deckProgressId = new UserDeckProgress.UserDeckProgressId(user.getId(),
                deck.getId());
        UserDeckProgress deckProgress = userDeckProgressRepository.findById(deckProgressId)
                .orElseGet(() -> UserDeckProgress.builder()
                        .user(user)
                        .deck(deck)
                        .totalCards(deck.getTotalCards())
                        .build());

        deckProgress.setTotalCards(deck.getTotalCards());
        deckProgress.setLastStudiedAt(Instant.now());

        // Update learned & mastered counts
        int learned = userFlashcardProgressRepository.countLearnedCards(user.getId(), deck.getId());
        int mastered = userFlashcardProgressRepository.countMasteredCards(user.getId(), deck.getId(), FlashcardGrade.EASY);

        deckProgress.setLearnedCards(learned);
        deckProgress.setMasteredCards(mastered);

        userDeckProgressRepository.save(deckProgress);
    }

    public void updateDeckProgressStats(Long userId, Long deckId) {
        UserDeckProgress.UserDeckProgressId deckProgressId = new UserDeckProgress.UserDeckProgressId(userId, deckId);
        userDeckProgressRepository.findById(deckProgressId).ifPresent(deckProgress -> {
            int learned = userFlashcardProgressRepository.countLearnedCards(userId, deckId);
            int mastered = userFlashcardProgressRepository.countMasteredCards(userId, deckId, FlashcardGrade.EASY);
            deckProgress.setLearnedCards(learned);
            deckProgress.setMasteredCards(mastered);
            userDeckProgressRepository.save(deckProgress);
        });
    }

    @Transactional(readOnly = true)
    public DeckProgressResponse getDeckProgress(Long deckId, UserDetails principal) {
        User user = resolveUser(principal);
        FlashcardDeck deck = flashcardDeckRepository.findById(deckId)
                .orElseThrow(() -> new AppException(ErrorCode.FLASHCARD_DECK_NOT_FOUND));

        UserDeckProgress.UserDeckProgressId dpId = new UserDeckProgress.UserDeckProgressId(user.getId(), deck.getId());
        UserDeckProgress dp = userDeckProgressRepository.findById(dpId)
                .orElseGet(() -> UserDeckProgress.builder()
                        .user(user)
                        .deck(deck)
                        .totalCards(deck.getTotalCards())
                        .build());

        int learned = userFlashcardProgressRepository.countLearnedCards(user.getId(), deck.getId());
        int mastered = userFlashcardProgressRepository.countMasteredCards(user.getId(), deck.getId(), FlashcardGrade.EASY);
        int totalDue = userFlashcardProgressRepository.countTotalDueCards(user.getId(), deck.getId(), LocalDate.now());

        // Update the progress table if it exists as a side effect (optional but good for consistency)
        dp.setLearnedCards(learned);
        dp.setMasteredCards(mastered);
        dp.setTotalCards(deck.getTotalCards());
        userDeckProgressRepository.save(dp);

        return new DeckProgressResponse(
                deck.getTotalCards(),
                deck.getTotalCards() - learned,
                learned,
                mastered,
                totalDue,
                dp.getLastStudiedAt());
    }
}
