package com.app.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.app.model.projection.AuthorResponse;

@SpringBootTest
@TestPropertySource(locations = "classpath:/application-test.properties")
@AutoConfigureWebTestClient
class AuthorControllerTest {

	@Autowired
	private WebTestClient testClient;
	
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
	void test_create_author_method() {
		String nameOfNewAuthor = "New";
		
		testClient.post()
		.uri("/api/authors/")
		.bodyValue(nameOfNewAuthor)
		.exchange()
		.expectStatus().isCreated();
		
	}
}
