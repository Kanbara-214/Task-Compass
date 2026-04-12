package com.example.demo.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entity.TaskItem;
import com.example.demo.entity.TaskStatus;
import com.example.demo.model.PriorityInsight;

@Service
public class PriorityScoringService {

    public PriorityInsight evaluate(TaskItem task, LocalDate today) {
        if (task.getStatus() == TaskStatus.DONE) {
            return new PriorityInsight(
                    0,
                    "完了済み",
                    "priority-complete",
                    "完了済みのためおすすめ対象から外れています。",
                    List.of("完了済みタスクです"),
                    false,
                    false,
                    false);
        }

        int score = 0;
        List<String> reasons = new ArrayList<>();
        long daysUntilDue = ChronoUnit.DAYS.between(today, task.getDueDate());

        int importancePoints = task.getImportance() * 12;
        int urgencyPoints = task.getUrgency() * 9;
        score += importancePoints + urgencyPoints;
        reasons.add("重要度 " + task.getImportance() + " と緊急度 " + task.getUrgency() + " を反映");

        boolean overdue = daysUntilDue < 0;
        boolean dueToday = daysUntilDue == 0;
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        boolean dueThisWeek = !task.getDueDate().isBefore(today) && !task.getDueDate().isAfter(endOfWeek);

        if (overdue) {
            int overduePoints = 55 + (int) Math.min(20, Math.abs(daysUntilDue) * 5);
            score += overduePoints;
            reasons.add("締切超過のため強く優先");
        } else if (dueToday) {
            score += 36;
            reasons.add("今日が締切");
        } else if (daysUntilDue == 1) {
            score += 28;
            reasons.add("明日が締切");
        } else if (daysUntilDue <= 3) {
            score += 20;
            reasons.add("3日以内に締切");
        } else if (daysUntilDue <= 7) {
            score += 12;
            reasons.add("今週中に締切");
        } else if (daysUntilDue <= 14) {
            score += 6;
            reasons.add("2週間以内に締切");
        }

        if (task.getEstimatedMinutes() >= 180) {
            score += 8;
            reasons.add("作業時間が長いため早めの着手を推奨");
        } else if (task.getEstimatedMinutes() >= 90) {
            score += 4;
            reasons.add("中程度以上の作業時間");
        }

        if (task.getStatus() == TaskStatus.IN_PROGRESS) {
            score += 6;
            reasons.add("進行中のため止めずに進めたいタスク");
        }

        String summary = buildSummary(overdue, dueToday, daysUntilDue, task.getImportance(), task.getEstimatedMinutes());
        String bandLabel = bandLabel(score);
        String bandClass = bandClass(score, overdue);
        return new PriorityInsight(score, bandLabel, bandClass, summary, List.copyOf(reasons), overdue, dueToday, dueThisWeek);
    }

    private String buildSummary(
            boolean overdue,
            boolean dueToday,
            long daysUntilDue,
            int importance,
            int estimatedMinutes) {
        if (overdue) {
            return "締切を過ぎているため最優先です。";
        }
        if (dueToday && importance >= 4) {
            return "締切が今日で重要度も高いため、今すぐ着手したいタスクです。";
        }
        if (daysUntilDue <= 3 && importance >= 4) {
            return "締切が近く重要度も高いため、上位に置いています。";
        }
        if (estimatedMinutes >= 180) {
            return "作業時間が長いため、締切前でも早めの着手を勧めます。";
        }
        if (importance >= 4) {
            return "重要度が高く、先送りしづらいタスクです。";
        }
        return "今のうちに少しずつ進めておくと楽になるタスクです。";
    }

    private String bandLabel(int score) {
        if (score >= 115) {
            return "最優先";
        }
        if (score >= 85) {
            return "高";
        }
        if (score >= 60) {
            return "中";
        }
        return "低";
    }

    private String bandClass(int score, boolean overdue) {
        if (overdue || score >= 115) {
            return "priority-critical";
        }
        if (score >= 85) {
            return "priority-high";
        }
        if (score >= 60) {
            return "priority-medium";
        }
        return "priority-low";
    }
}
