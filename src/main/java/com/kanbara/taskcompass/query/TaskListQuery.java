package com.kanbara.taskcompass.query;

import java.util.Locale;

import com.kanbara.taskcompass.entity.TaskStatus;
import com.kanbara.taskcompass.model.TaskSortOption;

public record TaskListQuery(
        String category,
        TaskStatus status,
        TaskSortOption sort) {

    public static TaskListQuery of(String category, String status, String sort) {
        String normalizedCategory = category == null ? "" : category.trim();
        TaskStatus normalizedStatus = parseStatus(status);
        TaskSortOption normalizedSort = TaskSortOption.from(sort);
        return new TaskListQuery(normalizedCategory, normalizedStatus, normalizedSort);
    }

    public static TaskListQuery all(TaskSortOption sort) {
        if (sort == null) {
            return new TaskListQuery("", null, TaskSortOption.RECOMMENDED);
        }
        return new TaskListQuery("", null, sort);
    }

    public boolean hasCategory() {
        return !category.isBlank();
    }

    private static TaskStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }

        try {
            return TaskStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
