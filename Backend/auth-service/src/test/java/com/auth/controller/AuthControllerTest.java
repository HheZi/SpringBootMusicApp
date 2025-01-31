package com.auth.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import java.util.UUID;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.auth.payload.request.AuthRequest;
import com.auth.payload.request.RefreshTokenRequest;
import com.auth.payload.response.AuthResponse;
import com.auth.payload.response.JwtTokenResponse;
import com.auth.payload.response.UserDetails;
import com.auth.service.UserWebService;
import com.auth.util.JwtUtil;

import reactor.core.publisher.Mono;

@SpringBootTest
@AutoConfigureWebTestClient
class AuthControllerTest {

	@Autowired
	private WebTestClient testClient;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@MockBean
	private UserWebService service;
	
	@Test
	@Order(1)
	void test_login() {
		AuthRequest authRequest = configBeforeBeforeRequest();
		
		testClient.post()
		.uri("/api/auth/login")
		.bodyValue(authRequest)
		.exchange()
		.expectStatus().isOk()
		.expectBody(AuthResponse.class)
		.value(t -> assertFalse(jwtUtil.isExpired(t.getToken())));
	}

	@Test
	void test_refresh_token() {
		AuthRequest authRequest = configBeforeBeforeRequest();
		
		AuthResponse responseBody = testClient.post()
		.uri("/api/auth/login")
		.bodyValue(authRequest)
		.exchange()
		.expectBody(AuthResponse.class)
		.returnResult().getResponseBody();
		
		RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(UUID.fromString(responseBody.getRefreshToken()));
		
		testClient.post()
		.uri("/api/auth/refresh")
		.bodyValue(refreshTokenRequest)
		.exchange()
		.expectStatus().isOk()
		.expectBody(JwtTokenResponse.class)
		.value(t -> assertFalse(jwtUtil.isExpired(t.getToken())));
	}

	private AuthRequest configBeforeBeforeRequest() {
		doReturn(Mono.just(new UserDetails(2, "test"))).when(service).getUserDetails(any());
		
		AuthRequest authRequest = new AuthRequest("test", "12345");
		return authRequest;
	}
	
}
