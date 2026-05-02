package com.kanbara.taskcompass.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kanbara.taskcompass.entity.AppUser;
import com.kanbara.taskcompass.entity.TaskItem;
import com.kanbara.taskcompass.entity.TaskStatus;
import com.kanbara.taskcompass.query.TaskListQuery;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TaskItemMapperTest {

    @Autowired
    private TaskItemMapper taskItemMapper;

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void findByOwnerIdAndListQueryFiltersByOwnerStatusAndCategory() {
        AppUser owner = createUser("Alice", "alice-filter@example.com");
        AppUser otherOwner = createUser("Jack", "jack-filter@example.com");
        TaskItem expected = createTask(owner.getId(), "Test", TaskStatus.TODO, "Expected",
                LocalDate.now().plusDays(1), 3, 3, LocalDateTime.now());
        createTask(owner.getId(), "Test", TaskStatus.DONE, "Different status",
                LocalDate.now().plusDays(1), 3, 3, LocalDateTime.now().minusMinutes(1));
        createTask(owner.getId(), "Other", TaskStatus.TODO, "Different category",
                LocalDate.now().plusDays(1), 3, 3, LocalDateTime.now().minusMinutes(2));
        createTask(otherOwner.getId(), "Test", TaskStatus.TODO, "Other owner",
                LocalDate.now().plusDays(1), 3, 3, LocalDateTime.now().minusMinutes(3));
        TaskListQuery query = TaskListQuery.of("Test", "TODO", "recommended");

        List<TaskItem> tasks = taskItemMapper.findByOwnerIdAndListQuery(
                owner.getId(), query);

        assertThat(tasks)
                .extracting(TaskItem::getId)
                .containsExactly(expected.getId());
    }

    @Test
    void findByOwnerIdAndListQuerySortsByDeadline() {
        AppUser owner = createUser("Alice", "alice-deadline@example.com");
        TaskItem later = createTask(owner.getId(), "Test", TaskStatus.TODO, "Later",
                LocalDate.now().plusDays(5), 5, 5, LocalDateTime.now());
        TaskItem earlier = createTask(owner.getId(), "Test", TaskStatus.TODO, "Earlier",
                LocalDate.now().plusDays(1), 1, 1, LocalDateTime.now().minusMinutes(1));
        TaskListQuery query = TaskListQuery.of("", "TODO", "deadline");

        List<TaskItem> tasks = taskItemMapper.findByOwnerIdAndListQuery(
                owner.getId(), query);

        assertThat(tasks)
                .extracting(TaskItem::getId)
                .containsExactly(earlier.getId(), later.getId());
    }

    @Test
    void findByOwnerIdAndListQuerySortsByPriority() {
        AppUser owner = createUser("Alice", "alice-priority@example.com");
        TaskItem lowPriority = createTask(owner.getId(), "Test", TaskStatus.TODO, "Low priority",
                LocalDate.now().plusDays(1), 2, 5, LocalDateTime.now());
        TaskItem highPriority = createTask(owner.getId(), "Test", TaskStatus.TODO, "High priority",
                LocalDate.now().plusDays(3), 5, 1, LocalDateTime.now().minusMinutes(1));
        TaskListQuery query = TaskListQuery.of("", "TODO", "priority");

        List<TaskItem> tasks = taskItemMapper.findByOwnerIdAndListQuery(
                owner.getId(), query);

        assertThat(tasks)
                .extracting(TaskItem::getId)
                .containsExactly(highPriority.getId(), lowPriority.getId());
    }

    @Test
    void findByOwnerIdAndListQuerySortsByUpdatedAt() {
        AppUser owner = createUser("Alice", "alice-updated@example.com");
        TaskItem oldTask = createTask(owner.getId(), "Test", TaskStatus.TODO, "Old",
                LocalDate.now().plusDays(1), 5, 5, LocalDateTime.now().minusDays(1));
        TaskItem newTask = createTask(owner.getId(), "Test", TaskStatus.TODO, "New",
                LocalDate.now().plusDays(2), 1, 1, LocalDateTime.now());
        TaskListQuery query = TaskListQuery.of("", "TODO", "updatedAt");

        List<TaskItem> tasks = taskItemMapper.findByOwnerIdAndListQuery(
                owner.getId(), query);

        assertThat(tasks)
                .extracting(TaskItem::getId)
                .containsExactly(newTask.getId(), oldTask.getId());
    }

    private AppUser createUser(String displayName, String email) {
        AppUser user = new AppUser();
        user.setDisplayName(displayName);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode("password123"));
        user.setCreatedAt(LocalDateTime.now());
        appUserMapper.insert(user);
        return user;
    }

    private TaskItem createTask(
            Long ownerId,
            String category,
            TaskStatus status,
            String title,
            LocalDate dueDate,
            int importance,
            int urgency,
            LocalDateTime updatedAt) {
        TaskItem task = new TaskItem();
        task.setOwnerId(ownerId);
        task.setTitle(title);
        task.setDescription(title + " description");
        task.setDueDate(dueDate);
        task.setImportance(importance);
        task.setUrgency(urgency);
        task.setEstimatedMinutes(60);
        task.setStatus(status);
        task.setCategory(category);
        task.setCreatedAt(updatedAt.minusMinutes(10));
        task.setUpdatedAt(updatedAt);
        taskItemMapper.insert(task);
        return task;
    }
}
