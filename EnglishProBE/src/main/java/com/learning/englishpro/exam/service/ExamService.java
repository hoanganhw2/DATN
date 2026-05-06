package com.learning.englishpro.exam.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.englishpro.common.exception.AppException;
import com.learning.englishpro.common.exception.ErrorCode;
import com.learning.englishpro.course.entity.Level;
import com.learning.englishpro.exam.dto.request.CreateExamRequest;
import com.learning.englishpro.exam.dto.request.UpdateExamRequest;
import com.learning.englishpro.exam.dto.response.ExamDetailResponse;
import com.learning.englishpro.exam.dto.response.ExamSummaryResponse;
import com.learning.englishpro.exam.entity.Exam;
import com.learning.englishpro.exam.entity.ExamQuestion;
import com.learning.englishpro.exam.entity.ExamSection;
import com.learning.englishpro.exam.entity.ExamType;
import com.learning.englishpro.exam.repository.ExamQuestionRepository;
import com.learning.englishpro.exam.repository.ExamRepository;
import com.learning.englishpro.exam.repository.ExamSectionRepository;
import com.learning.englishpro.exercise.entity.QuestionType;
import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExamService {

    private final ExamRepository examRepository;
    private final ExamSectionRepository examSectionRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public Page<ExamSummaryResponse> getPublishedExams(String examTypeStr, String keyword, Pageable pageable) {
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        if (examTypeStr != null && !examTypeStr.isBlank()) {
            try {
                ExamType examType = ExamType.valueOf(examTypeStr.toUpperCase());
                if (hasKeyword) {
                    return examRepository.findByExamTypeAndIsPublishedTrueAndTitleContainingIgnoreCase(examType, keyword.trim(), pageable)
                            .map(ExamSummaryResponse::from);
                }
                return examRepository.findByExamTypeAndIsPublishedTrue(examType, pageable)
                        .map(ExamSummaryResponse::from);
            } catch (IllegalArgumentException e) {
                throw new AppException(ErrorCode.VALIDATION_FAILED);
            }
        }
        if (hasKeyword) {
            return examRepository.findByIsPublishedTrueAndTitleContainingIgnoreCase(keyword.trim(), pageable)
                    .map(ExamSummaryResponse::from);
        }
        return examRepository.findByIsPublishedTrue(pageable).map(ExamSummaryResponse::from);
    }

    @Transactional(readOnly = true)
    public ExamDetailResponse getExamById(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));
        return ExamDetailResponse.from(exam, objectMapper);
    }

    @Transactional(readOnly = true)
    public Page<ExamSummaryResponse> getMyExams(String username, Pageable pageable) {
        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return examRepository.findByCreatorId(creator.getId(), pageable)
                .map(ExamSummaryResponse::from);
    }

    public ExamSummaryResponse createExam(CreateExamRequest request, String username) {
        ExamType examType;
        try {
            examType = ExamType.valueOf(request.examType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.VALIDATION_FAILED);
        }

        Level level = null;
        if (request.level() != null && !request.level().isBlank()) {
            try {
                level = Level.valueOf(request.level().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new AppException(ErrorCode.VALIDATION_FAILED);
            }
        }

        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Exam exam = Exam.builder()
                .title(request.title())
                .description(request.description())
                .examType(examType)
                .level(level)
                .duration(request.duration())
                .totalScore(request.totalScore())
                .passingScore(request.passingScore())
                .maxAttempts(request.maxAttempts())
                .isPublished(false) // Default is false for newly created exam
                .creator(creator)
                .build();

        Exam savedExam = examRepository.save(exam);

        int sectionIdx = 0;
        for (CreateExamRequest.ExamSectionRequest sectionReq : request.sections()) {
            ExamSection section = ExamSection.builder()
                    .exam(savedExam)
                    .sectionName(sectionReq.sectionName())
                    .sectionOrder(sectionReq.sectionOrder() != null ? sectionReq.sectionOrder() : sectionIdx)
                    .duration(sectionReq.duration())
                    .totalQuestions(sectionReq.totalQuestions())
                    .build();

            ExamSection savedSection = examSectionRepository.save(section);
            
            int questionIdx = 0;
            for (CreateExamRequest.ExamQuestionRequest questionReq : sectionReq.questions()) {
                QuestionType qType;
                try {
                    qType = QuestionType.valueOf(questionReq.questionType().toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new AppException(ErrorCode.VALIDATION_FAILED);
                }

                ExamQuestion question = ExamQuestion.builder()
                        .section(savedSection)
                        .questionText(questionReq.questionText())
                        .mediaUrl(questionReq.mediaUrl())
                        .questionType(qType)
                        .options(serializeOptions(questionReq.options()))
                        .correctAnswer(questionReq.correctAnswer())
                        .explanation(questionReq.explanation())
                        .points(questionReq.points())
                        .orderIndex(questionReq.orderIndex() != null ? questionReq.orderIndex() : questionIdx)
                        .build();

                examQuestionRepository.save(question);
                questionIdx++;
            }
            sectionIdx++;
        }

        return ExamSummaryResponse.from(savedExam);
    }

    public ExamSummaryResponse updateExam(Long examId, UpdateExamRequest request, String username, Collection<? extends GrantedAuthority> authorities) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        boolean isAdmin = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && (exam.getCreator() == null || !exam.getCreator().getId().equals(user.getId()))) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        exam.setTitle(request.title());
        exam.setDescription(request.description());
        if (request.isPublished() != null) {
            exam.setIsPublished(request.isPublished());
        }
        exam.setDuration(request.duration());
        exam.setTotalScore(request.totalScore());
        exam.setPassingScore(request.passingScore());
        exam.setMaxAttempts(request.maxAttempts());

        if (request.sections() != null) {
            // Remove existing sections
            exam.getSections().clear();
            examRepository.saveAndFlush(exam);

            int sectionIdx = 0;
            for (UpdateExamRequest.ExamSectionRequest sectionReq : request.sections()) {
                ExamSection section = ExamSection.builder()
                        .exam(exam)
                        .sectionName(sectionReq.sectionName())
                        .sectionOrder(sectionReq.sectionOrder() != null ? sectionReq.sectionOrder() : sectionIdx)
                        .duration(sectionReq.duration())
                        .totalQuestions(sectionReq.totalQuestions())
                        .build();

                ExamSection savedSection = examSectionRepository.save(section);
                
                int questionIdx = 0;
                for (UpdateExamRequest.ExamQuestionRequest questionReq : sectionReq.questions()) {
                    QuestionType qType;
                    try {
                        qType = QuestionType.valueOf(questionReq.questionType().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new AppException(ErrorCode.VALIDATION_FAILED);
                    }

                    ExamQuestion question = ExamQuestion.builder()
                            .section(savedSection)
                            .questionText(questionReq.questionText())
                            .mediaUrl(questionReq.mediaUrl())
                            .questionType(qType)
                            .options(serializeOptions(questionReq.options()))
                            .correctAnswer(questionReq.correctAnswer())
                            .explanation(questionReq.explanation())
                            .points(questionReq.points())
                            .orderIndex(questionReq.orderIndex() != null ? questionReq.orderIndex() : questionIdx)
                            .build();

                    examQuestionRepository.save(question);
                    questionIdx++;
                }
                sectionIdx++;
            }
        }

        return ExamSummaryResponse.from(examRepository.save(exam));
    }

    public void deleteExam(Long examId, String username, Collection<? extends GrantedAuthority> authorities) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        boolean isAdmin = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && (exam.getCreator() == null || !exam.getCreator().getId().equals(user.getId()))) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
        
        examRepository.delete(exam);
    }

    private String serializeOptions(List<String> options) {
        if (options == null || options.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(options);
        } catch (JsonProcessingException e) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
