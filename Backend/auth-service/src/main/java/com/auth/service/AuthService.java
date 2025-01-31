package com.auth.service;


import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {
	
	private final JwtUtil jwtUtil;
	
	private final UserWebService service;
	
	private final RefreshTokenRepository refreshTokenRepository;
	
	@Value("${refreshToken.expirationInDays}")
	private Integer EXPIRATION_DATE_OF_REFRESH_TOKEN_IN_DAYS;
	
	@Transactional
	public Mono<AuthResponse> loginUser(AuthRequest authRequest) {
		Mono<UserDetails> userCredential = service.getUserDetails(authRequest);
		return userCredential
				.flatMap(t -> refreshTokenRepository.findByUserId(t.getId()))
				.switchIfEmpty(userCredential.map(t -> new RefreshToken(t.getId())))
				.doOnNext(t -> {
					t.setToken(UUID.randomUUID());
					t.setExpirationDate(Instant.now().plus(EXPIRATION_DATE_OF_REFRESH_TOKEN_IN_DAYS, ChronoUnit.DAYS));
				})
				.flatMap(refreshTokenRepository::save)
				.map(t -> new AuthResponse(jwtUtil.createJwtToken(t.getUserId()), t.getToken().toString()));
	}

	public Mono<JwtTokenResponse> refreshJWTToken(RefreshTokenRequest refreshToken) {
		if (refreshToken.getRefreshToken() == null) 
			return Mono.error(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
		
		return refreshTokenRepository
				.findByToken(refreshToken.getRefreshToken())
				.filter(t -> t.getExpirationDate().isAfter(Instant.now()))
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN)))
				.map(t -> new JwtTokenResponse(jwtUtil.createJwtToken(t.getUserId())));
	}
	
}
