package com.kanbara.taskcompass.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kanbara.taskcompass.model.RecommendationCandidate;
import com.kanbara.taskcompass.model.RecommendationResult;

@Service
public class TaskRecommendationService {

	public RecommendationResult recommendTasks(List<RecommendationCandidate> undoneTasks, LocalDateTime now, int availableTime) {
		
		

		RecommendationResult recommendationResult = new RecommendationResult(
				undoneTasks,
				availableTime,
				0,
				"タスクの推薦機能は現在開発中です。しばらくお待ちください。");
		return recommendationResult;
	}

}
