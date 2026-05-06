package com.learning.englishpro.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradingRequest {
    private String prompt;
    private String studentEssay;
}
