package com.learning.englishpro.exam.service;

import com.learning.englishpro.common.exception.AppException;
import com.learning.englishpro.common.exception.ErrorCode;
import com.learning.englishpro.common.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExamUploadService {

    private static final long MAX_FILE_BYTES = 10L * 1024 * 1024; // 10 MB for exam media

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "audio/mpeg", "audio/ogg", "audio/wav", "audio/x-wav",
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    private static final String EXAM_MEDIA_SUB_DIR = "exam_media";

    private final FileStorageService fileStorageService;
    private final Tika tika = new Tika();

    public String uploadMedia(MultipartFile file) {
        String mimeType = detectMime(file);

        if (!ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }

        if (file.getSize() > MAX_FILE_BYTES) {
            throw new AppException(ErrorCode.FILE_TOO_LARGE);
        }

        String ext = extractExtension(file.getOriginalFilename());
        String fileName = "exam_media_" + UUID.randomUUID() + ext;

        String storedPath = fileStorageService.store(file, EXAM_MEDIA_SUB_DIR, fileName);

        log.info("Media file uploaded for exam: {}", storedPath);
        return storedPath;
    }

    private String detectMime(MultipartFile file) {
        try {
            return tika.detect(file.getInputStream());
        } catch (IOException e) {
            log.warn("Cannot detect MIME, using Content-Type header");
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
