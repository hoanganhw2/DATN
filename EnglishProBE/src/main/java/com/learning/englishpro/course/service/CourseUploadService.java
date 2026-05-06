package com.learning.englishpro.course.service;

import com.learning.englishpro.course.dto.response.ThumbnailUploadResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

public interface CourseUploadService {
    /**
     * Uploads a thumbnail image for the given course.
     * Validates MIME type (image/*) and size ≤ 10MB.
     * Checks that the current user is the course owner (TEACHER).
     */
    ThumbnailUploadResponse uploadThumbnail(Long courseId, MultipartFile file, UserDetails principal);
}
