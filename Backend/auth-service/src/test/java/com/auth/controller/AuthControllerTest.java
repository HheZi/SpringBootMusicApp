package com.auth.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClient;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.auth.payload.request.AuthRequest;
import com.auth.util.JwtUtil;

@SpringBootTest
class AuthControllerTest {

	@Autowired
	private WebTestClient testClient;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@BeforeAll
	private static void configureWireMock() {
		stubFor(post(urlEqualTo("/api/users/validate"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.withBody("{\"username\": \"test\", \"id\": \"1\"}")
						));
		
	}

	@Test
	void test_login_method() {
		AuthRequest authRequest = new AuthRequest("test", "test");
		
		String responseBody = testClient.post()
		.bodyValue(authRequest)
		.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
		.exchange()
		.expectStatus().isOk()
		.expectBody(String.class)
		.returnResult().getResponseBody();
	
		assertThat(responseBody).isNotEmpty(); 
		
		assertFalse(jwtUtil.isExpired(responseBody));
	}

}
