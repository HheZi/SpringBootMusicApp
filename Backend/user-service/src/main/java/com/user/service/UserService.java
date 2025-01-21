package com.user.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.user.model.User;
import com.user.payload.request.UserAuthRequest;
import com.user.payload.request.UserFormRequest;
import com.user.payload.response.ValidatedUser;
import com.user.repository.UserRepository;
import com.user.util.UserMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

	private final PasswordEncoder encoder;

	private final UserRepository userRepository;

	private final UserMapper userMapper;

	public Mono<ValidatedUser> validateUser(UserAuthRequest authRequest) {
		return userRepository.findByUsername(authRequest.getUsername())
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.filter(t -> encoder.matches(authRequest.getPassword(), t.getPassword()))
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST)))
				.map(userMapper::fromUserToValidatedUser);

	}

	public Mono<ResponseEntity<?>> createNewUser(UserFormRequest formRequest) {
		User user = userMapper.fromUserFormRequestToUser(formRequest, encoder.encode(formRequest.getPassword()));

		return userRepository.save(user).map(t -> ResponseEntity.status(HttpStatus.CREATED).build());
	}
}
