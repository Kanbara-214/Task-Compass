package com.example.demo.model;

import java.util.List;

public record PriorityInsight(
        int score,
        String bandLabel,
        String bandClass,
        String summary,
        List<String> reasons,
        boolean overdue,
        boolean dueToday,
        boolean dueThisWeek) {
}
