package com.learning.englishpro.exam.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.englishpro.ai.service.AiService;
import com.learning.englishpro.exam.dto.GradingRequest;
import com.learning.englishpro.exam.dto.GradingResult;
import com.learning.englishpro.exam.entity.EssaySubmission;
import com.learning.englishpro.exam.repository.EssaySubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EssayAiService {

    private final AiService aiService;
    private final ObjectMapper objectMapper;
    private final EssaySubmissionRepository essaySubmissionRepository;

    private static final String IELTS_GRADING_PROMPT = """
        You are a strict and highly qualified IELTS Examiner. The user will provide a Writing Task essay prompt and the student's essay response.
        Your job is to evaluate the essay based on the official IELTS marking criteria and provide a detailed review in Vietnamese. 
        Output strictly in valid JSON format ONLY. Do not include markdown code block syntax.

        Evaluation areas:
        1. Task Response (Trọng tâm bài viết)
        2. Coherence and Cohesion (Độ mạch lạc)
        3. Lexical Resource (Từ vựng)
        4. Grammatical Range and Accuracy (Ngữ pháp)

        Output JSON Schema:
        {
          "estimatedBandScore": 6.5,
          "scores": {
            "taskResponse": 6.5,
            "coherence": 6.0,
            "lexical": 7.0,
            "grammar": 6.0
          },
          "feedback_vi": {
            "strengths": ["string"],
            "weaknesses": ["string"],
            "advice": "string"
          },
          "correctedGrammarErrors": [
            {
              "originalSentence": "string",
              "correctedSentence": "string",
              "explanation_vi": "string"
            }
          ]
        }
        """;

    /**
     * Grade an IELTS essay using AI, return the evaluation result, and persist the submission tracking.
     * 
     * @param request The essay and the prompt
     * @param userId The ID of the student submitting the essay
     * @return GradingResult mapped from AI's JSON output
     */
    @Transactional
    public GradingResult gradeEssay(GradingRequest request, Long userId) {
        log.info("Grading essay for user {} via AI...", userId);

        String userPromptText = String.format("Topic Prompt: \"%s\"\nStudent Essay: \"%s\"",
                request.getPrompt(), request.getStudentEssay());

        try {
            // Temperature set to 0.2 to lower hallucination for grading
            String rawJson = aiService.generateJson(IELTS_GRADING_PROMPT, userPromptText, 0.2);
            
            // Clean code block wrappers from JSON string if any
            rawJson = rawJson.replaceAll("```json", "").replaceAll("```", "").trim();

            GradingResult gradingResult = objectMapper.readValue(rawJson, GradingResult.class);

            // Create submission tracking entity
            EssaySubmission submission = EssaySubmission.builder()
                    .userId(userId)
                    .prompt(request.getPrompt())
                    .studentEssay(request.getStudentEssay())
                    .estimatedBandScore(gradingResult.getEstimatedBandScore())
                    .gradingResultJson(rawJson)
                    .build();

            essaySubmissionRepository.save(submission);
            
            log.info("Essay graded successfully and saved to DB with ID {}", submission.getId());
            return gradingResult;

        } catch (Exception e) {
            log.error("Failed to grade essay via AI. Exception: ", e);
            throw new RuntimeException("Failed to grade essay via AI");
        }
    }
}
