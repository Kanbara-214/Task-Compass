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
import com.kanbara.taskcompass.model.TaskSortOption;

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
        TaskItem expected = createTask(owner.getId(), "Expected", TaskStatus.TODO, "Test",
                LocalDate.now().plusDays(1), 3, 3, LocalDateTime.now());
        createTask(owner.getId(), "Different status", TaskStatus.DONE, "Test",
                LocalDate.now().plusDays(1), 3, 3, LocalDateTime.now().minusMinutes(1));
        createTask(owner.getId(), "Different category", TaskStatus.TODO, "Other",
                LocalDate.now().plusDays(1), 3, 3, LocalDateTime.now().minusMinutes(2));
        createTask(otherOwner.getId(), "Other owner", TaskStatus.TODO, "Test",
                LocalDate.now().plusDays(1), 3, 3, LocalDateTime.now().minusMinutes(3));

        List<TaskItem> tasks = taskItemMapper.findByOwnerIdAndListQuery(
                owner.getId(), TaskStatus.TODO, "test", TaskSortOption.UPDATED);

        assertThat(tasks)
                .extracting(TaskItem::getId)
                .containsExactly(expected.getId());
    }

    @Test
    void findByOwnerIdAndListQuerySortsByDeadline() {
        AppUser owner = createUser("Alice", "alice-deadline@example.com");
        TaskItem later = createTask(owner.getId(), "Later", TaskStatus.TODO, "Test",
                LocalDate.now().plusDays(5), 5, 5, LocalDateTime.now());
        TaskItem earlier = createTask(owner.getId(), "Earlier", TaskStatus.TODO, "Test",
                LocalDate.now().plusDays(1), 1, 1, LocalDateTime.now().minusMinutes(1));

        List<TaskItem> tasks = taskItemMapper.findByOwnerIdAndListQuery(
                owner.getId(), null, "", TaskSortOption.DEADLINE);

        assertThat(tasks)
                .extracting(TaskItem::getId)
                .containsExactly(earlier.getId(), later.getId());
    }

    @Test
    void findByOwnerIdAndListQuerySortsByPriority() {
        AppUser owner = createUser("Alice", "alice-priority@example.com");
        TaskItem lowPriority = createTask(owner.getId(), "Low priority", TaskStatus.TODO, "Test",
                LocalDate.now().plusDays(1), 2, 5, LocalDateTime.now());
        TaskItem highPriority = createTask(owner.getId(), "High priority", TaskStatus.TODO, "Test",
                LocalDate.now().plusDays(3), 5, 1, LocalDateTime.now().minusMinutes(1));

        List<TaskItem> tasks = taskItemMapper.findByOwnerIdAndListQuery(
                owner.getId(), null, "", TaskSortOption.PRIORITY);

        assertThat(tasks)
                .extracting(TaskItem::getId)
                .containsExactly(highPriority.getId(), lowPriority.getId());
    }

    @Test
    void findByOwnerIdAndListQuerySortsByUpdatedAt() {
        AppUser owner = createUser("Alice", "alice-updated@example.com");
        TaskItem oldTask = createTask(owner.getId(), "Old", TaskStatus.TODO, "Test",
                LocalDate.now().plusDays(1), 5, 5, LocalDateTime.now().minusDays(1));
        TaskItem newTask = createTask(owner.getId(), "New", TaskStatus.TODO, "Test",
                LocalDate.now().plusDays(2), 1, 1, LocalDateTime.now());

        List<TaskItem> tasks = taskItemMapper.findByOwnerIdAndListQuery(
                owner.getId(), null, "", TaskSortOption.UPDATED);

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
            String title,
            TaskStatus status,
            String category,
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
