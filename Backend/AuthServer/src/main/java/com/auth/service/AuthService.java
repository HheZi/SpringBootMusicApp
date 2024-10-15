package com.auth.service;


import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
	
	private final WebClient.Builder webClient;
	
	@SneakyThrows
	public Mono<String> loginUser(AuthRequest authRequest) {
		return webClient.build()
                .post()
                .uri("http://UserService/api/users/validate")
                .bodyValue(authRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Credential")))
                .bodyToMono(UserDetails.class)
                .flatMap(userDetails -> {
                    return Mono.just(jwtUtil.createJwtToken(userDetails));
                });
	}
	
}
