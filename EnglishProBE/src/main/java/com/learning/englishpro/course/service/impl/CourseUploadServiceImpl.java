package com.learning.englishpro.course.service.impl;

import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.auth.repository.UserRepository;
import com.learning.englishpro.common.exception.AppException;
import com.learning.englishpro.common.exception.ErrorCode;
import com.learning.englishpro.common.service.FileStorageService;
import com.learning.englishpro.course.dto.response.ThumbnailUploadResponse;
import com.learning.englishpro.course.entity.Course;
import com.learning.englishpro.course.repository.CourseRepository;
import com.learning.englishpro.course.service.CourseUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseUploadServiceImpl implements CourseUploadService {

    private static final long   MAX_THUMBNAIL_BYTES = 10L * 1024 * 1024; // 10 MB
    private static final String THUMBNAIL_SUB_DIR   = "thumbnails";

    private final CourseRepository    courseRepository;
    private final UserRepository      userRepository;
    private final FileStorageService  fileStorageService;
    private final Tika                tika = new Tika();

    @Override
    @Transactional
    public ThumbnailUploadResponse uploadThumbnail(Long courseId,
                                                   MultipartFile file,
                                                   UserDetails principal) {
        // 1. Resolve current user
        User currentUser = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 2. Load course & check ownership
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (!course.getTeacher().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        // 3. Validate size
        if (file.getSize() > MAX_THUMBNAIL_BYTES) {
            throw new AppException(ErrorCode.FILE_TOO_LARGE_THUMBNAIL);
        }

        // 4. Validate MIME type (must be image/*)
        String mimeType = detectMime(file);
        if (!mimeType.startsWith("image/")) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }

        // 5. Build a unique file name and store
        String ext      = extractExtension(file.getOriginalFilename());
        String fileName = "course_" + courseId + "_" + UUID.randomUUID() + ext;
        String storedPath = fileStorageService.store(file, THUMBNAIL_SUB_DIR, fileName);

        // 6. Delete old thumbnail (best-effort)
        if (course.getThumbnailUrl() != null) {
            try {
                fileStorageService.delete(course.getThumbnailUrl());
            } catch (Exception e) {
                log.warn("Không thể xóa ảnh thu nhỏ cũ: {}", course.getThumbnailUrl());
            }
        }

        // 7. Persist URL
        course.setThumbnailUrl(storedPath);
        courseRepository.save(course);

        log.info("Ảnh thu nhỏ đã được tải lên cho khóa học {}: {}", courseId, storedPath);
        return ThumbnailUploadResponse.builder()
                .thumbnailUrl(storedPath)
                .build();
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private String detectMime(MultipartFile file) {
        try {
            return tika.detect(file.getInputStream());
        } catch (IOException e) {
            log.warn("Không thể nhận diện MIME, sử dụng Content-Type header");
            return file.getContentType() != null ? file.getContentType() : "application/octet-stream";
        }
    }

    private String extractExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "";
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.'));
    }
}
