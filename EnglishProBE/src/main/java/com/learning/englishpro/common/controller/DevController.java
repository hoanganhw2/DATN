package com.learning.englishpro.common.controller;

import com.learning.englishpro.auth.entity.Role;
import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.auth.entity.UserProfile;
import com.learning.englishpro.auth.repository.UserProfileRepository;
import com.learning.englishpro.auth.repository.UserRepository;
import com.learning.englishpro.course.entity.Chapter;
import com.learning.englishpro.course.entity.ContentType;
import com.learning.englishpro.course.entity.Lesson;
import com.learning.englishpro.course.repository.ChapterRepository;
import com.learning.englishpro.course.repository.LessonRepository;
import com.learning.englishpro.course.entity.Course;
import com.learning.englishpro.course.entity.Level;
import com.learning.englishpro.course.repository.CourseRepository;
import com.learning.englishpro.exam.entity.Exam;
import com.learning.englishpro.exam.entity.ExamType;
import com.learning.englishpro.exam.repository.ExamRepository;
import com.learning.englishpro.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/dev")
@RequiredArgsConstructor
@Slf4j
public class DevController {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final CourseRepository courseRepository;
    private final ChapterRepository chapterRepository;
    private final LessonRepository lessonRepository;
    private final ExamRepository examRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/seed")
    @Transactional
    public ResponseEntity<ApiResponse<String>> seedFakeData(
            @RequestParam(defaultValue = "20") int usersCount,
            @RequestParam(defaultValue = "15") int coursesCount,
            @RequestParam(defaultValue = "10") int examsCount) {

        String encodedPassword = passwordEncoder.encode("123456");
        Random random = new Random();

        // Arrays for fake generation
        String[] firstNames = {"Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Huỳnh", "Phan", "Vũ", "Võ", "Đặng"};
        String[] lastNames = {"Anh", "Minh", "Bảo", "Linh", "Trang", "Dũng", "Sơn", "Mai", "Cường", "Hùng"};
        String[] courseAdjectives = {"Cơ bản", "Nâng cao", "Thực chiến", "Toàn diện", "Cấp tốc", "Chuyên sâu"};
        String[] courseTopics = {"IELTS Reading", "IELTS Writing", "IELTS Speaking", "TOEIC Listening", "Giao tiếp English", "Ngữ pháp"};

        List<User> teachers = new ArrayList<>();

        // 1. Generate Users
        log.info("Bắt đầu tạo {} người dùng...", usersCount);
        for (int i = 0; i < usersCount; i++) {
            String uuid = UUID.randomUUID().toString().substring(0, 8);
            String uName = "user_" + uuid;
            boolean isTeacher = random.nextInt(10) > 7; // 20% are teachers

            User user = User.builder()
                    .username(uName)
                    .email(uName + "@example.com")
                    .password(encodedPassword)
                    .role(isTeacher ? Role.TEACHER : Role.STUDENT)
                    .build();
            user = userRepository.save(user);

            if (isTeacher) {
                teachers.add(user);
            }

            UserProfile profile = UserProfile.builder()
                    .user(user)
                    .fullName(firstNames[random.nextInt(firstNames.length)] + " " + lastNames[random.nextInt(lastNames.length)])
                    .phone("09" + (10000000 + random.nextInt(90000000)))
                    .build();
            userProfileRepository.save(profile);
        }

        // Make sure we have at least 1 teacher
        if (teachers.isEmpty()) {
            User teacher = User.builder()
                    .username("teacher_fallback")
                    .email("teacher_fallback@example.com")
                    .password(encodedPassword)
                    .role(Role.TEACHER)
                    .build();
            teacher = userRepository.save(teacher);
            teachers.add(teacher);
        }

        // 2. Generate Courses
        log.info("Bắt đầu tạo {} khóa học...", coursesCount);
        List<Course> createdCourses = new ArrayList<>();
        for (int i = 0; i < coursesCount; i++) {
            String topic = courseTopics[random.nextInt(courseTopics.length)];
            String adj = courseAdjectives[random.nextInt(courseAdjectives.length)];
            String title = "Khóa học " + topic + " " + adj + " " + UUID.randomUUID().toString().substring(0, 4);
            String slug = "khoa-hoc-" + UUID.randomUUID().toString().substring(0, 8);

            Course course = Course.builder()
                    .title(title)
                    .slug(slug)
                    .description("Đây là mô tả tự động cho khóa học " + title + ". Giúp học viên cải thiện trình độ nhanh chóng.")
                    .level(Level.values()[random.nextInt(Level.values().length)])
                    .category(topic.split(" ")[0]) // IELTS, TOEIC, v.v.
                    .price(new BigDecimal((random.nextInt(20) + 1) * 100000)) // 100k - 2000k
                    .isPublished(random.nextBoolean())
                    .teacher(teachers.get(random.nextInt(teachers.size())))
                    .build();
            Course savedCourse = courseRepository.save(course);
            
            // 2.1 Generate Chapters for each course
            int chaptersInCourse = random.nextInt(3) + 3; // 3-5 chapters
            int totalLessons = 0;
            int totalDuration = 0;
            
            for (int j = 0; j < chaptersInCourse; j++) {
                Chapter chapter = Chapter.builder()
                        .course(savedCourse)
                        .title("Chương " + (j + 1) + ": " + topic + " " + adj)
                        .description("Mô tả chi tiết cho chương học số " + (j + 1))
                        .orderIndex(j)
                        .build();
                Chapter savedChapter = chapterRepository.save(chapter);
                
                // 2.2 Generate Lessons for each chapter
                int lessonsInChapter = random.nextInt(3) + 2; // 2-4 lessons
                for (int k = 0; k < lessonsInChapter; k++) {
                    int duration = (random.nextInt(20) + 5) * 60; // 5-25 minutes
                    Lesson lesson = Lesson.builder()
                            .chapter(savedChapter)
                            .title("Bài học " + (k + 1) + ": Kiến thức " + topic + " " + (k + 1))
                            .description("Nội dung bài học giúp bạn nắm vững " + topic)
                            .contentType(ContentType.VIDEO)
                            .contentUrl("https://www.youtube.com/watch?v=kJQP7kiw9Fk")
                            .duration(duration)
                            .orderIndex(k)
                            .isFree(j == 0 && k == 0) // First lesson of first chapter is free
                            .build();
                    lessonRepository.save(lesson);
                    totalLessons++;
                    totalDuration += duration;
                }
            }
            
            // Update cached fields
            savedCourse.setTotalLessons(totalLessons);
            savedCourse.setTotalDurationSecs(totalDuration);
            courseRepository.save(savedCourse);
            
            createdCourses.add(savedCourse);
        }

        // 3. Generate Exams
        log.info("Bắt đầu tạo {} đề thi...", examsCount);
        for (int i = 0; i < examsCount; i++) {
            String title = "Đề thi thử " + (random.nextBoolean() ? "IELTS" : "TOEIC") + " số " + (random.nextInt(99) + 1);
            
            Course linkedCourse = null;
            if (random.nextBoolean() && !createdCourses.isEmpty()) {
                linkedCourse = createdCourses.get(random.nextInt(createdCourses.size()));
            }

            Exam exam = Exam.builder()
                    .title(title)
                    .description("Bài thi trắc nghiệm đánh giá năng lực tự động tạo.")
                    .examType(random.nextBoolean() ? ExamType.IELTS : ExamType.TOEIC)
                    .level(Level.values()[random.nextInt(Level.values().length)])
                    .duration((random.nextInt(3) + 1) * 3600) // 1 - 3 hours
                    .totalScore(100)
                    .passingScore(new BigDecimal("50.00"))
                    .isPublished(true)
                    .course(linkedCourse)
                    .build();
            examRepository.save(exam);
        }

        log.info("Fake dữ liệu hoàn tất!");
        return ResponseEntity.ok(ApiResponse.ok("Tạo thành công " + usersCount + " users, " + coursesCount + " courses, " + examsCount + " exams.", null));
    }
}
