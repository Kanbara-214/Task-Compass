package com.kanbara.taskcompass.model;

import java.time.LocalDateTime;

public record RecommendationCandidate(
		Long id,
		String title,
		LocalDateTime dueDateTime,
		int importance,
		int estimatedMinutes) {

}
