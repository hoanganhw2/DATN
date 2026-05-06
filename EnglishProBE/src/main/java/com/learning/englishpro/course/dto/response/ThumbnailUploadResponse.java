package com.learning.englishpro.course.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ThumbnailUploadResponse {
    private String thumbnailUrl;
}
