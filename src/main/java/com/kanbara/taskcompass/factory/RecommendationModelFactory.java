package com.kanbara.taskcompass.factory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.kanbara.taskcompass.model.RecommendationCandidate;
import com.kanbara.taskcompass.model.RecommendationJob;
import com.kanbara.taskcompass.model.RecommendationResult;
import com.kanbara.taskcompass.model.RecommendedTask;

public final class RecommendationModelFactory {

	private RecommendationModelFactory() {
	}

	public static RecommendationJob toRecommendationJob(
			RecommendationCandidate candidate,
			LocalDateTime now,
			int availableMinutes) {

		int processingTime = candidate.estimatedMinutes();
		LocalDateTime deadline = candidate.calculateDeadline(now, availableMinutes);
		int value = candidate.calculateValue(now, availableMinutes);

		return new RecommendationJob(candidate, processingTime, deadline, value);
	}

	public static List<RecommendedTask> toRecommendedTasks(List<RecommendationJob> jobs, LocalDateTime now,
			int availableMinutes) {
		List<RecommendedTask> recommendedTasks = new ArrayList<>();
		int elapsedMinutes = 0;

		for (RecommendationJob job : jobs) {
			String reason = buildReason(job, now, availableMinutes, elapsedMinutes);
			elapsedMinutes += job.processingTime();
			recommendedTasks.add(new RecommendedTask(
					job.candidate(),
					reason));
		}

		return recommendedTasks;
	}

	private static String buildReason(RecommendationJob job, LocalDateTime now, int availableMinutes,
			int elapsedMinutes) {
		LocalDateTime dueDateTime = job.candidate().dueDateTime();
		LocalDateTime workEnd = now.plusMinutes(availableMinutes);
		int remainingBeforeStart = availableMinutes - elapsedMinutes;

		if (dueDateTime.isBefore(now)) {
			return "期限切れ状態を解消できるため推薦されました。";
		}

		if (!dueDateTime.isAfter(workEnd)) {
			return "今回の作業時間内に期限を迎えるため、締切内に完了できる順序で推薦されました。";
		}

		if (0 < elapsedMinutes) {
			return "優先タスクへ取り組んだ後、残り" + remainingBeforeStart + "分で完了できる前倒しタスクとして推薦されました。";
		}

		return "今回の作業時間内に完了でき、報酬最大化に寄与するタスクです。";

	}

	public static RecommendationResult buildResult(List<RecommendationJob> selectedJobs,
			List<RecommendationJob> undoneJobs, LocalDateTime now, int availableMinutes) {
		List<RecommendedTask> recommendedTasks = RecommendationModelFactory.toRecommendedTasks(selectedJobs, now,
				availableMinutes);
		int remainingExpiredTaskCount = calculateRemainingExpiredTaskCount(selectedJobs, undoneJobs, now);
		String summary = buildSummary(selectedJobs, remainingExpiredTaskCount);
		return new RecommendationResult(
				recommendedTasks,
				availableMinutes,
				remainingExpiredTaskCount,
				summary);
	}

	private static int calculateRemainingExpiredTaskCount(List<RecommendationJob> selectedJobs,
			List<RecommendationJob> undoneJobs, LocalDateTime now) {
		Set<Long> selectedIds = selectedJobs.stream()
				.map(job -> job.candidate().id())
				.collect(Collectors.toSet());

		int remainingExpiredTaskCount = (int) undoneJobs.stream()
				.filter(job -> job.candidate().dueDateTime().isBefore(now))
				.filter(job -> !selectedIds.contains(job.candidate().id()))
				.count();

		return remainingExpiredTaskCount;
	}

	private static String buildSummary(List<RecommendationJob> jobs, int remainingExpiredTaskCount) {
		if (jobs.isEmpty()) {
			if (0 < remainingExpiredTaskCount) {
				return "今回の作業時間内で完了できる推薦タスクはありません。未選択の期限切れタスクが"
						+ remainingExpiredTaskCount
						+ "件あります。";
			}

			return "今回の作業時間内で完了できる推薦タスクはありません。";
		}

		int totalMinutes = jobs.stream()
				.mapToInt(RecommendationJob::processingTime)
				.sum();

		if (0 < remainingExpiredTaskCount) {
			return jobs.size()
					+ "件のタスクを合計"
					+ totalMinutes
					+ "分で推薦しました。未選択の期限切れタスクが"
					+ remainingExpiredTaskCount
					+ "件あります。";
		}

		return jobs.size()
				+ "件のタスクを合計"
				+ totalMinutes
				+ "分で推薦しました。";
	}

}
