package com.learning.englishpro.setting.controller;

import com.learning.englishpro.common.response.ApiResponse;
import com.learning.englishpro.progress.job.StudyReminderJob;
import com.learning.englishpro.setting.service.AppSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
public class AppSettingController {

    private final AppSettingService appSettingService;


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> getAllSettings() {
        return ResponseEntity.ok(ApiResponse.ok(appSettingService.getAllSettings()));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> saveSettings(@RequestBody Map<String, String> payload) {
        appSettingService.saveSettings(payload);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }


}
