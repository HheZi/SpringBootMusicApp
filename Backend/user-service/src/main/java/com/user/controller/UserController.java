package com.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.payload.request.UserAuthRequest;
import com.user.payload.request.UserFormRequest;
import com.user.payload.response.ValidatedUser;
import com.user.service.UserService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users/")
@RequiredArgsConstructor
public class UserController {

	private final UserService service;

	@PostMapping
	public Mono<ResponseEntity<?>> createUser(@Validated @RequestBody(required = true) Mono<UserFormRequest> dto) {
		return dto.flatMap(service::createNewUser);

	}

	@PostMapping("/validate")
	public Mono<ValidatedUser> validate(@RequestBody UserAuthRequest req) {
		return service.validateUser(req);
	}

}
