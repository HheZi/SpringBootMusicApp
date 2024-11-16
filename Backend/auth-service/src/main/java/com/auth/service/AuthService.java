package com.auth.service;


import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.auth.model.RefreshToken;
import com.auth.payload.request.AuthRequest;
import com.auth.payload.request.RefreshTokenRequest;
import com.auth.payload.response.AuthResponse;
import com.auth.payload.response.JwtTokenResponse;
import com.auth.payload.response.UserDetails;
import com.auth.repository.RefreshTokenRepository;
import com.auth.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {
	
	private final JwtUtil jwtUtil;
	
	private final WebClient.Builder webClient;
	
	private final RefreshTokenRepository refreshTokenRepository;
	
	private final Integer EXPIRATION_DATE_OF_REFRESH_TOKEN_IN_DAYS = 30;
	
	public Mono<AuthResponse> loginUser(AuthRequest authRequest) {
		return webClient.build()
                .post()
                .uri("http://user-service/api/users/validate")
                .bodyValue(authRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Bad Credential")))
                .bodyToMono(UserDetails.class)
                .flatMap(this::generateResponseForLogin);
	}
	
	@Transactional
	private Mono<AuthResponse> generateResponseForLogin(UserDetails details) {
		String jwtToken = jwtUtil.createJwtToken(details.getId());
		UUID refreshTokenUUID = UUID.randomUUID();
		
		return refreshTokenRepository.findByUserId(details.getId())
				.switchIfEmpty(Mono.just(new RefreshToken(details.getId())))
				.doOnNext(t -> {
					t.setToken(refreshTokenUUID);
					t.setExpirationDate(Instant.now().plus(EXPIRATION_DATE_OF_REFRESH_TOKEN_IN_DAYS, ChronoUnit.DAYS));
				})
				.flatMap(refreshTokenRepository::save)
				.map(t -> new AuthResponse(jwtToken, t.getToken().toString()));
	}

	public Mono<AuthResponse> refreshJWTToken(RefreshTokenRequest refreshToken) {
		if (refreshToken.getRefreshToken() == null) 
			return Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You need to login"));
		
		
		return refreshTokenRepository.findByToken(UUID.fromString(refreshToken.getRefreshToken()))
				.filter(t -> t.getExpirationDate().compareTo(Instant.now()) > 0)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You need to login")))
				.flatMap(t -> {
					t.setToken(UUID.randomUUID());
					return refreshTokenRepository.save(t);
				})
				.map(t -> new AuthResponse(jwtUtil.createJwtToken(t.getUserId()), t.getToken().toString()));
	}
	
}
