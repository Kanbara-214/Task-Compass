package com.kanbara.taskcompass.form;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.kanbara.taskcompass.entity.TaskStatus;

public class TaskForm {

    @NotBlank(message = "タスク名を入力してください")
    @Size(max = 160, message = "タスク名は160文字以内で入力してください")
    private String title;

    @Size(max = 2000, message = "説明は2000文字以内で入力してください")
    private String description;

    @NotNull(message = "締切を入力してください")
    private LocalDate dueDate;

    @NotNull(message = "重要度を選択してください")
    @Min(value = 1, message = "重要度は1以上で入力してください")
    @Max(value = 5, message = "重要度は5以下で入力してください")
    private Integer importance = 3;

    @NotNull(message = "緊急度を選択してください")
    @Min(value = 1, message = "緊急度は1以上で入力してください")
    @Max(value = 5, message = "緊急度は5以下で入力してください")
    private Integer urgency = 3;

    @NotNull(message = "予想作業時間を入力してください")
    @Min(value = 15, message = "予想作業時間は15分以上で入力してください")
    @Max(value = 720, message = "予想作業時間は12時間以内で入力してください")
    private Integer estimatedMinutes = 60;

    @NotNull(message = "ステータスを選択してください")
    private TaskStatus status = TaskStatus.TODO;

    @NotBlank(message = "カテゴリを入力してください")
    @Size(max = 80, message = "カテゴリは80文字以内で入力してください")
    private String category;

    public static TaskForm empty() {
        return new TaskForm();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getImportance() {
        return importance;
    }

    public void setImportance(Integer importance) {
        this.importance = importance;
    }

    public Integer getUrgency() {
        return urgency;
    }

    public void setUrgency(Integer urgency) {
        this.urgency = urgency;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
