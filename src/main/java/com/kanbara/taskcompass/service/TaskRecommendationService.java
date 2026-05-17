package com.kanbara.taskcompass.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kanbara.taskcompass.model.RecommendationCandidate;
import com.kanbara.taskcompass.model.RecommendationResult;

@Service
public class TaskRecommendationService {
	public RecommendationResult recommendTasks(List<RecommendationCandidate> undoneTasks, LocalDateTime now,
			int availableMinutes) {
		return new RecommendationResult(
				List.of(),
				availableMinutes,
				0,
				"タスクの推薦機能は現在開発中です。しばらくお待ちください。");
	}

}