package com.auth.service;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.auth.model.AuthRequest;
import com.auth.model.UserDetails;
import com.auth.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {
	
	private final JwtUtil jwtUtil;
	
	private final RestTemplate restTemplate = new RestTemplate();
	
	@SneakyThrows
	public String loginUser(AuthRequest authRequest) {
		UserDetails userDetails = restTemplate.getForEntity(new URI("http://UserService/api/users/validate"), UserDetails.class).getBody();
		if (userDetails == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wrong login or password");
		}
		
		return jwtUtil.createJwtToken(userDetails);
	}
	
}
