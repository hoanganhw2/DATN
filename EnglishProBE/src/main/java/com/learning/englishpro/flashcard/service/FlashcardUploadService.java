package com.learning.englishpro.flashcard.service;

import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.auth.repository.UserRepository;
import com.learning.englishpro.common.exception.AppException;
import com.learning.englishpro.common.exception.ErrorCode;
import com.learning.englishpro.common.service.FileStorageService;
import com.learning.englishpro.flashcard.dto.response.FlashcardUploadResponse;
import com.learning.englishpro.flashcard.entity.Flashcard;
import com.learning.englishpro.flashcard.repository.FlashcardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlashcardUploadService {

    private static final long MAX_AUDIO_BYTES = 5L * 1024 * 1024; // 5 MB

    private static final Set<String> ALLOWED_AUDIO_TYPES = Set.of(
            "audio/mpeg", "audio/ogg", "audio/wav", "audio/x-wav"
    );

    private static final String AUDIO_SUB_DIR = "flashcard_audio";

    private final FlashcardRepository flashcardRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final Tika tika = new Tika();

    @Transactional
    public FlashcardUploadResponse uploadAudio(Long flashcardId, MultipartFile file, UserDetails principal) {
        User teacher = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Flashcard flashcard = flashcardRepository.findById(flashcardId)
                .orElseThrow(() -> new AppException(ErrorCode.FLASHCARD_NOT_FOUND));

        if (!flashcard.getDeck().getTeacher().getId().equals(teacher.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        String mimeType = detectMime(file);

        if (!ALLOWED_AUDIO_TYPES.contains(mimeType)) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }

        if (file.getSize() > MAX_AUDIO_BYTES) {
            throw new AppException(ErrorCode.FILE_TOO_LARGE_AUDIO);
        }

        String ext = extractExtension(file.getOriginalFilename());
        String fileName = "flashcard_" + flashcardId + "_" + UUID.randomUUID() + ext;

        String storedPath = fileStorageService.store(file, AUDIO_SUB_DIR, fileName);

        if (flashcard.getAudioUrl() != null) {
            try {
                fileStorageService.delete(flashcard.getAudioUrl());
            } catch (Exception e) {
                log.warn("Không thể xóa file audio cũ: {}", flashcard.getAudioUrl());
            }
        }

        flashcard.setAudioUrl(storedPath);
        flashcardRepository.save(flashcard);

        log.info("File âm thanh đã được tải lên cho flashcard {}: {}", flashcardId, storedPath);
        return new FlashcardUploadResponse(flashcard.getId(), storedPath);
    }

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
