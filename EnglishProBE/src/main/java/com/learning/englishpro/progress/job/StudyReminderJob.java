package com.learning.englishpro.progress.job;

import com.learning.englishpro.mail.service.EmailService;
import com.learning.englishpro.progress.entity.UserCourse;
import com.learning.englishpro.progress.repository.UserCourseRepository;
import com.learning.englishpro.setting.service.AppSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class StudyReminderJob {

    private final UserCourseRepository userCourseRepository;
    private final EmailService emailService;
    private final AppSettingService appSettingService;

    // Run every day at 08:00 AM
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional(readOnly = true)
    public void sendReminders() {
        // Check if the setting is enabled
        String isEnabled = appSettingService.getSetting("notifyIdleStudents", "false");
        if (!"true".equalsIgnoreCase(isEnabled)) {
            log.info("Study reminder job is disabled in settings. Skipping.");
            return;
        }

        log.info("Starting study reminder job.");

        Instant now = Instant.now();
        Instant startTime = now.minus(8, ChronoUnit.DAYS); // Between 8 and 7 days

        Instant endTime = now.minus(7, ChronoUnit.DAYS);

        // // test
        // Instant now = Instant.now();
        // Instant startTime = now.minus(30, ChronoUnit.DAYS); // Quét rộng ra từ 30
        // ngày trước
        // Instant endTime = now.plus(1, ChronoUnit.DAYS); // Đến tận ngày mai
        List<UserCourse> idleEnrollments = userCourseRepository.findIdleEnrollments(startTime, endTime);

        int count = 0;
        for (UserCourse uc : idleEnrollments) {
            String email = uc.getUser().getEmail();
            String studentName = uc.getUser().getProfile() != null && uc.getUser().getProfile().getFullName() != null
                    ? uc.getUser().getProfile().getFullName()
                    : uc.getUser().getUsername();
            String courseName = uc.getCourse().getTitle();

            emailService.sendStudyReminderEmail(email, studentName, courseName);
            count++;
        }

        log.info("Finished study reminder job. Sent {} emails.", count);
    }
}
