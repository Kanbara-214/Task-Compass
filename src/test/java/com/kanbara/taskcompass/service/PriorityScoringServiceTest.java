package com.kanbara.taskcompass.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.kanbara.taskcompass.entity.TaskItem;
import com.kanbara.taskcompass.entity.TaskStatus;

class PriorityScoringServiceTest {

	private final PriorityScoringService priorityScoringService = new PriorityScoringService();

	@Test
	void overdueHighImportanceTaskGetsCriticalPriority() {
		LocalDateTime now = LocalDateTime.of(2026, 5, 23, 6, 50);
		TaskItem task = createTask(LocalDateTime.of(2026, 5, 22, 6, 50), 5, 5, 120, TaskStatus.TODO);
		var insight = priorityScoringService.evaluate(task, now);

		assertThat(insight.score()).isGreaterThanOrEqualTo(115);
		assertThat(insight.overdue()).isTrue();
		assertThat(insight.summary()).contains("最優先");
	}

	@Test
	void sameDayPastDueTaskIsOverdue() {
		LocalDateTime now = LocalDateTime.of(2026, 5, 21, 18, 0);
		TaskItem task = createTask(LocalDateTime.of(2026, 5, 21, 9, 0), 3, 2, 60, TaskStatus.TODO);

		var insight = priorityScoringService.evaluate(task, now);

		assertThat(insight.overdue()).isTrue();
		assertThat(insight.dueToday()).isFalse();
		assertThat(insight.summary()).contains("締切を過ぎ");
	}

	@Test
	void futureTaskDueTodayGetsDueTodayPriority() {
		LocalDateTime now = LocalDateTime.of(2026, 5, 21, 10, 0);
		TaskItem task = createTask(LocalDateTime.of(2026, 5, 21, 18, 0), 5, 1, 60, TaskStatus.TODO);

		var insight = priorityScoringService.evaluate(task, now);

		assertThat(insight.overdue()).isFalse();
		assertThat(insight.dueToday()).isTrue();
		assertThat(insight.reasons()).contains("今日が締切");
	}

	@Test
	void tomorrowTaskUsesDateBasedTomorrowPriority() {
		LocalDateTime now = LocalDateTime.of(2026, 5, 21, 23, 30);
		TaskItem task = createTask(LocalDateTime.of(2026, 5, 22, 9, 0), 3, 1, 60, TaskStatus.TODO);

		var insight = priorityScoringService.evaluate(task, now);

		assertThat(insight.reasons()).contains("明日が締切");
	}

	@Test
	void dueThisWeekIncludesSundayEndOfDay() {
		LocalDateTime now = LocalDateTime.of(2026, 5, 21, 18, 0);
		TaskItem task = createTask(LocalDateTime.of(2026, 5, 24, 23, 0), 3, 1, 60, TaskStatus.TODO);

		var insight = priorityScoringService.evaluate(task, now);

		assertThat(insight.dueThisWeek()).isTrue();
	}

	@Test
	void doneTaskIsExcludedFromRecommendation() {
		TaskItem task = createTask(LocalDateTime.of(2026, 5, 21, 6, 50), 3, 2, 60, TaskStatus.DONE);

		var insight = priorityScoringService.evaluate(task, LocalDateTime.of(2026, 5, 21, 6, 50));

		assertThat(insight.score()).isZero();
		assertThat(insight.bandLabel()).isEqualTo("完了済み");
	}

	private TaskItem createTask(
			LocalDateTime dueDateTime,
			int importance,
			int urgency,
			int estimatedMinutes,
			TaskStatus status) {
		TaskItem task = new TaskItem();
		task.setTitle("Test task");
		task.setDueDateTime(dueDateTime);
		task.setImportance(importance);
		task.setUrgency(urgency);
		task.setEstimatedMinutes(estimatedMinutes);
		task.setStatus(status);
		return task;
	}
}
