package com.kanbara.taskcompass.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.kanbara.taskcompass.entity.TaskStatus;
import com.kanbara.taskcompass.model.TaskSortOption;

@ControllerAdvice
public class ViewModelAdvice {

    @ModelAttribute("appName")
    public String appName() {
        return "Task Compass";
    }

    @ModelAttribute("taskStatuses")
    public TaskStatus[] taskStatuses() {
        return TaskStatus.values();
    }

    @ModelAttribute("taskSortOptions")
    public TaskSortOption[] taskSortOptions() {
        return TaskSortOption.values();
    }
}
