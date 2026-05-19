package com.kanbara.taskcompass.factory;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import com.kanbara.taskcompass.entity.TaskItem;
import com.kanbara.taskcompass.entity.TaskStatus;
import com.kanbara.taskcompass.model.RecommendationCandidate;

public class RecommendationCandidateFactoryTest {

	@Test
	void convertsTaskItemToRecommendationCandidate() {
		TaskItem task = createTask(1L, "Test", LocalDate.of(2026, 5, 25), 3, 60);
		RecommendationCandidate candidate = RecommendationCandidateFactory.toRecommendationCandidate(task);
		assertThat(candidate).isEqualTo(new RecommendationCandidate(
				task.getId(),
				task.getTitle(),
				task.getDueDate().atTime(LocalTime.MAX),
				task.getImportance(),
				task.getEstimatedMinutes()));
	}

	private TaskItem createTask(
			Long id,
			String title,
			LocalDate dueDate,
			int importance,
			int estimatedMinutes) {
		TaskItem task = new TaskItem();
		task.setId(id);
		task.setOwnerId(1L);
		task.setTitle(title);
		task.setDescription(title + " description");
		task.setDueDate(dueDate);
		task.setImportance(importance);
		task.setUrgency(1);
		task.setEstimatedMinutes(estimatedMinutes);
		task.setStatus(TaskStatus.TODO);
		task.setCategory("Test");
		task.setCreatedAt(LocalDateTime.of(2026, 5, 20, 3, 25));
		task.setUpdatedAt(LocalDateTime.of(2026, 5, 20, 3, 25));
		return task;
	}

}
