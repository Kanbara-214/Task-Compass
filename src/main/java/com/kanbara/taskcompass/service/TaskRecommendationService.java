package com.kanbara.taskcompass.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kanbara.taskcompass.model.RecommendationCandidate;
import com.kanbara.taskcompass.model.RecommendationJob;
import com.kanbara.taskcompass.model.RecommendationResult;

@Service
public class TaskRecommendationService {
	public RecommendationResult recommendTasks(List<RecommendationCandidate> undoneTasks, LocalDateTime now,
			int availableMinutes) {
		List<RecommendationJob> undoneJobs = undoneTasks
				.stream()
				.map((candidate) -> toRecommendationJob(candidate, now, availableMinutes))
				.sorted(RecommendationJob::compare)
				.toList();

		List<RecommendationJob> selectedJobs = selectJobs(undoneJobs, now, availableMinutes);

		return new RecommendationResult(
				List.of(),
				availableMinutes,
				0,
				"タスクの推薦機能は現在開発中です。しばらくお待ちください。");
	}

	private List<RecommendationJob> selectJobs(List<RecommendationJob> undoneJobs, LocalDateTime now,
			int availableMinutes) {

		int n = undoneJobs.size();
		int[][] dp = new int[n + 1][availableMinutes + 1]; //先頭 i 件のジョブを見て、合計 t 分で完了できる最大 value
		boolean[][] selected = new boolean[n + 1][availableMinutes + 1];

		for (int i = 1; i < dp.length; i++) {
				Arrays.fill(dp[i], -1);
		}
		dp[0][0] = 0;

		for (int i = 1; i <= undoneJobs.size(); i++) {
			RecommendationJob job = undoneJobs.get(i - 1);
			for (int t = 0; t <= availableMinutes; t++) {
				LocalDateTime finishAt = now.plusMinutes(t);
				int previousTime = t - job.processingTime(); //startAt = now.plusMinutes(previousTime);
				boolean ableToFinishInT = 0 <= previousTime;
				dp[i][t] = dp[i - 1][t];
				if (!ableToFinishInT) {
					continue;
				}

				boolean reachableState = dp[i - 1][previousTime] != -1;
				boolean ableToFinishBeforeDeadline = !finishAt.isAfter(job.deadline());
				if (ableToFinishBeforeDeadline && reachableState) {
					int nextValue = dp[i - 1][previousTime] + job.value();
					if (dp[i][t] < nextValue) {
						dp[i][t] = nextValue;
						selected[i][t] = true;
					}
				}
			}
		}

		int bestTime = findBestTime(dp, n, availableMinutes);
		List<RecommendationJob> selectedJobs = restoreSelected(selected, undoneJobs, bestTime);
		return selectedJobs;
	}
	
	private int findBestTime(int[][] dp, int n, int availableMinutes) {
	    int bestTime = 0;

	    for (int t = 1; t <= availableMinutes; t++) {
	        if (dp[n][bestTime] < dp[n][t]) {
	            bestTime = t;
	        }
	    }

	    return bestTime;
	}
	
	private List<RecommendationJob> restoreSelected(boolean[][] selected, List<RecommendationJob> undoneJobs, int bestTime) {
	    List<RecommendationJob> selectedJobs = new java.util.ArrayList<>();
	    int t = bestTime;
	    int i = selected.length - 1;
	    
		while(0 < i) {
			if(selected[i][t]) {
				RecommendationJob job = undoneJobs.get(i - 1);
				selectedJobs.add(job);
				t -= job.processingTime();
			}
			i--;
		}
		
		return selectedJobs.reversed();
	}
	
	private RecommendationJob toRecommendationJob(
	        RecommendationCandidate candidate,
	        LocalDateTime now,
	        int availableMinutes) {
	    int processingTime = candidate.estimatedMinutes();
	    LocalDateTime deadline = candidate.calculateDeadline(now, availableMinutes);
	    int value = candidate.calculateValue(now, availableMinutes);

	    return new RecommendationJob(candidate, processingTime, deadline, value);
	}
	
}