package com.kanbara.taskcompass.factory;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.kanbara.taskcompass.entity.TaskItem;
import com.kanbara.taskcompass.entity.TaskStatus;
import com.kanbara.taskcompass.model.RecommendationCandidate;

public class RecommendationCandidateFactoryTest {

	@Test
	void convertsTaskItemToRecommendationCandidate() {
		TaskItem task = createTask(1L, "Test", LocalDateTime.of(2026, 5, 25, 6, 44), 3, 60);
		RecommendationCandidate candidate = RecommendationCandidateFactory.toRecommendationCandidate(task);
		assertThat(candidate).isEqualTo(new RecommendationCandidate(
				task.getId(),
				task.getTitle(),
				task.getDueDateTime(),
				task.getImportance(),
				task.getEstimatedMinutes()));
	}

	@Test
	void toRecommendationCandidatesConvertsTasksInOrder() {
		LocalDateTime dateTime = LocalDateTime.of(2026, 5, 25, 6, 44);
		TaskItem todo = createTask(1L, "TODO task", dateTime, 3, 60);

		TaskItem inProgress = createTask(2L, "IN_PROGRESS task", dateTime, 2, 30);
		inProgress.setStatus(TaskStatus.IN_PROGRESS);

		TaskItem done = createTask(3L, "DONE task", dateTime, 5, 15);
		done.setStatus(TaskStatus.DONE);

		List<RecommendationCandidate> candidates = RecommendationCandidateFactory.toRecommendationCandidates(
				List.of(todo, inProgress, done));

		assertThat(candidates)
				.extracting(RecommendationCandidate::id)
				.containsExactly(todo.getId(), inProgress.getId(), done.getId());
	}

	private TaskItem createTask(
			Long id,
			String title,
			LocalDateTime dueDateTime,
			int importance,
			int estimatedMinutes) {
		TaskItem task = new TaskItem();
		task.setId(id);
		task.setOwnerId(1L);
		task.setTitle(title);
		task.setDescription(title + " description");
		task.setDueDateTime(dueDateTime);
		task.setImportance(importance);
		task.setEstimatedMinutes(estimatedMinutes);
		task.setStatus(TaskStatus.TODO);
		task.setCategory("Test");
		task.setCreatedAt(LocalDateTime.of(2026, 5, 20, 3, 25));
		task.setUpdatedAt(LocalDateTime.of(2026, 5, 20, 3, 25));
		return task;
	}

}
