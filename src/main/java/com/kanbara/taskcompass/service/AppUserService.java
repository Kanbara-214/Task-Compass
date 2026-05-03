package com.kanbara.taskcompass.service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kanbara.taskcompass.entity.AppUser;
import com.kanbara.taskcompass.form.RegistrationForm;
import com.kanbara.taskcompass.mapper.AppUserMapper;

@Service
public class AppUserService {

	private final AppUserMapper appUserMapper;
	private final PasswordEncoder passwordEncoder;

	public AppUserService(AppUserMapper appUserMapper, PasswordEncoder passwordEncoder) {
		this.appUserMapper = appUserMapper;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public AppUser register(RegistrationForm form) {
		String email = form.getEmail().trim().toLowerCase(Locale.ROOT);
		if (findByEmail(email).isPresent()) {
			throw new IllegalArgumentException("このメールアドレスは既に登録されています");
		}
		if (!form.getPassword().equals(form.getConfirmPassword())) {
			throw new IllegalArgumentException("確認用パスワードが一致しません");
		}

		AppUser user = new AppUser();
		user.setDisplayName(form.getDisplayName().trim());
		user.setEmail(email);
		user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
		user.setCreatedAt(LocalDateTime.now());
		appUserMapper.insert(user);
		return user;
	}

	@Transactional(readOnly = true)
	public AppUser requireByEmail(String email) {
		return findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));
	}

	private Optional<AppUser> findByEmail(String email) {
		return Optional.ofNullable(appUserMapper.findByEmail(email));
	}
}
