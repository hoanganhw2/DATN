package com.learning.englishpro.exam.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.auth.repository.UserRepository;
import com.learning.englishpro.common.exception.AppException;
import com.learning.englishpro.common.exception.ErrorCode;
import com.learning.englishpro.exam.dto.request.SubmitExamRequest;
import com.learning.englishpro.exam.dto.response.ExamResultDetailResponse;
import com.learning.englishpro.exam.dto.response.ExamResultSummaryResponse;
import com.learning.englishpro.exam.dto.response.StartExamResponse;
import com.learning.englishpro.exam.dto.response.SubmitExamResponse;
import com.learning.englishpro.exam.entity.*;
import com.learning.englishpro.exam.repository.ExamRepository;
import com.learning.englishpro.exam.repository.UserExamAnswerRepository;
import com.learning.englishpro.exam.repository.UserExamResultRepository;
import com.learning.englishpro.exercise.entity.QuestionType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserExamService {

    private final ExamRepository examRepository;
    private final UserExamResultRepository userExamResultRepository;
    private final UserExamAnswerRepository userExamAnswerRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    private User resolveUser(UserDetails principal) {
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private List<String> parseOptions(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }

    public StartExamResponse startExam(Long examId, UserDetails principal) {
        User user = resolveUser(principal);
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        if (!exam.getIsPublished()) {
            throw new AppException(ErrorCode.EXAM_NOT_PUBLISHED);
        }

        // Check if there is already an IN_PROGRESS exam
        Optional<UserExamResult> inProgressOpt = userExamResultRepository
                .findFirstByUserIdAndExamIdAndStatus(user.getId(), exam.getId(), ExamResultStatus.IN_PROGRESS);

        UserExamResult result;
        if (inProgressOpt.isPresent()) {
            result = inProgressOpt.get();
        } else {
            // Check max attempts
            if (exam.getMaxAttempts() != null) {
                int attempts = userExamResultRepository.countByUserIdAndExamId(user.getId(), exam.getId());
                if (attempts >= exam.getMaxAttempts()) {
                    throw new AppException(ErrorCode.EXAM_MAX_ATTEMPTS_EXCEEDED);
                }
            }

            int attemptNumber = userExamResultRepository.countByUserIdAndExamId(user.getId(), exam.getId()) + 1;
            Instant startTime = Instant.now();
            Instant endTime = exam.getDuration() != null ? startTime.plus(exam.getDuration(), ChronoUnit.SECONDS) : null;

            result = UserExamResult.builder()
                    .user(user)
                    .exam(exam)
                    .attemptNumber(attemptNumber)
                    .startTime(startTime)
                    .endTime(endTime)
                    .status(ExamResultStatus.IN_PROGRESS)
                    .build();
            result = userExamResultRepository.save(result);
        }

        // Build response WITH submitted answers if resuming, but NEVER correctAnswers
        Map<Long, String> submittedAnswers = result.getAnswers().stream()
                .collect(Collectors.toMap(a -> a.getQuestion().getId(), UserExamAnswer::getAnswerText));

        List<StartExamResponse.SectionResponse> sectionResponses = new ArrayList<>();
        for (ExamSection section : exam.getSections()) {
            List<StartExamResponse.QuestionResponse> questionResponses = new ArrayList<>();
            for (ExamQuestion question : section.getQuestions()) {
                questionResponses.add(new StartExamResponse.QuestionResponse(
                        question.getId(),
                        question.getQuestionText(),
                        question.getMediaUrl(),
                        question.getQuestionType().name(),
                        parseOptions(question.getOptions()),
                        question.getPoints(),
                        question.getOrderIndex(),
                        submittedAnswers.get(question.getId())
                ));
            }
            sectionResponses.add(new StartExamResponse.SectionResponse(
                    section.getId(),
                    section.getSectionName(),
                    section.getSectionOrder(),
                    section.getDuration(),
                    questionResponses
            ));
        }

        return new StartExamResponse(
                result.getId(),
                result.getStartTime(),
                result.getEndTime(),
                sectionResponses
        );
    }

    public SubmitExamResponse submitExam(Long resultId, SubmitExamRequest request, UserDetails principal) {
        User user = resolveUser(principal);
        UserExamResult result = userExamResultRepository.findById(resultId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_RESULT_NOT_FOUND));

        if (!result.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        if (result.getStatus() != ExamResultStatus.IN_PROGRESS) {
            throw new AppException(ErrorCode.EXAM_ALREADY_SUBMITTED);
        }

        Exam exam = result.getExam();
        Instant now = Instant.now();
        List<SubmitExamRequest.AnswerItem> answersToProcess = request.answers();

        // Timeout check check: if now() > deadline, we still grade what they submitted.
        // It's possible we only accept answers if they were sent before deadline, but standard behavior
        // is to grade what was provided.
        // (If strictly dropping answers past deadline: answersToProcess = now.isAfter(result.getEndTime()) ? new ArrayList<>() : request.answers(); )

        BigDecimal totalScore = BigDecimal.ZERO;
        List<SubmitExamResponse.SectionResult> sectionResults = new ArrayList<>();

        Map<Long, String> requestAnswersMap = answersToProcess.stream()
                .collect(Collectors.toMap(SubmitExamRequest.AnswerItem::questionId, SubmitExamRequest.AnswerItem::answerText, (a, b) -> a));

        for (ExamSection section : exam.getSections()) {
            int sectionCorrect = 0;
            int sectionTotal = section.getQuestions().size();
            BigDecimal sectionScore = BigDecimal.ZERO;

            for (ExamQuestion question : section.getQuestions()) {
                String submittedAnswer = requestAnswersMap.get(question.getId());
                
                // Existing answer if any
                UserExamAnswer answer = userExamAnswerRepository.findByExamResultIdAndQuestionId(result.getId(), question.getId())
                        .orElse(UserExamAnswer.builder()
                                .examResult(result)
                                .question(question)
                                .build());

                answer.setAnswerText(submittedAnswer);

                boolean isCorrect = false;
                BigDecimal pointsEarned = BigDecimal.ZERO;

                if (submittedAnswer != null && !submittedAnswer.isBlank()) {
                    if (question.getQuestionType() == QuestionType.ESSAY) {
                        answer.setIsCorrect(null); // needs manual grading
                        pointsEarned = BigDecimal.ZERO;
                    } else {
                        String correct = question.getCorrectAnswer();
                        isCorrect = submittedAnswer.trim().equalsIgnoreCase(correct != null ? correct.trim() : "");
                        answer.setIsCorrect(isCorrect);
                        if (isCorrect) {
                            pointsEarned = BigDecimal.valueOf(question.getPoints() != null ? question.getPoints() : 0);
                            sectionCorrect++;
                        }
                    }
                } else {
                    answer.setIsCorrect(false);
                }

                answer.setPointsEarned(pointsEarned);
                userExamAnswerRepository.save(answer);

                sectionScore = sectionScore.add(pointsEarned);
            }

            sectionResults.add(new SubmitExamResponse.SectionResult(
                    section.getSectionName(),
                    sectionCorrect,
                    sectionTotal,
                    sectionScore
            ));
            totalScore = totalScore.add(sectionScore);
        }

        result.setScore(totalScore);
        result.setSubmittedAt(now);
        result.setStatus(ExamResultStatus.SUBMITTED);
        if (exam.getPassingScore() != null) {
            result.setIsPassed(totalScore.compareTo(exam.getPassingScore()) >= 0);
        }
        userExamResultRepository.save(result);

        return new SubmitExamResponse(
                result.getId(),
                totalScore,
                result.getIsPassed(),
                result.getSubmittedAt(),
                sectionResults
        );
    }

    @Transactional(readOnly = true)
    public ExamResultDetailResponse getResultDetail(Long resultId, UserDetails principal) {
        User user = resolveUser(principal);
        UserExamResult result = userExamResultRepository.findById(resultId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_RESULT_NOT_FOUND));

        if (!result.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        if (result.getStatus() == ExamResultStatus.IN_PROGRESS) {
            // Typically shouldn't view detail (with correct answers) if still IN_PROGRESS
            throw new AppException(ErrorCode.VALIDATION_FAILED); // or custom not submitted yet
        }

        Map<Long, UserExamAnswer> answersMap = result.getAnswers().stream()
                .collect(Collectors.toMap(a -> a.getQuestion().getId(), a -> a));

        List<ExamResultDetailResponse.SectionDetail> sectionDetails = new ArrayList<>();
        for (ExamSection section : result.getExam().getSections()) {
            List<ExamResultDetailResponse.QuestionDetail> questionDetails = new ArrayList<>();
            for (ExamQuestion question : section.getQuestions()) {
                UserExamAnswer answer = answersMap.get(question.getId());
                questionDetails.add(new ExamResultDetailResponse.QuestionDetail(
                        question.getId(),
                        question.getQuestionText(),
                        question.getMediaUrl(),
                        question.getQuestionType().name(),
                        parseOptions(question.getOptions()),
                        question.getCorrectAnswer(),
                        question.getExplanation(),
                        question.getPoints(),
                        answer != null ? answer.getAnswerText() : null,
                        answer != null ? answer.getIsCorrect() : null,
                        answer != null ? answer.getPointsEarned() : null
                ));
            }
            sectionDetails.add(new ExamResultDetailResponse.SectionDetail(
                    section.getId(),
                    section.getSectionName(),
                    questionDetails
            ));
        }

        return new ExamResultDetailResponse(
                result.getId(),
                result.getExam().getId(),
                result.getExam().getTitle(),
                result.getAttemptNumber(),
                result.getStartTime(),
                result.getEndTime(),
                result.getScore(),
                result.getIsPassed(),
                result.getStatus(),
                result.getSubmittedAt(),
                sectionDetails
        );
    }

    @Transactional(readOnly = true)
    public Page<ExamResultSummaryResponse> getHistory(Long examId, Pageable pageable, UserDetails principal) {
        User user = resolveUser(principal);
        return userExamResultRepository.findByUserIdAndExamIdOrderByStartTimeDesc(user.getId(), examId, pageable)
                .map(ExamResultSummaryResponse::from);
    }
}
