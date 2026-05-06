package com.learning.englishpro.exercise.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.auth.repository.UserRepository;
import com.learning.englishpro.common.exception.AppException;
import com.learning.englishpro.common.exception.ErrorCode;
import com.learning.englishpro.course.entity.Lesson;
import com.learning.englishpro.course.repository.LessonRepository;
import com.learning.englishpro.exercise.dto.request.CreateExerciseRequest;
import com.learning.englishpro.exercise.dto.request.SubmitExerciseRequest;
import com.learning.englishpro.exercise.dto.response.ExerciseDetailResponse;
import com.learning.englishpro.exercise.dto.response.ExerciseSummaryResponse;
import com.learning.englishpro.exercise.dto.response.ExerciseSubmitResponse;
import com.learning.englishpro.exercise.dto.response.TeacherExerciseDetailResponse;
import com.learning.englishpro.exercise.entity.*;
import com.learning.englishpro.exercise.repository.ExerciseQuestionRepository;
import com.learning.englishpro.exercise.repository.ExerciseRepository;
import com.learning.englishpro.exercise.repository.UserExerciseAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseQuestionRepository questionRepository;
    private final UserExerciseAnswerRepository answerRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private User resolveUser(UserDetails principal) {
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private void checkExerciseOwnership(Exercise exercise, User teacher) {
        User courseTeacher = exercise.getLesson().getChapter().getCourse().getTeacher();
        if (!courseTeacher.getId().equals(teacher.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
    }

    private List<String> parseOptions(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }

    private String serializeOptions(List<String> options) {
        if (options == null || options.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(options);
        } catch (JsonProcessingException e) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /** Map an ExerciseQuestion → QuestionResponse WITHOUT correctAnswer */
    private ExerciseDetailResponse.QuestionResponse toQuestionResponse(ExerciseQuestion q) {
        return new ExerciseDetailResponse.QuestionResponse(
                q.getId(),
                q.getQuestionText(),
                q.getMediaUrl(),
                q.getQuestionType().name(),
                parseOptions(q.getOptions()),
                q.getExplanation(),
                q.getPoints(),
                q.getOrderIndex()
        );
    }

    /** Map an ExerciseQuestion → Teacher QuestionResponse WITH correctAnswer */
    private TeacherExerciseDetailResponse.QuestionResponse toTeacherQuestionResponse(ExerciseQuestion q) {
        return new TeacherExerciseDetailResponse.QuestionResponse(
                q.getId(),
                q.getQuestionText(),
                q.getMediaUrl(),
                q.getQuestionType().name(),
                parseOptions(q.getOptions()),
                q.getExplanation(),
                q.getPoints(),
                q.getOrderIndex(),
                q.getCorrectAnswer()
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STUDENT – Read endpoints
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ExerciseSummaryResponse> listByLesson(Long lessonId) {
        // Verify lesson exists
        lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));

        return exerciseRepository.findByLessonId(lessonId)
                .stream()
                .map(ExerciseSummaryResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExerciseDetailResponse getDetail(Long exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new AppException(ErrorCode.EXERCISE_NOT_FOUND));

        List<ExerciseDetailResponse.QuestionResponse> questions = exercise.getQuestions()
                .stream()
                .map(this::toQuestionResponse)
                .collect(Collectors.toList());

        return ExerciseDetailResponse.from(exercise, questions);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STUDENT – Submit
    // ─────────────────────────────────────────────────────────────────────────

    public ExerciseSubmitResponse submit(Long exerciseId, SubmitExerciseRequest request,
                                         UserDetails principal) {
        User student = resolveUser(principal);
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new AppException(ErrorCode.EXERCISE_NOT_FOUND));

        List<ExerciseSubmitResponse.AnswerResult> results = new ArrayList<>();
        int correctCount = 0;
        BigDecimal totalScore = BigDecimal.ZERO;

        for (SubmitExerciseRequest.AnswerItem item : request.answers()) {
            ExerciseQuestion question = questionRepository.findById(item.questionId())
                    .orElseThrow(() -> new AppException(ErrorCode.EXERCISE_QUESTION_NOT_FOUND));

            String submitted = item.answerText() == null ? "" : item.answerText().trim();
            String correct   = question.getCorrectAnswer() == null ? "" : question.getCorrectAnswer().trim();

            boolean isCorrect = evaluateAnswer(question.getQuestionType(), submitted, correct);
            BigDecimal score  = isCorrect
                    ? BigDecimal.valueOf(question.getPoints() == null ? 0 : question.getPoints())
                    : BigDecimal.ZERO;

            if (isCorrect) correctCount++;
            totalScore = totalScore.add(score);

            // Upsert answer
            UserExerciseAnswer answer = answerRepository
                    .findByUserIdAndExerciseIdAndQuestionId(
                            student.getId(), exerciseId, question.getId())
                    .orElseGet(() -> UserExerciseAnswer.builder()
                            .user(student)
                            .exercise(exercise)
                            .question(question)
                            .build());

            answer.setAnswerText(item.answerText());
            answer.setIsCorrect(isCorrect);
            answer.setScore(score);
            answer.setSubmittedAt(Instant.now());
            answerRepository.save(answer);

            results.add(new ExerciseSubmitResponse.AnswerResult(
                    question.getId(),
                    question.getQuestionText(),
                    item.answerText(),
                    question.getCorrectAnswer(),  // revealed after submit
                    isCorrect,
                    score,
                    question.getExplanation()
            ));
        }

        return new ExerciseSubmitResponse(
                exerciseId,
                request.answers().size(),
                correctCount,
                totalScore,
                results
        );
    }

    /**
     * Evaluate whether an answer is correct.
     * - SINGLE_CHOICE / MULTIPLE_CHOICE : exact match (case-insensitive + trim).
     * - TEXT_INPUT                        : case-insensitive + trim.
     * - ESSAY                             : always non-auto-graded → false.
     */
    private boolean evaluateAnswer(QuestionType type, String submitted, String correct) {
        return switch (type) {
            case SINGLE_CHOICE, MULTIPLE_CHOICE, TEXT_INPUT ->
                    submitted.equalsIgnoreCase(correct);
            case ESSAY -> false;
        };
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEACHER – CRUD
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public TeacherExerciseDetailResponse getTeacherDetail(Long exerciseId, UserDetails principal) {
        User teacher = resolveUser(principal);
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new AppException(ErrorCode.EXERCISE_NOT_FOUND));

        checkExerciseOwnership(exercise, teacher);

        List<TeacherExerciseDetailResponse.QuestionResponse> questions = exercise.getQuestions()
                .stream()
                .map(this::toTeacherQuestionResponse)
                .collect(Collectors.toList());

        return TeacherExerciseDetailResponse.from(exercise, questions);
    }

    public ExerciseSummaryResponse createExercise(Long lessonId,
                                                   CreateExerciseRequest request,
                                                   UserDetails principal) {
        User teacher = resolveUser(principal);
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));

        // Ownership: lesson → chapter → course → teacher
        if (!lesson.getChapter().getCourse().getTeacher().getId().equals(teacher.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        ExerciseType exerciseType;
        try {
            exerciseType = ExerciseType.valueOf(request.type().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.VALIDATION_FAILED);
        }

        Exercise exercise = Exercise.builder()
                .lesson(lesson)
                .title(request.title())
                .description(request.description())
                .type(exerciseType)
                .totalPoints(request.totalPoints())
                .timeLimit(request.timeLimit())
                .build();

        Exercise saved = exerciseRepository.save(exercise);

        if (request.questions() != null) {
            int idx = 0;
            for (CreateExerciseRequest.QuestionRequest qr : request.questions()) {
                QuestionType qType;
                try {
                    qType = QuestionType.valueOf(qr.questionType().toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new AppException(ErrorCode.VALIDATION_FAILED);
                }

                ExerciseQuestion question = ExerciseQuestion.builder()
                        .exercise(saved)
                        .questionText(qr.questionText())
                        .mediaUrl(qr.mediaUrl())
                        .questionType(qType)
                        .options(serializeOptions(qr.options()))
                        .correctAnswer(qr.correctAnswer())
                        .explanation(qr.explanation())
                        .points(qr.points())
                        .orderIndex(qr.orderIndex() != null ? qr.orderIndex() : idx)
                        .build();
                questionRepository.save(question);
                idx++;
            }
        }

        // Reload to pick up questions
        Exercise reload = exerciseRepository.findById(saved.getId())
                .orElseThrow(() -> new AppException(ErrorCode.EXERCISE_NOT_FOUND));
        return ExerciseSummaryResponse.from(reload);
    }

    public ExerciseSummaryResponse updateExercise(Long exerciseId,
                                                   CreateExerciseRequest request,
                                                   UserDetails principal) {
        User teacher = resolveUser(principal);
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new AppException(ErrorCode.EXERCISE_NOT_FOUND));

        checkExerciseOwnership(exercise, teacher);

        ExerciseType exerciseType;
        try {
            exerciseType = ExerciseType.valueOf(request.type().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.VALIDATION_FAILED);
        }

        exercise.setTitle(request.title());
        exercise.setDescription(request.description());
        exercise.setType(exerciseType);
        exercise.setTotalPoints(request.totalPoints());
        exercise.setTimeLimit(request.timeLimit());

        // First, delete related answers to avoid foreign key constraints!
        answerRepository.deleteByExerciseId(exerciseId);

        // Replace all questions
        exercise.getQuestions().clear();
        exerciseRepository.saveAndFlush(exercise); // flush orphan removal

        if (request.questions() != null) {
            int idx = 0;
            for (CreateExerciseRequest.QuestionRequest qr : request.questions()) {
                QuestionType qType;
                try {
                    qType = QuestionType.valueOf(qr.questionType().toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new AppException(ErrorCode.VALIDATION_FAILED);
                }

                ExerciseQuestion question = ExerciseQuestion.builder()
                        .exercise(exercise)
                        .questionText(qr.questionText())
                        .mediaUrl(qr.mediaUrl())
                        .questionType(qType)
                        .options(serializeOptions(qr.options()))
                        .correctAnswer(qr.correctAnswer())
                        .explanation(qr.explanation())
                        .points(qr.points())
                        .orderIndex(qr.orderIndex() != null ? qr.orderIndex() : idx)
                        .build();
                exercise.getQuestions().add(question);
                idx++;
            }
        }

        Exercise saved = exerciseRepository.save(exercise);
        return ExerciseSummaryResponse.from(saved);
    }

    public void deleteExercise(Long exerciseId, UserDetails principal) {
        User teacher = resolveUser(principal);
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new AppException(ErrorCode.EXERCISE_NOT_FOUND));

        checkExerciseOwnership(exercise, teacher);

        exerciseRepository.delete(exercise);
    }
}
