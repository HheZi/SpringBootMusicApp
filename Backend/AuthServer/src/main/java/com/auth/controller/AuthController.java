package com.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.auth.model.AuthRequest;
import com.auth.service.AuthService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class AuthController {
	
	private final AuthService authService; 
	
	@PostMapping("/login")
	public Mono<String> login(@RequestBody AuthRequest authRequest) {
		return authService.loginUser(authRequest);
	}
	
	
}
