package com.learning.englishpro.common.service;

import java.io.File;

public interface MediaMetadataService {
    /**
     * Extracts the duration in seconds from a media file.
     * Returns null if the duration cannot be determined.
     */
    Integer extractDurationSeconds(File file);
}
