package com.learning.englishpro.course.dto.response;

import com.learning.englishpro.course.entity.Lesson;
import com.learning.englishpro.course.entity.ContentType;
import lombok.Builder;

@Builder
public record LessonResponse(
        Long id,
        String title,
        String description,
        ContentType contentType,
        String contentUrl,
        Integer duration,
        Integer orderIndex,
        Boolean isFree
) {
    public static LessonResponse from(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .description(lesson.getDescription())
                .contentType(lesson.getContentType())
                .contentUrl(lesson.getContentUrl())
                .duration(lesson.getDuration())
                .orderIndex(lesson.getOrderIndex())
                .isFree(lesson.getIsFree())
                .build();
    }
}
