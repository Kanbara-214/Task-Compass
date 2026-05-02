package com.kanbara.taskcompass.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kanbara.taskcompass.entity.AppUser;
import com.kanbara.taskcompass.entity.TaskItem;
import com.kanbara.taskcompass.entity.TaskStatus;
import com.kanbara.taskcompass.exception.TaskNotFoundException;
import com.kanbara.taskcompass.form.TaskForm;
import com.kanbara.taskcompass.mapper.TaskItemMapper;
import com.kanbara.taskcompass.model.DashboardView;
import com.kanbara.taskcompass.model.PriorityInsight;
import com.kanbara.taskcompass.model.TaskSortOption;
import com.kanbara.taskcompass.model.TaskView;
import com.kanbara.taskcompass.query.TaskListQuery;

@Service
public class TaskPlannerService {

    private final TaskItemMapper taskItemMapper;
    private final PriorityScoringService priorityScoringService;

    public TaskPlannerService(TaskItemMapper taskItemMapper, PriorityScoringService priorityScoringService) {
        this.taskItemMapper = taskItemMapper;
        this.priorityScoringService = priorityScoringService;
    }

    @Transactional(readOnly = true)
    public DashboardView buildDashboard(AppUser owner) {
        List<TaskView> recommended = taskItemMapper.findByOwnerIdOrderByUpdatedAtDesc(owner.getId()).stream()
                .map(this::toView)
                .sorted(recommendedComparator())
                .toList();

        List<TaskView> activeTasks = recommended.stream()
                .filter(task -> !task.isDone())
                .toList();
        List<TaskView> overdueTasks = activeTasks.stream()
                .filter(task -> task.priority().overdue())
                .toList();
        List<TaskView> dueThisWeek = activeTasks.stream()
                .filter(task -> task.priority().dueThisWeek())
                .toList();

        int totalCount = recommended.size();
        int doneCount = (int) recommended.stream().filter(TaskView::isDone).count();
        int inProgressCount = (int) recommended.stream().filter(task -> task.status() == TaskStatus.IN_PROGRESS).count();
        int openCount = totalCount - doneCount;
        int completionRate = totalCount == 0 ? 0 : Math.round((doneCount * 100.0f) / totalCount);
        int averagePriority = activeTasks.isEmpty()
                ? 0
                : Math.round((float) activeTasks.stream().mapToInt(task -> task.priority().score()).average().orElse(0));

        return new DashboardView(
                activeTasks.stream().limit(3).toList(),
                overdueTasks.stream().limit(5).toList(),
                dueThisWeek.stream().limit(5).toList(),
                totalCount,
                openCount,
                inProgressCount,
                doneCount,
                completionRate,
                averagePriority);
    }

    @Transactional(readOnly = true)
    public List<TaskView> listTasks(AppUser owner, TaskListQuery query) {
        List<TaskView> tasks = taskItemMapper.findByOwnerIdAndListQuery(
                owner.getId(),
                query.status(),
                query.category(),
                query.sort()).stream()
                .map(this::toView)
                .toList();

        if (query.sort() == TaskSortOption.RECOMMENDED) {
            return tasks.stream()
                    .sorted(recommendedComparator())
                    .toList();
        }

        return tasks;
    }

    @Transactional(readOnly = true)
    public TaskView getTaskView(AppUser owner, Long taskId) {
        return toView(requireTask(owner.getId(), taskId));
    }

    @Transactional(readOnly = true)
    public TaskForm getTaskForm(AppUser owner, Long taskId) {
        TaskItem task = requireTask(owner.getId(), taskId);

        TaskForm form = new TaskForm();
        form.setTitle(task.getTitle());
        form.setDescription(task.getDescription());
        form.setDueDate(task.getDueDate());
        form.setImportance(task.getImportance());
        form.setUrgency(task.getUrgency());
        form.setEstimatedMinutes(task.getEstimatedMinutes());
        form.setStatus(task.getStatus());
        form.setCategory(task.getCategory());
        return form;
    }

    @Transactional(readOnly = true)
    public List<String> categoryOptions(AppUser owner) {
        return taskItemMapper.findDistinctCategoriesByOwnerId(owner.getId());
    }

    @Transactional
    public TaskItem createTask(AppUser owner, TaskForm form) {
        LocalDateTime now = LocalDateTime.now();
        TaskItem task = new TaskItem();
        task.setOwnerId(owner.getId());
        task.setCreatedAt(now);
        task.setUpdatedAt(now);
        apply(task, form);
        taskItemMapper.insert(task);
        return task;
    }

    @Transactional
    public TaskItem updateTask(AppUser owner, Long taskId, TaskForm form) {
        TaskItem task = requireTask(owner.getId(), taskId);
        apply(task, form);
        task.setUpdatedAt(LocalDateTime.now());
        taskItemMapper.update(task);
        return task;
    }

    @Transactional
    public void deleteTask(AppUser owner, Long taskId) {
        requireTask(owner.getId(), taskId);
        taskItemMapper.deleteByIdAndOwnerId(taskId, owner.getId());
    }

    private void apply(TaskItem task, TaskForm form) {
        task.setTitle(form.getTitle().trim());
        task.setDescription(form.getDescription() == null ? "" : form.getDescription().trim());
        task.setDueDate(form.getDueDate());
        task.setImportance(form.getImportance());
        task.setUrgency(form.getUrgency());
        task.setEstimatedMinutes(form.getEstimatedMinutes());
        task.setStatus(form.getStatus());
        task.setCategory(form.getCategory().trim());
    }

    private TaskItem requireTask(Long ownerId, Long taskId) {
        TaskItem task = taskItemMapper.findByIdAndOwnerId(taskId, ownerId);
        if (task == null) {
            throw new TaskNotFoundException("タスクが見つかりません");
        }
        return task;
    }

    private TaskView toView(TaskItem task) {
        PriorityInsight priority = priorityScoringService.evaluate(task, LocalDate.now());
        return new TaskView(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getImportance(),
                task.getUrgency(),
                task.getEstimatedMinutes(),
                formatMinutes(task.getEstimatedMinutes()),
                task.getStatus(),
                task.getCategory(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                priority);
    }

    private Comparator<TaskView> recommendedComparator() {
        return Comparator.comparing(TaskView::isDone)
                .thenComparing(Comparator.comparingInt((TaskView task) -> task.priority().score()).reversed())
                .thenComparing(TaskView::dueDate)
                .thenComparing(Comparator.comparingInt(TaskView::importance).reversed())
                .thenComparing(TaskView::updatedAt, Comparator.reverseOrder());
    }

    private String formatMinutes(int minutes) {
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;
        if (hours == 0) {
            return remainingMinutes + "分";
        }
        if (remainingMinutes == 0) {
            return hours + "時間";
        }
        return hours + "時間" + remainingMinutes + "分";
    }
}
