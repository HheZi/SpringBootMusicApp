package com.user.controller;

import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.user.payload.request.UserAuthRequest;
import com.user.payload.request.UserFormRequest;
import com.user.payload.response.ValidatedUser;

@SpringBootTest
@TestPropertySource(locations = "classpath:/application-test.properties")
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

	@SuppressWarnings("unchecked")
	@Test
	void test_create_new_user_but_with_bad_payload() {
		UserFormRequest formRequest = new UserFormRequest();

		formRequest.setUsername(" ");
		formRequest.setEmail("email");
		formRequest.setPassword(" ");

		@SuppressWarnings("rawtypes")
		ArrayList responseBody = testClient.post().uri("/api/users/").contentType(MediaType.APPLICATION_JSON)
				.bodyValue(formRequest).exchange().expectStatus().is4xxClientError().expectBody(ArrayList.class)
				.returnResult().getResponseBody();

		assertThatCollection(responseBody).contains("The username can't be blank",
				"The email must follow the email template", "The password can't be blank");

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
		.expectStatus().isBadRequest()
		.expectBody()
		.jsonPath("$.message", "Wrong credentials");
		
	}
}
