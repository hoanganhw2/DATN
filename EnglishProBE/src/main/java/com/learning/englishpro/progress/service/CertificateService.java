package com.learning.englishpro.progress.service;

import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.auth.entity.UserProfile;
import com.learning.englishpro.auth.repository.UserRepository;
import com.learning.englishpro.common.exception.AppException;
import com.learning.englishpro.common.exception.ErrorCode;
import com.learning.englishpro.course.entity.Course;
import com.learning.englishpro.course.repository.CourseRepository;
import com.learning.englishpro.progress.dto.response.CertificateResponse;
import com.learning.englishpro.progress.entity.Certificate;
import com.learning.englishpro.progress.entity.EnrollmentStatus;
import com.learning.englishpro.progress.entity.UserCourse;
import com.learning.englishpro.progress.repository.CertificateRepository;
import com.learning.englishpro.progress.repository.UserCourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final UserCourseRepository userCourseRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    private User resolveUser(UserDetails principal) {
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    // ── 1. Cấp chứng chỉ khi hoàn thành 100% ──────────────────────────────
    @Transactional
    public CertificateResponse issueCertificate(Long courseId, UserDetails principal) {
        User user = resolveUser(principal);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        // Kiểm tra đã có chứng chỉ chưa
        if (certificateRepository.existsByUserIdAndCourseId(user.getId(), courseId)) {
            Certificate existing = certificateRepository.findByUserIdAndCourseId(user.getId(), courseId)
                    .orElseThrow();
            return CertificateResponse.from(existing);
        }

        // Kiểm tra đã hoàn thành 100% chưa
        UserCourse userCourse = userCourseRepository
                .findByUserIdAndCourseId(user.getId(), courseId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_ENROLLED));

        if (userCourse.getStatus() != EnrollmentStatus.COMPLETED) {
            throw new AppException(ErrorCode.COURSE_NOT_COMPLETED);
        }

        // Lấy thông tin để lưu snapshot
        String studentName = "Học viên";
        UserProfile profile = user.getProfile();
        if (profile != null && profile.getFullName() != null && !profile.getFullName().isBlank()) {
            studentName = profile.getFullName();
        }

        String instructorName = "";
        User teacher = course.getTeacher();
        if (teacher != null && teacher.getProfile() != null
                && teacher.getProfile().getFullName() != null) {
            instructorName = teacher.getProfile().getFullName();
        }

        Certificate certificate = Certificate.builder()
                .user(user)
                .course(course)
                .studentName(studentName)
                .courseName(course.getTitle())
                .instructorName(instructorName)
                .issuedAt(Instant.now())
                .build();

        certificateRepository.save(certificate);
        log.info("Đã cấp chứng chỉ {} cho user {} khóa {}", certificate.getCertificateCode(), user.getId(), courseId);
        return CertificateResponse.from(certificate);
    }

    // ── 2. Lấy chứng chỉ của 1 khóa cụ thể ────────────────────────────────
    @Transactional(readOnly = true)
    public CertificateResponse getCertificate(Long courseId, UserDetails principal) {
        User user = resolveUser(principal);
        Certificate cert = certificateRepository.findByUserIdAndCourseId(user.getId(), courseId)
                .orElse(null);
        return cert != null ? CertificateResponse.from(cert) : null;
    }

    // ── 3. Lấy tất cả chứng chỉ của user ───────────────────────────────────
    @Transactional(readOnly = true)
    public List<CertificateResponse> getMyCertificates(UserDetails principal) {
        User user = resolveUser(principal);
        return certificateRepository.findAllByUserId(user.getId())
                .stream()
                .map(CertificateResponse::from)
                .toList();
    }

    // ── 4. Xác minh chứng chỉ bằng mã (public) ────────────────────────────
    @Transactional(readOnly = true)
    public CertificateResponse verifyCertificate(String code) {
        Certificate cert = certificateRepository.findByCertificateCode(code)
                .orElse(null);
        return cert != null ? CertificateResponse.from(cert) : null;
    }
}
