package com.kanbara.taskcompass.model;

import java.time.LocalDateTime;

import com.kanbara.taskcompass.entity.TaskStatus;

public record TaskView(Long id, String title, String description, LocalDateTime dueDateTime, int importance,
		int urgency, int estimatedMinutes, String estimatedLabel, TaskStatus status, String category,
		LocalDateTime createdAt, LocalDateTime updatedAt) {

	public boolean isDone() {
		return status == TaskStatus.DONE;
	}
}
