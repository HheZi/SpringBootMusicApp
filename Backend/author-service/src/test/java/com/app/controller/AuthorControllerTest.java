package com.app.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.app.payload.request.AuthorCreateOrUpdateRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@TestPropertySource(locations = "classpath:/application-test.properties")
@AutoConfigureWebTestClient
@Disabled
class AuthorControllerTest {

	@Autowired
	private WebTestClient testClient;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Test
	void test_get_author_method() {
		String nameOfAuthor = "First";
		
		testClient.get()
		.uri("/api/authors/" + 1)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.jsonPath("$.id").isEqualTo(1)
		.jsonPath("$.name").isEqualTo(nameOfAuthor);
	}

	@Test
	void test_find_author_by_start_symbol() {
		String nameOfAuthor = "Second";
		
		testClient.get()
		.uri("/api/authors/symbol/" + nameOfAuthor.toLowerCase().substring(0, 3))
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.jsonPath("$[0].id").isEqualTo(2)
		.jsonPath("$[0].name").isEqualTo(nameOfAuthor);
		
	}
	
	@Test
	void test_create_author_method() throws JsonProcessingException {
		
//		AuthorCreateOrUpdateRequest request = new AuthorCreateOrUpdateRequest("New", null);
//		
//		testClient.post()
//		.uri("/api/authors/")
//		.contentType(MediaType.APPLICATION_JSON)
//		.bodyValue(mapper.writeValueAsBytes(request))
//		.exchange()
//		.expectStatus().isCreated();
		
	}
}
