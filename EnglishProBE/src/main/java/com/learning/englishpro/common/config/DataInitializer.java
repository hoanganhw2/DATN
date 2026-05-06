package com.learning.englishpro.common.config;

import com.learning.englishpro.auth.entity.Role;
import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.auth.entity.UserProfile;
import com.learning.englishpro.auth.entity.UserStatus;
import com.learning.englishpro.auth.repository.UserProfileRepository;
import com.learning.englishpro.auth.repository.UserRepository;
import com.learning.englishpro.course.entity.*;
import com.learning.englishpro.course.repository.*;
import com.learning.englishpro.exam.entity.*;
import com.learning.englishpro.exam.repository.*;
import com.learning.englishpro.flashcard.entity.*;
import com.learning.englishpro.flashcard.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final CourseRepository courseRepository;
    private final ChapterRepository chapterRepository;
    private final LessonRepository lessonRepository;
    private final ExamRepository examRepository;
    private final FlashcardDeckRepository flashcardDeckRepository;
    private final FlashcardRepository flashcardRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Dữ liệu hệ thống đã tồn tại, bỏ qua bước khởi tạo mẫu.");
            return;
        }

        log.info("Bắt đầu khởi tạo dữ liệu mẫu cho hệ thống (Cách 2)...");

        // 1. Tạo Tài khoản Admin / Teacher mặc định
        User admin = User.builder()
                .username("admin")
                .email("admin@englishpro.com")
                .password(passwordEncoder.encode("Admin@123"))
                .role(Role.ADMIN)
                .status(UserStatus.ACTIVE)
                .build();
        admin = userRepository.save(admin);

        UserProfile profile = UserProfile.builder()
                .user(admin)
                .fullName("Hệ Thống Quản Trị")
                .phone("0123456789")
                .build();
        userProfileRepository.save(profile);

        // 2. Tạo Khóa học mẫu
        Course ieltsCourse = Course.builder()
                .title("IELTS Mastery: 7.5+ Preparation")
                .slug("ielts-mastery-75-prep")
                .description("Khóa học toàn diện tập trung vào chiến thuật làm bài và từ vựng học thuật nâng cao.")
                .level(Level.ADVANCED)
                .category("IELTS")
                .price(new BigDecimal("1990000"))
                .originalPrice(new BigDecimal("2500000"))
                .thumbnailUrl("https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8")
                .teacher(admin) // <-- Bắt buộc phải có teacher
                .build();
        ieltsCourse = courseRepository.save(ieltsCourse);

        // 3. Tạo Chương và Bài học cho IELTS
        Chapter chapter1 = Chapter.builder()
                .title("Chương 1: Listening & Reading Strategies")
                .course(ieltsCourse)
                .orderIndex(1)
                .build();
        chapter1 = chapterRepository.save(chapter1);

        Lesson lesson1 = Lesson.builder()
                .title("Bài 1: Skimming and Scanning Techniques")
                .chapter(chapter1)
                .contentType(ContentType.VIDEO)
                .contentUrl("https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4")
                .duration(1200)
                .orderIndex(1)
                .isFree(true)
                .build();
        lessonRepository.save(lesson1);

        Lesson lesson2 = Lesson.builder()
                .title("Bài 2: Multiple Choice Questions in Listening")
                .chapter(chapter1)
                .contentType(ContentType.VIDEO)
                .contentUrl("https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4")
                .duration(1500)
                .orderIndex(2)
                .isFree(false)
                .build();
        lessonRepository.save(lesson2);

        // 4. Tạo Đề thi thử (Mock Test) cho IELTS
        Exam ieltsExam = Exam.builder()
                .title("IELTS Mock Test - Full Test 01")
                .examType(ExamType.IELTS)
                .level(Level.ADVANCED)
                .duration(10800) // 3 tiếng
                .totalScore(40)
                .passingScore(new BigDecimal("25.00"))
                .isPublished(true)
                .course(ieltsCourse)
                .build();
        examRepository.save(ieltsExam);

        // 5. Tạo Đề thi thử (Mock Test) cho TOEIC
        Exam toeicExam = Exam.builder()
                .title("TOEIC Practice Test - Reading & Listening 01")
                .examType(ExamType.TOEIC)
                .level(Level.INTERMEDIATE)
                .duration(7200) // 2 tiếng
                .totalScore(200) // TOEIC có 200 câu
                .passingScore(new BigDecimal("100.00"))
                .isPublished(true)
                .course(null) // Đề thi độc lập (Standalone test)
                .build();
        examRepository.save(toeicExam);

        // 5. Tạo Bộ Flashcard cho IELTS
        FlashcardDeck deck = FlashcardDeck.builder()
                .title("IELTS Advanced Academic Verbs")
                .description("Học các động từ học thuật giúp nâng band điểm Writing Task 2.")
                .course(ieltsCourse)
                .teacher(admin)
                .isPublic(true)
                .totalCards(2)
                .build();
        deck = flashcardDeckRepository.save(deck);

        Flashcard card1 = Flashcard.builder()
                .deck(deck)
                .frontText("Exacerbate")
                .backText("Làm trầm trọng thêm")
                .exampleSentence("The stock market crash exacerbated the economic crisis.")
                .orderIndex(1)
                .build();
        flashcardRepository.save(card1);

        Flashcard card2 = Flashcard.builder()
                .deck(deck)
                .frontText("Ameliorate")
                .backText("Cải thiện, làm cho tốt hơn")
                .exampleSentence("Steps have been taken to ameliorate the situation.")
                .orderIndex(2)
                .build();
        flashcardRepository.save(card2);

        log.info("Khởi tạo dữ liệu mẫu hoàn tất! ID Khóa học mẫu: {}", ieltsCourse.getId());
    }
}
