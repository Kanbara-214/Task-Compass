package com.kanbara.taskcompass.model;

public record RecommendedTask(
        RecommendationCandidate candidate,
        String reason) {
}
