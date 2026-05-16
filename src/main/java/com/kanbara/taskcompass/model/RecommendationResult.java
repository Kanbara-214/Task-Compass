package com.kanbara.taskcompass.model;

import java.util.List;

public record RecommendationResult(
		List<RecommendationCandidate> recommendedTasks,
		int availableMinutes,
		int remainingExpiredTaskCount,
		String description) {
}