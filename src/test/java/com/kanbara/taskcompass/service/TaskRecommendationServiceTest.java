package com.kanbara.taskcompass.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.kanbara.taskcompass.model.RecommendationCandidate;
import com.kanbara.taskcompass.model.RecommendationResult;
import com.kanbara.taskcompass.model.RecommendedTask;

public class TaskRecommendationServiceTest {

	private final TaskRecommendationService taskRecommendationService = new TaskRecommendationService();

	@Test
	void doesNotSelectAllOverdueTasksWhenFutureTasksImproveReward() {
		LocalDateTime time = LocalDateTime.of(2026, 5, 19, 20, 3);
		List<RecommendationCandidate> candidates = List.of(
				createRecommendationCandidate(1L, "Test1", time.minusDays(1), 1, 30),
				createRecommendationCandidate(2L, "Test2", time.minusMinutes(1), 5, 15),
				createRecommendationCandidate(3L, "Test3", time.plusMinutes(45), 3, 15),
				createRecommendationCandidate(4L, "Test4", time.plusMinutes(90), 5, 15));

		RecommendationResult result = taskRecommendationService.recommendTasks(candidates, time, 45);

		assertThat(result.recommendedTasks())
				.extracting(RecommendedTask::candidate)
				.extracting(RecommendationCandidate::id)
				.containsExactly(2L, 3L, 4L);

		assertThat(result.remainingExpiredTaskCount()).isEqualTo(1);
	}

	@Test
	void selectsOverdueTasksWhenTheyHaveHigherReward() {
		LocalDateTime time = LocalDateTime.of(2026, 5, 19, 20, 3);
		List<RecommendationCandidate> candidates = List.of(
				createRecommendationCandidate(1L, "Test1", time.minusDays(1), 5, 15),
				createRecommendationCandidate(2L, "Test2", time.minusMinutes(15), 5, 15),
				createRecommendationCandidate(3L, "Test3", time.minusMinutes(5), 1, 15),
				createRecommendationCandidate(4L, "Test4", time.plusMinutes(15), 1, 15),
				createRecommendationCandidate(5L, "Test5", time.plusMinutes(45), 1, 15));

		RecommendationResult result = taskRecommendationService.recommendTasks(candidates, time, 30);

		assertThat(result.recommendedTasks())
				.extracting(RecommendedTask::candidate)
				.extracting(RecommendationCandidate::id)
				.containsExactly(1L, 2L);

		assertThat(result.remainingExpiredTaskCount()).isEqualTo(1);
	}

	@Test
	void selectsTaskWhenEstimatedMinutesEqualAvailableMinutes() {
		LocalDateTime time = LocalDateTime.of(2026, 5, 19, 20, 3);
		List<RecommendationCandidate> candidates = List.of(
				createRecommendationCandidate(1L, "Test1", time.plusMinutes(30), 5, 15),
				createRecommendationCandidate(2L, "Test2", time.plusMinutes(30), 5, 30),
				createRecommendationCandidate(3L, "Test3", time.plusMinutes(45), 5, 45));

		RecommendationResult result = taskRecommendationService.recommendTasks(candidates, time, 15);

		assertThat(result.recommendedTasks())
				.extracting(RecommendedTask::candidate)
				.extracting(RecommendationCandidate::id)
				.containsExactly(1L);

		assertThat(result.remainingExpiredTaskCount()).isEqualTo(0);
	}

	@Test
	void selectsTaskWhenFinishTimeEqualsDueDateTime() {
		LocalDateTime time = LocalDateTime.of(2026, 5, 19, 20, 3);
		List<RecommendationCandidate> candidates = List.of(
				createRecommendationCandidate(1L, "Test1", time.plusMinutes(15), 5, 15),
				createRecommendationCandidate(2L, "Test2", time.plusMinutes(45), 5, 30),
				createRecommendationCandidate(3L, "Test3", time.plusMinutes(60), 5, 75));

		RecommendationResult result = taskRecommendationService.recommendTasks(candidates, time, 60);

		assertThat(result.recommendedTasks())
				.extracting(RecommendedTask::candidate)
				.extracting(RecommendationCandidate::id)
				.containsExactly(1L, 2L);

		assertThat(result.remainingExpiredTaskCount()).isEqualTo(0);
	}

	@Test
	void selectsNoTasksWhenNoCompletableTasksExist() {
		LocalDateTime time = LocalDateTime.of(2026, 5, 19, 20, 3);
		List<RecommendationCandidate> candidates = List.of(
				createRecommendationCandidate(1L, "Test1", time.plusMinutes(10), 5, 15),
				createRecommendationCandidate(2L, "Test2", time.plusMinutes(30), 5, 45),
				createRecommendationCandidate(3L, "Test3", time.plusMinutes(60), 5, 75));

		RecommendationResult result = taskRecommendationService.recommendTasks(candidates, time, 60);

		assertThat(result.recommendedTasks()).isEmpty();

		assertThat(result.remainingExpiredTaskCount()).isEqualTo(0);
	}

	@Test
	void returnsSelectedTasksInStableTieBreakOrder() {
		LocalDateTime time = LocalDateTime.of(2026, 5, 19, 20, 3);
		List<RecommendationCandidate> candidates = List.of(
				createRecommendationCandidate(1L, "Test1", time.plusMinutes(60), 2, 15),
				createRecommendationCandidate(2L, "Test2", time.plusMinutes(60), 5, 15),
				createRecommendationCandidate(3L, "Test3", time.plusMinutes(60), 4, 15),
				createRecommendationCandidate(4L, "Test4", time.plusMinutes(120), 5, 15),
				createRecommendationCandidate(5L, "Test5", time.plusMinutes(195), 4, 15),
				createRecommendationCandidate(6L, "Test6", time.plusMinutes(210), 4, 15),
				createRecommendationCandidate(7L, "Test7", time.plusMinutes(195), 4, 30),
				createRecommendationCandidate(8L, "Test8", time.plusMinutes(180), 2, 15));

		RecommendationResult result = taskRecommendationService.recommendTasks(candidates, time, 180);

		assertThat(result.recommendedTasks())
				.extracting(RecommendedTask::candidate)
				.extracting(RecommendationCandidate::id)
				.containsExactly(2L, 3L, 1L, 4L, 5L, 6L, 7L, 8L);

		assertThat(result.remainingExpiredTaskCount()).isEqualTo(0);
	}

	@Test
	void explainsFutureTaskSelectedWithRemainingTimeWhenOverdueTaskRemains() {
		LocalDateTime time = LocalDateTime.of(2026, 5, 19, 20, 3);
		List<RecommendationCandidate> candidates = List.of(
				createRecommendationCandidate(1L, "Test1", time.plusDays(1), 5, 30),
				createRecommendationCandidate(2L, "Test2", time.plusWeeks(1), 1, 15),
				createRecommendationCandidate(3L, "Test3", time.minusDays(1), 1, 30));

		RecommendationResult result = taskRecommendationService.recommendTasks(candidates, time, 45);

		assertThat(result.recommendedTasks())
				.extracting(RecommendedTask::candidate)
				.extracting(RecommendationCandidate::id)
				.containsExactly(1L, 2L);

		assertThat(result.recommendedTasks().get(1).reason())
				.contains("残り15分")
				.contains("前倒しタスク");
		assertThat(result.remainingExpiredTaskCount()).isEqualTo(1);
		assertThat(result.summary())
				.contains("2件")
				.contains("45分")
				.contains("未選択の期限切れタスクが1件");
	}

	private RecommendationCandidate createRecommendationCandidate(
			Long id,
			String title,
			LocalDateTime dueDateTime,
			int importance,
			int estimatedMinutes) {
		RecommendationCandidate candidate = new RecommendationCandidate(id, title, dueDateTime, importance,
				estimatedMinutes);
		return candidate;
	}

}
