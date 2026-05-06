package com.learning.englishpro.progress.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CourseProgressDetailResponse {

    private Long   courseId;
    private String courseTitle;
    private List<ChapterProgressInfo> chapters;

    @Data
    @Builder
    public static class ChapterProgressInfo {
        private Long    chapterId;
        private String  title;
        private long    completedLessons;
        private long    totalLessons;
        private boolean isCompleted;
        /** Actual IDs of lessons the user has completed in this chapter. */
        private List<Long> completedLessonIds;
    }
}

