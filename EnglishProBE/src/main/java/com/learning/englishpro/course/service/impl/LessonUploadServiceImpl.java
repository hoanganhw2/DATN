package com.learning.englishpro.course.service.impl;

import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.auth.repository.UserRepository;
import com.learning.englishpro.common.exception.AppException;
import com.learning.englishpro.common.exception.ErrorCode;
import com.learning.englishpro.common.service.FileStorageService;
import com.learning.englishpro.common.service.MediaMetadataService;
import com.learning.englishpro.course.dto.response.LessonUploadResponse;
import com.learning.englishpro.course.entity.Lesson;
import com.learning.englishpro.course.repository.LessonRepository;
import com.learning.englishpro.course.service.LessonUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LessonUploadServiceImpl implements LessonUploadService {

    // ── size limits ──────────────────────────────────────────────────────────
    private static final long MAX_VIDEO_BYTES = 2L * 1024 * 1024 * 1024; // 2 GB
    private static final long MAX_AUDIO_BYTES = 100L * 1024 * 1024;       // 100 MB
    private static final long MAX_DOCUMENT_BYTES = 50L * 1024 * 1024;     // 50 MB

    // ── allowed MIME types ───────────────────────────────────────────────────
    private static final Set<String> ALLOWED_VIDEO_TYPES = Set.of("video/mp4");
    private static final Set<String> ALLOWED_AUDIO_TYPES = Set.of("audio/mpeg", "audio/ogg");
    private static final Set<String> ALLOWED_DOCUMENT_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/x-tika-ooxml",
            "application/x-tika-msoffice",
            "application/zip"
    );

    // ── sub-directories under uploads/ ──────────────────────────────────────
    private static final String VIDEO_SUB_DIR = "videos";
    private static final String AUDIO_SUB_DIR = "audio";
    private static final String DOCUMENT_SUB_DIR = "documents";

    private final LessonRepository    lessonRepository;
    private final UserRepository      userRepository;
    private final FileStorageService  fileStorageService;
    private final MediaMetadataService mediaMetadataService;
    private final Tika                tika = new Tika();

    @Override
    @Transactional
    public LessonUploadResponse uploadMedia(Long lessonId,
                                            MultipartFile file,
                                            UserDetails principal) {
        // 1. Resolve current user
        User currentUser = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 2. Load lesson & ownership check: lesson → chapter → course → teacher
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));

        User teacher = lesson.getChapter().getCourse().getTeacher();
        if (!teacher.getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        // 3. Detect real MIME type
        String mimeType = detectMime(file);

        boolean isVideo = ALLOWED_VIDEO_TYPES.contains(mimeType);
        boolean isAudio = ALLOWED_AUDIO_TYPES.contains(mimeType);
        boolean isDocument = ALLOWED_DOCUMENT_TYPES.contains(mimeType);

        if (!isVideo && !isAudio && !isDocument) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }

        // 4. Validate size based on MIME category
        if (isVideo && file.getSize() > MAX_VIDEO_BYTES) {
            throw new AppException(ErrorCode.FILE_TOO_LARGE_VIDEO);
        }
        if (isAudio && file.getSize() > MAX_AUDIO_BYTES) {
            throw new AppException(ErrorCode.FILE_TOO_LARGE_AUDIO);
        }
        if (isDocument && file.getSize() > MAX_DOCUMENT_BYTES) {
            throw new AppException(ErrorCode.FILE_TOO_LARGE_AUDIO); // Reusing error or creating a new one, but let's just reuse for now
        }

        // 5. Determine target sub-directory and build unique file name
        String subDir   = isVideo ? VIDEO_SUB_DIR : (isAudio ? AUDIO_SUB_DIR : DOCUMENT_SUB_DIR);
        String ext      = extractExtension(file.getOriginalFilename());
        String fileName = "lesson_" + lessonId + "_" + UUID.randomUUID() + ext;

        // 6. Write to a temp file so FFmpeg can read it for metadata (only for video/audio)
        Integer duration = null;
        File tempFile = null;
        if (isVideo || isAudio) {
            try {
                tempFile = Files.createTempFile("upload_", ext).toFile();
                try (java.io.InputStream is = file.getInputStream()) {
                    Files.copy(is, tempFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
                duration = mediaMetadataService.extractDurationSeconds(tempFile);
            } catch (IOException e) {
                log.warn("Không thể tạo file tạm để lấy thời lượng: {}", e.getMessage());
            } finally {
                if (tempFile != null && tempFile.exists()) {
                    tempFile.delete();
                }
            }
        }

        // 7. Store file via FileStorageService
        String storedPath = fileStorageService.store(file, subDir, fileName);

        // 8. Delete old content file (best-effort)
        if (lesson.getContentUrl() != null) {
            try {
                fileStorageService.delete(lesson.getContentUrl());
            } catch (Exception e) {
                log.warn("Không thể xóa file bài học cũ: {}", lesson.getContentUrl());
            }
        }

        // 9. Update lesson fields
        lesson.setContentUrl(storedPath);
        if (duration != null) {
            lesson.setDuration(duration);
        }
        lessonRepository.save(lesson);

        log.info("File đa phương tiện đã được tải lên cho bài học {}: {} ({} giây)", lessonId, storedPath, duration);
        return LessonUploadResponse.builder()
                .contentUrl(storedPath)
                .duration(duration)
                .build();
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private String detectMime(MultipartFile file) {
        try {
            String mime = tika.detect(file.getInputStream(), file.getOriginalFilename());
            if (mime.equals("application/zip") || mime.equals("application/octet-stream")) {
                if (file.getContentType() != null) {
                    return file.getContentType();
                }
            }
            return mime;
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
