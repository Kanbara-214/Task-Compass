package com.example.demo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.demo.entity.TaskStatus;

public record TaskView(
        Long id,
        String title,
        String description,
        LocalDate dueDate,
        int importance,
        int urgency,
        int estimatedMinutes,
        String estimatedLabel,
        TaskStatus status,
        String category,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        PriorityInsight priority) {

    public boolean isDone() {
        return status == TaskStatus.DONE;
    }
}
