package com.kanbara.taskcompass.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import com.kanbara.taskcompass.entity.AppUser;

@Mapper
public interface AppUserMapper {

	@Select("""
			select id, display_name, email, password_hash, created_at
			from app_users
			where email = #{email}
			""")
	AppUser findByEmail(String email);

	@Select("""
			select count(*)
			from app_users
			""")
	long countUsers();

	@Insert("""
			insert into app_users (
			    display_name,
			    email,
			    password_hash,
			    created_at
			) values (
			    #{displayName},
			    #{email},
			    #{passwordHash},
			    #{createdAt}
			)
			""")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	int insert(AppUser user);
}
