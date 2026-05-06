package com.learning.englishpro.progress.dto.response;

import com.learning.englishpro.progress.entity.Certificate;
import lombok.Builder;

import java.time.Instant;

@Builder
public record CertificateResponse(
        Long id,
        String certificateCode,
        String studentName,
        String courseName,
        String instructorName,
        Long courseId,
        Instant issuedAt
) {
    public static CertificateResponse from(Certificate cert) {
        return CertificateResponse.builder()
                .id(cert.getId())
                .certificateCode(cert.getCertificateCode())
                .studentName(cert.getStudentName())
                .courseName(cert.getCourseName())
                .instructorName(cert.getInstructorName())
                .courseId(cert.getCourse().getId())
                .issuedAt(cert.getIssuedAt())
                .build();
    }
}
