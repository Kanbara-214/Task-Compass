package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.example.demo.entity.TaskItem;
import com.example.demo.entity.TaskStatus;

class PriorityScoringServiceTest {

    private final PriorityScoringService service = new PriorityScoringService();

    @Test
    void overdueHighImportanceTaskGetsCriticalPriority() {
        TaskItem task = new TaskItem();
        task.setTitle("職務経歴書を更新する");
        task.setDueDate(LocalDate.of(2026, 4, 10));
        task.setImportance(5);
        task.setUrgency(5);
        task.setEstimatedMinutes(120);
        task.setStatus(TaskStatus.TODO);

        var insight = service.evaluate(task, LocalDate.of(2026, 4, 11));

        assertThat(insight.score()).isGreaterThanOrEqualTo(115);
        assertThat(insight.overdue()).isTrue();
        assertThat(insight.summary()).contains("最優先");
    }

    @Test
    void doneTaskIsExcludedFromRecommendation() {
        TaskItem task = new TaskItem();
        task.setTitle("企業研究をする");
        task.setDueDate(LocalDate.of(2026, 4, 20));
        task.setImportance(3);
        task.setUrgency(2);
        task.setEstimatedMinutes(60);
        task.setStatus(TaskStatus.DONE);

        var insight = service.evaluate(task, LocalDate.of(2026, 4, 11));

        assertThat(insight.score()).isZero();
        assertThat(insight.bandLabel()).isEqualTo("完了済み");
    }
}
