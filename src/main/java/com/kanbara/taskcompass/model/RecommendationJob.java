package com.kanbara.taskcompass.model;

import java.time.LocalDateTime;
import java.util.Comparator;

public record RecommendationJob(
		RecommendationCandidate candidate,
		int processingTime,
		LocalDateTime deadline,
		int value) {

	public static int compare(RecommendationJob left, RecommendationJob right) {
		Comparator<RecommendationJob> comparator = Comparator
				.comparing(RecommendationJob::deadline)
				.thenComparing(Comparator.comparingInt(RecommendationJob::value).reversed())
				.thenComparing(
						Comparator.comparingInt((RecommendationJob job) -> job.candidate().importance()).reversed())
				.thenComparingInt(RecommendationJob::processingTime)
				.thenComparingLong(job -> job.candidate().id());

		return comparator.compare(left, right);
	}
}