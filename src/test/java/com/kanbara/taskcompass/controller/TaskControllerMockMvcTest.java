package com.kanbara.taskcompass.controller;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
		AppUser user = createUser();
		AppUserPrincipal principal = new AppUserPrincipal(user);

		mockMvc.perform(get("/tasks")
				.with(user(principal)))
				.andExpect(status().isOk())
				.andExpect(view().name("tasks/list"))
				.andExpect(model().attributeExists("currentUser", "query", "categories", "tasks"));
	}

	@Test
	void authenticatedUserCanAccessTasksCreateForm() throws Exception {
		AppUser user = createUser();
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
		AppUser user = createUser();
		AppUserPrincipal principal = new AppUserPrincipal(user);
		Long taskId = createTaskItemForTests(user.getId());
		mockMvc.perform(get("/tasks/" + taskId)
				.with(user(principal)))
				.andExpect(status().isOk())
				.andExpect(view().name("tasks/detail"))
				.andExpect(model().attributeExists("currentUser", "task"));

	}

	@Test
	void authenticatedUserCanAccessTasksEditForm() throws Exception {
		AppUser user = createUser();
		AppUserPrincipal principal = new AppUserPrincipal(user);
		Long taskId = createTaskItemForTests(user.getId());
		mockMvc.perform(get("/tasks/" + taskId + "/edit")
				.with(user(principal)))
				.andExpect(status().isOk())
				.andExpect(view().name("tasks/form"))
				.andExpect(model().attributeExists("currentUser", "taskForm", "categories", "formTitle",
						"formAction", "taskId"));

	}

	private AppUser createUser() {
		AppUser user = new AppUser();
		user.setDisplayName("Alice");
		user.setEmail("alice@example.com");
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