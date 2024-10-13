package com.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.model.projection.UserAuthRequest;
import com.user.model.projection.UserFormRequest;
import com.user.model.projection.ValidatedUser;
import com.user.service.UserService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/users/")
@RequiredArgsConstructor
public class UserController {

	private final UserService service;
	
	@PostMapping
	public Mono<ResponseEntity<?>> createUser(@RequestBody UserFormRequest entity) {
		service.createNewUser(entity);
		
		return Mono.just(ResponseEntity.status(HttpStatus.CREATED).build());
	}
	
	@PostMapping("/validate")
	public Mono<ValidatedUser> validate(@RequestBody UserAuthRequest req){
		return Mono.just(service.validateUser(req));
	}
	
}
