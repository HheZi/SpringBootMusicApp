package com.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.auth.model.AuthRequest;
import com.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {
	
	private final AuthService authService; 
	
	@GetMapping("/login")
	public String login(@RequestBody AuthRequest authRequest) {
		return authService.loginUser(authRequest);
	}
	
	
}
