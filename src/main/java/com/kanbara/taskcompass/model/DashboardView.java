package com.kanbara.taskcompass.model;

import java.util.List;

public record DashboardView(
		RecommendationResult recommendationResult,
		List<TaskView> overdueTasks,
		int totalCount,
		int openCount,
		int inProgressCount,
		int doneCount,
		int completionRate) {
}
