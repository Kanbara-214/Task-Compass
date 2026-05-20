package com.kanbara.taskcompass.controller;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.kanbara.taskcompass.entity.AppUser;
import com.kanbara.taskcompass.entity.TaskItem;
import com.kanbara.taskcompass.entity.TaskStatus;
import com.kanbara.taskcompass.mapper.AppUserMapper;
import com.kanbara.taskcompass.mapper.TaskItemMapper;
import com.kanbara.taskcompass.model.DashboardView;
import com.kanbara.taskcompass.security.AppUserPrincipal;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DashboardControllerMockMvcTest {

	private final MockMvc mockMvc;

	private final AppUserMapper appUserMapper;

	private final TaskItemMapper taskItemMapper;

	private final PasswordEncoder passwordEncoder;

	@Autowired
	DashboardControllerMockMvcTest(MockMvc mockMvc, AppUserMapper appUserMapper, TaskItemMapper taskItemMapper,
			PasswordEncoder passwordEncoder) {
		this.mockMvc = mockMvc;
		this.appUserMapper = appUserMapper;
		this.taskItemMapper = taskItemMapper;
		this.passwordEncoder = passwordEncoder;
	}

	@Test
	void unauthenticatedUserIsRedirectedToLoginWhenAccessingDashboard() throws Exception {
		mockMvc.perform(get("/dashboard"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrlPattern("**/login"));
	}

	@Test
	void dashboardUsesRequestedAvailableMinutes() throws Exception {
		AppUser user = createUser("Alice", "alice-dashboard-90@example.com");
		AppUserPrincipal principal = new AppUserPrincipal(user);
		TaskItem recommendedTask = createTask(
				user.getId(),
				TaskStatus.TODO,
				"Dashboard recommended task",
				LocalDateTime.now().plusDays(1),
				3,
				30);

		MvcResult result = mockMvc.perform(get("/dashboard")
				.param("availableMinutes", "90")
				.with(user(principal)))
				.andExpect(status().isOk())
				.andExpect(view().name("dashboard"))
				.andExpect(model().attributeExists("currentUser", "dashboard"))
				.andExpect(content().string(containsString("<option value=\"90\" selected=\"selected\">90")))
				.andExpect(content().string(containsString(recommendedTask.getTitle())))
				.andReturn();

		DashboardView dashboard = dashboardFrom(result);

		assertThat(dashboard.recommendationResult().availableMinutes()).isEqualTo(90);
		assertThat(dashboard.recommendationResult().recommendedTasks())
				.extracting(recommended -> recommended.candidate().id())
				.containsExactly(recommendedTask.getId());
	}

	@Test
	void dashboardFallsBackToDefaultAvailableMinutesWhenRequestValueIsInvalid() throws Exception {
		AppUser user = createUser("Alice", "alice-dashboard-invalid@example.com");
		AppUserPrincipal principal = new AppUserPrincipal(user);
		createTask(
				user.getId(),
				TaskStatus.TODO,
				"Test Invalid Minutes",
				LocalDateTime.now().plusDays(1),
				3,
				30);

		MvcResult result = mockMvc.perform(get("/dashboard")
				.param("availableMinutes", "17")
				.with(user(principal)))
				.andExpect(status().isOk())
				.andExpect(view().name("dashboard"))
				.andExpect(model().attributeExists("currentUser", "dashboard"))
				.andExpect(content().string(containsString("<option value=\"60\" selected=\"selected\">60")))
				.andReturn();

		DashboardView dashboard = dashboardFrom(result);

		assertThat(dashboard.recommendationResult().availableMinutes()).isEqualTo(60);
	}

	@Test
	void dashboardUsesDefaultAvailableMinutesWhenRequestValueIsOmitted() throws Exception {
		AppUser user = createUser("Alice", "alice-dashboard-default@example.com");
		AppUserPrincipal principal = new AppUserPrincipal(user);
		createTask(
				user.getId(),
				TaskStatus.TODO,
				"Test Default Minutes",
				LocalDateTime.now().plusDays(1),
				3,
				30);

		MvcResult result = mockMvc.perform(get("/dashboard")
				.with(user(principal)))
				.andExpect(status().isOk())
				.andExpect(view().name("dashboard"))
				.andExpect(model().attributeExists("currentUser", "dashboard"))
				.andExpect(content().string(containsString("<option value=\"60\" selected=\"selected\">60")))
				.andReturn();

		DashboardView dashboard = dashboardFrom(result);

		assertThat(dashboard.recommendationResult().availableMinutes()).isEqualTo(60);
	}

	private DashboardView dashboardFrom(MvcResult result) {
		Object dashboard = result.getModelAndView().getModel().get("dashboard");
		assertThat(dashboard).isInstanceOf(DashboardView.class);
		return (DashboardView) dashboard;
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
			TaskStatus status,
			String title,
			LocalDateTime dueDateTime,
			int importance,
			int estimatedMinutes) {
		TaskItem task = new TaskItem();
		task.setOwnerId(ownerId);
		task.setTitle(title);
		task.setDescription(title + " description");
		task.setDueDateTime(dueDateTime);
		task.setImportance(importance);
		task.setUrgency(1);
		task.setEstimatedMinutes(estimatedMinutes);
		task.setStatus(status);
		task.setCategory("Test");
		task.setCreatedAt(LocalDateTime.now().minusMinutes(10));
		task.setUpdatedAt(LocalDateTime.now());
		taskItemMapper.insert(task);
		return task;
	}
}
