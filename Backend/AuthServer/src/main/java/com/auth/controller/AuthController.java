package com.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth.payload.request.AuthRequest;
import com.auth.payload.request.RefreshTokenRequest;
import com.auth.payload.response.AuthResponse;
import com.auth.payload.response.JwtTokenResponse;
import com.auth.service.AuthService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	
	private final AuthService authService; 
	
	@PostMapping("/login")
	public Mono<AuthResponse> login(@RequestBody Mono<AuthRequest> authRequest) {
		return authRequest.flatMap(authService::loginUser);
	}
	
	@PostMapping("/refresh")
	public Mono<JwtTokenResponse> refreshJWTtoken(@RequestBody Mono<RefreshTokenRequest> refreshToken){
		return refreshToken.flatMap(authService::refreshJWTToken);
	}
	
}
