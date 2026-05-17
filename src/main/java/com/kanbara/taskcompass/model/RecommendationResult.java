package com.kanbara.taskcompass.model;

import java.util.List;

public record RecommendationResult(
        List<RecommendedTask> recommendedTasks,
        int availableMinutes,
        int remainingExpiredTaskCount,
        String summary) {
}