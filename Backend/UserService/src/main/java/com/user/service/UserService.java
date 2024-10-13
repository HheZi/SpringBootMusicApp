package com.user.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.user.model.User;
import com.user.model.projection.UserAuthRequest;
import com.user.model.projection.UserFormRequest;
import com.user.model.projection.ValidatedUser;
import com.user.repository.UserRepository;
import com.user.util.UserMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
	
	private final PasswordEncoder encoder;
	
	private final UserRepository userRepository;
	
	private final UserMapper userMapper;
	
	public ValidatedUser validateUser(UserAuthRequest authRequest) {
		log.warn("Username is {}", authRequest.getUsername());
		User user = userRepository.findByUsername(authRequest.getUsername())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User is not found"));	
		
		if (!encoder.matches(authRequest.getPassword(), user.getPassword())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong credentials");
		}
		
		return userMapper.fromUserToValidatedUser(user);
	}
	
	public void createNewUser(UserFormRequest formRequest) {
		User user = userMapper.fromUserFormRequestToUser(formRequest, encoder.encode(formRequest.getPassword()));
		
		userRepository.save(user);
	}
}
