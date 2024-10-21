package com.user.util;

import org.springframework.stereotype.Component;

import com.user.model.User;
import com.user.model.projection.UserFormRequest;
import com.user.model.projection.ValidatedUser;

@Component
public class UserMapper {

	public ValidatedUser fromUserToValidatedUser(User user) {
		return ValidatedUser.builder().username(user.getUsername()).id(user.getId()).build();
	}

	public User fromUserFormRequestToUser(UserFormRequest formRequest, String password) {
		return User.builder().username(formRequest.getUsername()).password(password).email(formRequest.getEmail())
				.build();
	}
}
