package com.user.util;

import com.user.model.User;
import com.user.payload.request.UserFormRequest;
import com.user.payload.response.ValidatedUser;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

	public ValidatedUser fromUserToValidatedUser(User user) {
		return ValidatedUser.builder()
				.username(user.getUsername())
				.id(user.getId())
				.userRole(user.getUserRole())
				.build();
	}

	public User fromUserFormRequestToUser(UserFormRequest formRequest, String password) {
		return User.builder()
				.username(formRequest.getUsername())
				.password(password)
				.email(formRequest.getEmail())
				.userRole(formRequest.getUserRole())
				.build();
	}
}
