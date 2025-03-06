package com.user.util;

import com.user.model.User;
import com.user.payload.request.UserFormRequest;
import com.user.payload.response.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

	public UserDetails fromUserToUserDetails(User user) {
		return UserDetails.builder()
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
