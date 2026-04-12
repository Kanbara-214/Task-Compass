package com.example.demo.dto;

import java.util.Locale;

import com.example.demo.entity.TaskStatus;
import com.example.demo.model.TaskSortOption;

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
