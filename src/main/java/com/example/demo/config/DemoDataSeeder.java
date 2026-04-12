package com.example.demo.config;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.entity.AppUser;
import com.example.demo.entity.TaskItem;
import com.example.demo.entity.TaskStatus;
import com.example.demo.mapper.AppUserMapper;
import com.example.demo.mapper.TaskItemMapper;

@Component
public class DemoDataSeeder implements CommandLineRunner {

    private final AppUserMapper appUserMapper;
    private final TaskItemMapper taskItemMapper;
    private final PasswordEncoder passwordEncoder;

    public DemoDataSeeder(
            AppUserMapper appUserMapper,
            TaskItemMapper taskItemMapper,
            PasswordEncoder passwordEncoder) {
        this.appUserMapper = appUserMapper;
        this.taskItemMapper = taskItemMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (appUserMapper.countUsers() > 0) {
            return;
        }

        AppUser demoUser = new AppUser();
        demoUser.setDisplayName("Demo User");
        demoUser.setEmail("demo@example.com");
        demoUser.setPasswordHash(passwordEncoder.encode("password123"));
        demoUser.setCreatedAt(LocalDateTime.now());
        appUserMapper.insert(demoUser);

        seedTask(task(demoUser.getId(), "職務経歴書を更新する", "最新の実績を反映し、応募先に合わせて表現を調整する", LocalDate.now().plusDays(1), 5, 5, 90, TaskStatus.IN_PROGRESS, "転職活動"));
        seedTask(task(demoUser.getId(), "ポートフォリオのAPIを作る", "ログイン機能とCRUDに優先度計算APIを追加する", LocalDate.now().plusDays(4), 5, 4, 180, TaskStatus.TODO, "ポートフォリオ"));
        seedTask(task(demoUser.getId(), "Javaの復習をする", "設計、例外、コレクション周りを復習する", LocalDate.now().plusDays(6), 4, 3, 120, TaskStatus.TODO, "学習"));
        seedTask(task(demoUser.getId(), "企業研究をする", "志望動機と逆質問に備えて事業内容を整理する", LocalDate.now().plusDays(2), 4, 4, 60, TaskStatus.TODO, "転職活動"));
        seedTask(task(demoUser.getId(), "面談用の質問を整理する", "聞きたい内容を箇条書きにして優先順位を決める", LocalDate.now().minusDays(1), 5, 4, 45, TaskStatus.TODO, "面談"));
    }

    private TaskItem task(
            Long ownerId,
            String title,
            String description,
            LocalDate dueDate,
            int importance,
            int urgency,
            int estimatedMinutes,
            TaskStatus status,
            String category) {
        LocalDateTime now = LocalDateTime.now();
        TaskItem task = new TaskItem();
        task.setOwnerId(ownerId);
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(dueDate);
        task.setImportance(importance);
        task.setUrgency(urgency);
        task.setEstimatedMinutes(estimatedMinutes);
        task.setStatus(status);
        task.setCategory(category);
        task.setCreatedAt(now);
        task.setUpdatedAt(now);
        return task;
    }

    private void seedTask(TaskItem task) {
        taskItemMapper.insert(task);
    }
}
