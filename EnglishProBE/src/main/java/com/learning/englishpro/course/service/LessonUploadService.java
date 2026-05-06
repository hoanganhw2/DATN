package com.learning.englishpro.course.service;

import com.learning.englishpro.course.dto.response.LessonUploadResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

public interface LessonUploadService {
    /**
     * Uploads a media file (video/mp4 ≤ 2GB or audio/mpeg, audio/ogg ≤ 100MB) for the given lesson.
     * Validates MIME type and size, checks ownership (lesson → chapter → course → teacher),
     * stores the file, parses duration, and updates the lesson entity.
     */
    LessonUploadResponse uploadMedia(Long lessonId, MultipartFile file, UserDetails principal);
}
