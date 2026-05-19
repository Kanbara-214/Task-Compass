package com.kanbara.taskcompass.factory;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.kanbara.taskcompass.entity.TaskItem;
import com.kanbara.taskcompass.model.RecommendationCandidate;

public final class RecommendationCandidateFactory {

	private RecommendationCandidateFactory() {
	}

	public static List<RecommendationCandidate> toRecommendationCandidates(List<TaskItem> tasks) {
		return tasks.stream()
				.map(RecommendationCandidateFactory::toRecommendationCandidate)
				.toList();
	}

	public static RecommendationCandidate toRecommendationCandidate(TaskItem task) {
		return new RecommendationCandidate(
				task.getId(),
				task.getTitle(),
				toDueDateTime(task),
				task.getImportance(),
				task.getEstimatedMinutes());
	}

	private static LocalDateTime toDueDateTime(TaskItem task) {
		return task.getDueDate().atTime(LocalTime.MAX);
	}
}
