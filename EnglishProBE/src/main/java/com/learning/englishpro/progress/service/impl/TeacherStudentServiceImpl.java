package com.learning.englishpro.progress.service.impl;

import com.learning.englishpro.common.exception.AppException;
import com.learning.englishpro.common.exception.ErrorCode;
import com.learning.englishpro.course.entity.Course;
import com.learning.englishpro.course.repository.CourseRepository;
import com.learning.englishpro.exercise.entity.Exercise;
import com.learning.englishpro.exercise.entity.UserExerciseAnswer;
import com.learning.englishpro.exercise.repository.ExerciseRepository;
import com.learning.englishpro.exercise.repository.UserExerciseAnswerRepository;
import com.learning.englishpro.progress.dto.response.CourseStudentDetailResponse;
import com.learning.englishpro.progress.dto.response.CourseStudentDetailResponse.ExerciseScoreItem;
import com.learning.englishpro.progress.entity.UserCourse;
import com.learning.englishpro.progress.repository.UserCourseRepository;
import com.learning.englishpro.progress.repository.UserLessonCompletionRepository;
import com.learning.englishpro.progress.service.TeacherStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeacherStudentServiceImpl implements TeacherStudentService {

    private final CourseRepository courseRepository;
    private final UserCourseRepository userCourseRepository;
    private final UserLessonCompletionRepository completionRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserExerciseAnswerRepository answerRepository;

    @Override
    public List<CourseStudentDetailResponse> getStudentsByCourse(Long courseId, Long teacherId) {
        // 1. Verify course exists and belongs to the teacher
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        // 2. Get total lessons in course
        long totalLessons = userCourseRepository.countTotalLessonsInCourse(courseId);

        // 3. Get all exercises for the course (for score lookup)
        List<Exercise> exercises = exerciseRepository.findByCourseIdWithQuestions(courseId);

        // 4. Get all enrolled students
        List<UserCourse> enrollments = userCourseRepository.findByCourseIdWithUser(courseId);

        // 5. Build response for each student
        List<CourseStudentDetailResponse> result = new ArrayList<>();

        for (UserCourse uc : enrollments) {
            Long userId = uc.getUser().getId();

            // Count completed lessons
            long completedLessons = completionRepository.countCompletedLessonsInCourse(userId, courseId);

            // Get exercise scores
            List<UserExerciseAnswer> answers = answerRepository.findByUserIdAndCourseId(userId, courseId);

            // Group answers by exerciseId
            Map<Long, List<UserExerciseAnswer>> answersByExercise = answers.stream()
                    .collect(Collectors.groupingBy(a -> a.getExercise().getId()));

            List<ExerciseScoreItem> exerciseScores = new ArrayList<>();
            for (Exercise exercise : exercises) {
                List<UserExerciseAnswer> exAnswers = answersByExercise.get(exercise.getId());
                if (exAnswers != null && !exAnswers.isEmpty()) {
                    int totalQuestions = exercise.getQuestions().size();
                    long correct = exAnswers.stream().filter(a -> Boolean.TRUE.equals(a.getIsCorrect())).count();
                    BigDecimal percent = totalQuestions > 0
                            ? BigDecimal.valueOf(correct * 100.0 / totalQuestions).setScale(1, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;

                    exerciseScores.add(ExerciseScoreItem.builder()
                            .exerciseId(exercise.getId())
                            .exerciseTitle(exercise.getTitle())
                            .lessonTitle(exercise.getLesson().getTitle())
                            .totalQuestions(totalQuestions)
                            .correctAnswers((int) correct)
                            .scorePercent(percent)
                            .submittedAt(exAnswers.stream()
                                    .map(UserExerciseAnswer::getSubmittedAt)
                                    .max(java.time.Instant::compareTo)
                                    .orElse(null))
                            .build());
                }
            }

            // Build avatar URL
            String avatarUrl = null;
            if (uc.getUser().getProfile() != null) {
                avatarUrl = uc.getUser().getProfile().getAvatarUrl();
            }

            String fullName = null;
            if (uc.getUser().getProfile() != null && uc.getUser().getProfile().getFullName() != null) {
                fullName = uc.getUser().getProfile().getFullName();
            }

            result.add(CourseStudentDetailResponse.builder()
                    .userId(userId)
                    .username(uc.getUser().getUsername())
                    .email(uc.getUser().getEmail())
                    .fullName(fullName != null ? fullName : uc.getUser().getUsername())
                    .avatarUrl(avatarUrl)
                    .enrolledAt(uc.getEnrolledAt())
                    .lastAccessedAt(uc.getLastAccessedAt())
                    .lastLoginAt(uc.getUser().getLastLoginAt())
                    .enrollmentStatus(uc.getStatus().name())
                    .progressPercent(uc.getProgressPercent())
                    .completedLessons((int) completedLessons)
                    .totalLessons((int) totalLessons)
                    .exerciseScores(exerciseScores)
                    .build());
        }

        return result;
    }
}
