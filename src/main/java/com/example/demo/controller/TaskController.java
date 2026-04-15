package com.example.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

import com.example.demo.entity.AppUser;
import com.example.demo.form.TaskForm;
import com.example.demo.query.TaskListQuery;
import com.example.demo.security.AppUserPrincipal;
import com.example.demo.service.AppUserService;
import com.example.demo.service.TaskPlannerService;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final AppUserService appUserService;
    private final TaskPlannerService taskPlannerService;

    public TaskController(AppUserService appUserService, TaskPlannerService taskPlannerService) {
        this.appUserService = appUserService;
        this.taskPlannerService = taskPlannerService;
    }

    @GetMapping
    public String list(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sort,
            Model model) {
        AppUser currentUser = currentUser(principal);
        TaskListQuery query = TaskListQuery.of(category, status, sort);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("query", query);
        model.addAttribute("categories", taskPlannerService.categoryOptions(currentUser));
        model.addAttribute("tasks", taskPlannerService.listTasks(currentUser, query));
        return "tasks/list";
    }

    @GetMapping("/new")
    public String createForm(@AuthenticationPrincipal AppUserPrincipal principal, Model model) {
        AppUser currentUser = currentUser(principal);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("taskForm", TaskForm.empty());
        model.addAttribute("categories", taskPlannerService.categoryOptions(currentUser));
        model.addAttribute("formTitle", "タスクを追加");
        model.addAttribute("formAction", "/tasks");
        return "tasks/form";
    }

    @PostMapping
    public String create(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @Valid @ModelAttribute("taskForm") TaskForm taskForm,
            BindingResult bindingResult,
            Model model) {
        AppUser currentUser = currentUser(principal);
        if (bindingResult.hasErrors()) {
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("categories", taskPlannerService.categoryOptions(currentUser));
            model.addAttribute("formTitle", "タスクを追加");
            model.addAttribute("formAction", "/tasks");
            return "tasks/form";
        }

        taskPlannerService.createTask(currentUser, taskForm);
        return "redirect:/tasks";
    }

    @GetMapping("/{taskId}")
    public String detail(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @PathVariable Long taskId,
            Model model) {
        AppUser currentUser = currentUser(principal);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("task", taskPlannerService.getTaskView(currentUser, taskId));
        return "tasks/detail";
    }

    @GetMapping("/{taskId}/edit")
    public String editForm(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @PathVariable Long taskId,
            Model model) {
        AppUser currentUser = currentUser(principal);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("taskForm", taskPlannerService.getTaskForm(currentUser, taskId));
        model.addAttribute("categories", taskPlannerService.categoryOptions(currentUser));
        model.addAttribute("formTitle", "タスクを編集");
        model.addAttribute("formAction", "/tasks/" + taskId);
        model.addAttribute("taskId", taskId);
        return "tasks/form";
    }

    @PostMapping("/{taskId}")
    public String update(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @PathVariable Long taskId,
            @Valid @ModelAttribute("taskForm") TaskForm taskForm,
            BindingResult bindingResult,
            Model model) {
        AppUser currentUser = currentUser(principal);
        if (bindingResult.hasErrors()) {
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("categories", taskPlannerService.categoryOptions(currentUser));
            model.addAttribute("formTitle", "タスクを編集");
            model.addAttribute("formAction", "/tasks/" + taskId);
            model.addAttribute("taskId", taskId);
            return "tasks/form";
        }

        taskPlannerService.updateTask(currentUser, taskId, taskForm);
        return "redirect:/tasks/" + taskId;
    }

    @PostMapping("/{taskId}/delete")
    public String delete(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @PathVariable Long taskId) {
        AppUser currentUser = currentUser(principal);
        taskPlannerService.deleteTask(currentUser, taskId);
        return "redirect:/tasks";
    }

    private AppUser currentUser(AppUserPrincipal principal) {
        return appUserService.requireByEmail(principal.getUsername());
    }
}
