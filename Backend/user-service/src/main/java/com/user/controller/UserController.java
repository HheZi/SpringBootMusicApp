package com.user.controller;

import com.user.enums.UserRole;
import com.user.payload.request.UserAuthRequest;
import com.user.payload.request.UserFormRequest;
import com.user.payload.response.ValidatedUser;
import com.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users/")
@RequiredArgsConstructor
public class UserController {

	private final UserService service;

	@PostMapping
	public Mono<ResponseEntity<?>> createUser(
			@Validated @RequestBody
			UserFormRequest dto,
			@RequestHeader("userRole") UserRole role
	) {
		return service.createNewUser(dto, role);

	}

	@PostMapping("/validate")
	public Mono<ValidatedUser> validate(@RequestBody UserAuthRequest req) {
		return service.validateUser(req);
	}

}
