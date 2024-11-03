package com.user.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
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
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "The user is not found")))
				.doOnNext(t -> {
					if (!encoder.matches(authRequest.getPassword(), t.getPassword())) {
						throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong credentials");
					}
				})
				.map(userMapper::fromUserToValidatedUser);

	}

	public void createNewUser(UserFormRequest formRequest) {
		User user = userMapper.fromUserFormRequestToUser(formRequest, encoder.encode(formRequest.getPassword()));

		userRepository.save(user);
	}
}
