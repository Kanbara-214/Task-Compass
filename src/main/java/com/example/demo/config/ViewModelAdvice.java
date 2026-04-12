package com.example.demo.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.demo.entity.TaskStatus;
import com.example.demo.model.TaskSortOption;

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
