package com.learning.englishpro.exam.controller;

import com.learning.englishpro.common.response.ApiResponse;
import com.learning.englishpro.exam.dto.GradingRequest;
import com.learning.englishpro.exam.dto.GradingResult;
import com.learning.englishpro.exam.service.EssayAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/essays")
@RequiredArgsConstructor
public class EssayAiController {

    private final EssayAiService essayAiService;

    @PostMapping("/grade")
    public ResponseEntity<ApiResponse<GradingResult>> gradeEssay(@RequestBody GradingRequest request) {
        
        // TODO: MOCK USER ID - In production, extract user id from @AuthenticationPrincipal SecurityContext
        Long mockUserId = 1L;
        
        GradingResult result = essayAiService.gradeEssay(request, mockUserId);
        
        return ResponseEntity.ok(ApiResponse.ok(
                "Successfully graded essay",
                result
        ));
    }
}
