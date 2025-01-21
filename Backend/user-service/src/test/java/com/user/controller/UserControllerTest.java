package com.user.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.user.payload.request.UserAuthRequest;
import com.user.payload.request.UserFormRequest;
import com.user.payload.response.ValidatedUser;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

	@Autowired
	private WebTestClient testClient;

	@Test
	void  test_create_new_user() {
		UserFormRequest formRequest = new UserFormRequest();

		formRequest.setUsername("test3");
		formRequest.setEmail("email@gmail.com");
		formRequest.setPassword("12345");

		testClient.post().uri("/api/users/").contentType(MediaType.APPLICATION_JSON).bodyValue(formRequest).exchange()
				.expectStatus().isCreated();
	}

	@Test
	void test_create_new_user_but_with_bad_payload() {
		UserFormRequest formRequest = new UserFormRequest();

		formRequest.setUsername(" ");
		formRequest.setEmail("email");
		formRequest.setPassword(" ");

		testClient
		.post()
		.uri("/api/users/")
		.contentType(MediaType.APPLICATION_JSON)
		.bodyValue(formRequest)
		.exchange()
		.expectStatus().is4xxClientError()
		.expectBody();

	}

	@Test
	void test_validate_user() {

		UserAuthRequest userAuthRequest = new UserAuthRequest();

		userAuthRequest.setPassword("12345");
		userAuthRequest.setUsername("test");

		ValidatedUser expected = new ValidatedUser(1, "test");
		
		ValidatedUser responseBody = testClient.post().uri("/api/users/validate")
				.contentType(MediaType.APPLICATION_JSON).bodyValue(userAuthRequest).exchange().expectStatus()
				.is2xxSuccessful().expectBody(ValidatedUser.class).returnResult().getResponseBody();

		assertEquals(expected, responseBody);
		
	}
	
	@Test
	void test_validate_user_but_not_found_expected() {
		
		UserAuthRequest userAuthRequest = new UserAuthRequest();

		userAuthRequest.setPassword("12345");
		userAuthRequest.setUsername("user");
		
		testClient.post().uri("/api/users/validate")
		.contentType(MediaType.APPLICATION_JSON).bodyValue(userAuthRequest)
		.exchange()
		.expectStatus().isNotFound();
	}
	
	@Test
	void test_validate_user_but_bad_credential_expected() {
		
		UserAuthRequest userAuthRequest = new UserAuthRequest();

		userAuthRequest.setPassword("egerge");
		userAuthRequest.setUsername("test");
		
		testClient.post().uri("/api/users/validate")
		.contentType(MediaType.APPLICATION_JSON).bodyValue(userAuthRequest)
		.exchange()
		.expectStatus().isBadRequest();
		
	}
}
