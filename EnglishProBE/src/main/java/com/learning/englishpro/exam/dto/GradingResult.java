package com.learning.englishpro.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradingResult {
    private Double estimatedBandScore;
    private CriteriaScores scores;
    private Feedback feedback_vi;
    private List<GrammarCorrection> correctedGrammarErrors;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CriteriaScores {
        private Double taskResponse;
        private Double coherence;
        private Double lexical;
        private Double grammar;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Feedback {
        private List<String> strengths;
        private List<String> weaknesses;
        private String advice;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrammarCorrection {
        private String originalSentence;
        private String correctedSentence;
        private String explanation_vi;
    }
}
