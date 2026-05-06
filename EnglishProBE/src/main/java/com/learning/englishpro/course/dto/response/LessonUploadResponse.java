package com.learning.englishpro.course.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LessonUploadResponse {
    private String contentUrl;
    /** Duration in seconds; null if metadata could not be parsed */
    private Integer duration;
}
