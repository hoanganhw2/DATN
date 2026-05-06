package com.learning.englishpro.course.dto.request;

import java.util.List;

/**
 * Payload for reordering chapters and lessons within a course.
 * Each item carries an ID and the new orderIndex.
 */
public record ReorderRequest(
        List<ReorderItem> chapters,
        List<ReorderItem> lessons
) {
    public record ReorderItem(Long id, int orderIndex) {}
}
