package com.learning.englishpro.course.dto.response;

import com.learning.englishpro.course.entity.Chapter;
import lombok.Builder;
import java.util.List;

@Builder
public record ChapterResponse(
        Long id,
        String title,
        String description,
        Integer orderIndex,
        List<LessonResponse> lessons
) {
    public static ChapterResponse from(Chapter chapter) {
        return ChapterResponse.builder()
                .id(chapter.getId())
                .title(chapter.getTitle())
                .description(chapter.getDescription())
                .orderIndex(chapter.getOrderIndex())
                .lessons(chapter.getLessons() != null ? chapter.getLessons().stream().map(LessonResponse::from).toList() : List.of())
                .build();
    }
}
