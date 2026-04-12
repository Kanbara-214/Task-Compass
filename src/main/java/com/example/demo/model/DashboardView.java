package com.example.demo.model;

import java.util.List;

public record DashboardView(
        List<TaskView> todayFocus,
        List<TaskView> overdueTasks,
        List<TaskView> dueThisWeek,
        int totalCount,
        int openCount,
        int inProgressCount,
        int doneCount,
        int completionRate,
        int averagePriority) {
}
