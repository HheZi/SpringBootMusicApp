package com.user.service;

import com.user.enums.UserRole;
import com.user.exceptions.UserRuntimeException;
import com.user.payload.request.UserAuthRequest;
import com.user.payload.request.UserFormRequest;
import com.user.payload.response.ValidatedUser;
import com.user.repository.UserRepository;
import com.user.util.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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

	public Mono<ResponseEntity<?>> createNewUser(UserFormRequest formRequest, UserRole role) {
		return Mono.just(formRequest)
				.filter(t -> this.checkIfUserWithRoleCanBeCreated(t.getUserRole(), role))
				.switchIfEmpty(Mono.error(() -> new UserRuntimeException("Only admins allows to create admin user")))
				.map(request -> userMapper.fromUserFormRequestToUser(request, encoder.encode(formRequest.getPassword())))
				.flatMap(userRepository::save)
				.map(t -> ResponseEntity.status(HttpStatus.CREATED).build());
	}

	private boolean checkIfUserWithRoleCanBeCreated(UserRole userRoleOfRequest, UserRole role){
		boolean canBeAdminUserCreated = userRoleOfRequest == UserRole.ADMIN && role == UserRole.ADMIN;
		boolean canBeUserCreated = userRoleOfRequest == UserRole.USER;

		return canBeUserCreated || canBeAdminUserCreated;
	}
}
