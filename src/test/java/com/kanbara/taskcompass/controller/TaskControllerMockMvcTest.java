package com.kanbara.taskcompass.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import com.kanbara.taskcompass.mapper.AppUserMapper;
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
	void authenticatedUserCanAccessTasks() throws Exception {
		AppUser user = createUser();
		AppUserPrincipal principal = new AppUserPrincipal(user);

		mockMvc.perform(get("/tasks")
				.with(user(principal)))
				.andExpect(status().isOk())
				.andExpect(view().name("tasks/list"))
				.andExpect(model().attributeExists("currentUser", "query", "categories", "tasks"));
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

}