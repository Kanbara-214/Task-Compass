package com.kanbara.taskcompass.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.kanbara.taskcompass.entity.AppUser;
import com.kanbara.taskcompass.entity.TaskItem;
import com.kanbara.taskcompass.entity.TaskStatus;
import com.kanbara.taskcompass.mapper.AppUserMapper;
import com.kanbara.taskcompass.mapper.TaskItemMapper;
import com.kanbara.taskcompass.security.AppUserPrincipal;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TaskControllerMockMvcTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	AppUserMapper appUserMapper;

	@Autowired
	TaskItemMapper taskItemMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@ParameterizedTest(name = "[{index}] unauthenticated GET {0} redirects to /login")
	@ValueSource(strings = {
			"/tasks",
			"/tasks/new",
			"/tasks/1",
			"/tasks/1/edit"
	})
	void unauthenticatedUserIsRedirectedToLoginWhenAccessingTasks(String path) throws Exception {
		mockMvc.perform(get(path))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrlPattern("**/login"));
	}

	@Test
	void authenticatedUserCanAccessTaskList() throws Exception {
		AppUser user = createUser("Alice", "alice@example.com");
		AppUserPrincipal principal = new AppUserPrincipal(user);

		mockMvc.perform(get("/tasks")
				.with(user(principal)))
				.andExpect(status().isOk())
				.andExpect(view().name("tasks/list"))
				.andExpect(model().attributeExists("currentUser", "query", "categories", "tasks"));
	}

	@Test
	void authenticatedUserCanAccessTasksCreateForm() throws Exception {
		AppUser user = createUser("Alice", "alice@example.com");
		AppUserPrincipal principal = new AppUserPrincipal(user);

		mockMvc.perform(get("/tasks/new")
				.with(user(principal)))
				.andExpect(status().isOk())
				.andExpect(view().name("tasks/form"))
				.andExpect(model().attributeExists("currentUser", "taskForm", "categories", "formTitle",
						"formAction"));

	}

	@Test
	void authenticatedUserCanAccessTasksDetail() throws Exception {
		AppUser user = createUser("Alice", "alice@example.com");
		AppUserPrincipal principal = new AppUserPrincipal(user);
		Long taskId = createTaskItemForTests(user.getId());

		mockMvc.perform(get("/tasks/" + taskId)
				.with(user(principal)))
				.andExpect(status().isOk())
				.andExpect(view().name("tasks/detail"))
				.andExpect(model().attributeExists("currentUser", "task"));

	}

	@Test
	void authenticatedUserCannotAccessOthersTasksDetail() throws Exception {
		AppUser user1 = createUser("Alice", "alice@example.com"), user2 = createUser("Jack", "jack@example.com");
		AppUserPrincipal principal = new AppUserPrincipal(user1);
		Long taskId = createTaskItemForTests(user2.getId());

		mockMvc.perform(get("/tasks/" + taskId)
				.with(user(principal)))
				.andExpect(status().isNotFound());
	}

	@Test
	void authenticatedUserCannotAccessNonexistentTaskDetail() throws Exception {
		AppUser user = createUser("Alice", "alice@example.com");
		AppUserPrincipal principal = new AppUserPrincipal(user);
		Long taskId = createTaskItemForTests(user.getId());
		Long nonexistentTaskId = taskId + 1; // 存在しないID

		mockMvc.perform(get("/tasks/" + nonexistentTaskId)
				.with(user(principal)))
				.andExpect(status().isNotFound());
	}

	@Test
	void authenticatedUserCannotUpdateOthersTasks() throws Exception {
		AppUser user1 = createUser("Alice", "alice@example.com"), user2 = createUser("Jack", "jack@example.com");
		AppUserPrincipal principal = new AppUserPrincipal(user1);
		Long taskId = createTaskItemForTests(user2.getId());
		TaskItem before = taskItemMapper.findByIdAndOwnerId(taskId, user2.getId());

		mockMvc.perform(post("/tasks/" + taskId)
				.with(user(principal))
				.with(csrf())
				.param("title", "task updated by Alice")
				.param("description", "validation test")
				.param("dueDate", LocalDate.now().plusDays(1).toString())
				.param("importance", "3")
				.param("urgency", "3")
				.param("estimatedMinutes", "60")
				.param("status", "TODO")
				.param("category", "学習"))
				.andExpect(status().isNotFound());

		TaskItem after = taskItemMapper.findByIdAndOwnerId(taskId, user2.getId());
		assertEquals(before.getTitle(), after.getTitle(), "Task title should not be updated");
		assertEquals(before.getUpdatedAt(), after.getUpdatedAt(), "Task updatedAt should not be updated");
	}

	@Test
	void authenticatedUserCannotUpdateNonexistentTask() throws Exception {
		AppUser user = createUser("Alice", "alice@example.com");
		AppUserPrincipal principal = new AppUserPrincipal(user);
		Long taskId = createTaskItemForTests(user.getId());
		Long nonexistentTaskId = taskId + 1; // 存在しないID
		int before = taskItemMapper.findByOwnerIdOrderByUpdatedAtDesc(user.getId()).size();

		mockMvc.perform(post("/tasks/" + nonexistentTaskId)
				.with(user(principal))
				.with(csrf())
				.param("title", "task updated by Alice")
				.param("description", "validation test")
				.param("dueDate", LocalDate.now().plusDays(1).toString())
				.param("importance", "3")
				.param("urgency", "3")
				.param("estimatedMinutes", "60")
				.param("status", "TODO")
				.param("category", "学習"))
				.andExpect(status().isNotFound());

		int after = taskItemMapper.findByOwnerIdOrderByUpdatedAtDesc(user.getId()).size();
		assertEquals(before, after, "Task should not be updated");
	}

	@Test
	void authenticatedUserCanAccessTasksEditForm() throws Exception {
		AppUser user = createUser("Alice", "alice@example.com");
		AppUserPrincipal principal = new AppUserPrincipal(user);
		Long taskId = createTaskItemForTests(user.getId());

		mockMvc.perform(get("/tasks/" + taskId + "/edit")
				.with(user(principal)))
				.andExpect(status().isOk())
				.andExpect(view().name("tasks/form"))
				.andExpect(model().attributeExists("currentUser", "taskForm", "categories", "formTitle",
						"formAction", "taskId"));

	}

	@Test
	void authenticatedUserCannotAccessOthersTasksEditForm() throws Exception {
		AppUser user1 = createUser("Alice", "alice@example.com"), user2 = createUser("Jack", "jack@example.com");
		AppUserPrincipal principal = new AppUserPrincipal(user1);
		Long taskId = createTaskItemForTests(user2.getId());

		mockMvc.perform(get("/tasks/" + taskId + "/edit")
				.with(user(principal)))
				.andExpect(status().isNotFound());
	}

	@Test
	void authenticatedUserCannotAccessNonexistentTaskEditForm() throws Exception {
		AppUser user = createUser("Alice", "alice@example.com");
		AppUserPrincipal principal = new AppUserPrincipal(user);
		Long taskId = createTaskItemForTests(user.getId());
		Long nonexistentTaskId = taskId + 1; // 存在しないID

		mockMvc.perform(get("/tasks/" + nonexistentTaskId + "/edit")
				.with(user(principal)))
				.andExpect(status().isNotFound());
	}

	@Test
	void authenticatedUserCannotDeleteOthersTask() throws Exception {
		AppUser user1 = createUser("Alice", "alice@example.com"), user2 = createUser("Jack", "jack@example.com");
		AppUserPrincipal principal = new AppUserPrincipal(user1);
		Long taskId = createTaskItemForTests(user2.getId());
		int before = taskItemMapper.findByOwnerIdOrderByUpdatedAtDesc(user2.getId()).size();

		mockMvc.perform(post("/tasks/" + taskId + "/delete")
				.with(user(principal))
				.with(csrf()))
				.andExpect(status().isNotFound());

		int after = taskItemMapper.findByOwnerIdOrderByUpdatedAtDesc(user2.getId()).size();
		assertEquals(before, after, "Task should not be deleted");
	}

	@Test
	void authenticatedUserCannotDeleteNonexistentTask() throws Exception {
		AppUser user = createUser("Alice", "alice@example.com");
		AppUserPrincipal principal = new AppUserPrincipal(user);
		Long taskId = createTaskItemForTests(user.getId());
		Long nonexistentTaskId = taskId + 1; // 存在しないID
		int before = taskItemMapper.findByOwnerIdOrderByUpdatedAtDesc(user.getId()).size();

		mockMvc.perform(post("/tasks/" + nonexistentTaskId + "/delete")
				.with(user(principal))
				.with(csrf()))
				.andExpect(status().isNotFound());

		int after = taskItemMapper.findByOwnerIdOrderByUpdatedAtDesc(user.getId()).size();
		assertEquals(before, after, "Task should not be deleted");
	}

	@Test
	void authenticatedUserSubmittingInvalidTaskReturnsCreateFormWithErrors() throws Exception {
		AppUser user = createUser("Alice", "alice@example.com");
		AppUserPrincipal principal = new AppUserPrincipal(user);
		int before = taskItemMapper.findByOwnerIdOrderByUpdatedAtDesc(user.getId()).size();

		mockMvc.perform(post("/tasks")
				.with(user(principal))
				.with(csrf())
				.param("title", "") // NotBlank違反
				.param("description", "validation test")
				.param("dueDate", LocalDate.now().plusDays(1).toString())
				.param("importance", "3")
				.param("urgency", "3")
				.param("estimatedMinutes", "60")
				.param("status", "TODO")
				.param("category", "学習"))
				.andExpect(status().isOk())
				.andExpect(view().name("tasks/form"))
				.andExpect(model().attributeExists(
						"taskForm", "currentUser", "categories", "formTitle", "formAction"))
				.andExpect(model().attributeHasFieldErrors("taskForm", "title"));

		int after = taskItemMapper.findByOwnerIdOrderByUpdatedAtDesc(user.getId()).size();
		assertEquals(before, after, "Task should not be created");
	}

	@Test
	void authenticatedUserSubmittingInvalidTaskReturnsEditFormWithErrors() throws Exception {
		AppUser user = createUser("Alice", "alice@example.com");
		AppUserPrincipal principal = new AppUserPrincipal(user);
		Long taskId = createTaskItemForTests(user.getId());
		TaskItem before = taskItemMapper.findByIdAndOwnerId(taskId, user.getId());

		mockMvc.perform(post("/tasks/" + taskId)
				.with(user(principal))
				.with(csrf())
				.param("title", "") // NotBlank違反
				.param("description", "validation test")
				.param("dueDate", LocalDate.now().plusDays(1).toString())
				.param("importance", "3")
				.param("urgency", "3")
				.param("estimatedMinutes", "60")
				.param("status", "TODO")
				.param("category", "学習"))
				.andExpect(status().isOk())
				.andExpect(view().name("tasks/form"))
				.andExpect(model().attributeExists(
						"taskForm", "currentUser", "categories", "formTitle", "formAction", "taskId"))
				.andExpect(model().attributeHasFieldErrors("taskForm", "title"));

		TaskItem after = taskItemMapper.findByIdAndOwnerId(taskId, user.getId());
		assertEquals(before.getTitle(), after.getTitle(), "Task title should remain unchanged");
		assertEquals(before.getUpdatedAt(), after.getUpdatedAt(), "Task updatedAt should remain unchanged");

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

	private Long createTaskItemForTests(Long ownerId) {
		TaskItem task = new TaskItem();
		task.setOwnerId(ownerId);
		task.setTitle("Task for tests");
		task.setDescription("This task is for tests");
		task.setDueDate(LocalDate.now());
		task.setImportance(1);
		task.setUrgency(1);
		task.setEstimatedMinutes(30);
		task.setStatus(TaskStatus.IN_PROGRESS);
		task.setCategory("Test");
		task.setCreatedAt(LocalDateTime.now());
		task.setUpdatedAt(LocalDateTime.now());

		taskItemMapper.insert(task);
		return task.getId();
	}

}