package com.example.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.entity.AppUser;
import com.example.demo.security.AppUserPrincipal;
import com.example.demo.service.AppUserService;
import com.example.demo.service.TaskPlannerService;

@Controller
public class DashboardController {

    private final AppUserService appUserService;
    private final TaskPlannerService taskPlannerService;

    public DashboardController(AppUserService appUserService, TaskPlannerService taskPlannerService) {
        this.appUserService = appUserService;
        this.taskPlannerService = taskPlannerService;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal AppUserPrincipal principal, Model model) {
        AppUser currentUser = appUserService.requireByEmail(principal.getUsername());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("dashboard", taskPlannerService.buildDashboard(currentUser));
        return "dashboard";
    }
}
