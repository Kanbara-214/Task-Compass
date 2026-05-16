package com.kanbara.taskcompass.model;

import java.util.List;

import com.kanbara.taskcompass.entity.TaskItem;

public record RecommendationResult(
		List<TaskItem> recommendedTasks,
		int availableMinutes,
		int remainingExpiredTaskCount,
		String description) {
}