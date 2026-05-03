package com.kanbara.taskcompass.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kanbara.taskcompass.mapper.AppUserMapper;
import com.kanbara.taskcompass.security.AppUserPrincipal;

@Service
public class AppUserDetailsService implements UserDetailsService {

	private final AppUserMapper appUserMapper;

	public AppUserDetailsService(AppUserMapper appUserMapper) {
		this.appUserMapper = appUserMapper;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return Optional.ofNullable(appUserMapper.findByEmail(username))
				.map(AppUserPrincipal::new)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}
}
