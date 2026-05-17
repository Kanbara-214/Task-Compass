package com.kanbara.taskcompass.model;

import java.time.LocalDateTime;

public record RecommendationCandidate(
		Long id,
		String title,
		LocalDateTime dueDateTime,
		int importance,
		int estimatedMinutes) {

	public int calculateValue(LocalDateTime now, int availableMinutes ) {
		LocalDateTime dueDateTime = this.dueDateTime();
		
		if (dueDateTime.isBefore(now)) {
			return this.importance() * 2;
		} else if (!dueDateTime.isAfter(now.plusMinutes(availableMinutes ))) {
			return this.importance() * 2;
		} else {
			return this.importance();
		}
	}

	public LocalDateTime calculateDeadline(LocalDateTime now, int availableMinutes) {
		LocalDateTime dueDateTime = this.dueDateTime();
		LocalDateTime workEnd = now.plusMinutes(availableMinutes);
		LocalDateTime deadline;

		if (dueDateTime.isBefore(now) || dueDateTime.isAfter(workEnd)) {
			deadline = workEnd;
		} else {
			deadline = dueDateTime;
		}
		return deadline;
	}

}
