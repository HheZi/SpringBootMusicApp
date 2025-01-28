package com.app.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import com.app.payload.request.AuthorCreateOrUpdateRequest;
import com.app.payload.response.AuthorResponse;
import com.app.service.WebService;
import com.fasterxml.jackson.core.JsonProcessingException;

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@AutoConfigureWebTestClient
class AuthorControllerTest {

	@Autowired
	private WebTestClient testClient;
	
	@MockBean
	private WebService service;
	
	@Test
	void test_get_author() {
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
	void test_get_authors() {
		testClient.get()
		.uri(t -> t.path("/api/authors/").queryParam("ids", 1,2).build())
		.exchange()
		.expectStatus().isOk()
		.expectBodyList(AuthorResponse.class)
		.hasSize(2);
	} 

	@Test
	void test_find_author_by_start_symbol() {
		String nameOfAuthor = "sec";
		
		testClient.get()
		.uri("/api/authors/symbol/" + nameOfAuthor)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.jsonPath("$[0].id").isEqualTo(2)
		.jsonPath("$[0].name").isEqualTo("Second");
		
	}
	
	@Test
	void test_create_author_without_cover() {
		
		var builder = new MultipartBodyBuilder();
		
		builder.part("name", "test");
		builder.part("description", "test desc");
		
		testClient.post()
		.uri(t -> t.path("/api/authors/").build())
		.header("userId", "2")
		.contentType(MediaType.MULTIPART_FORM_DATA)
		.bodyValue(BodyInserters.fromMultipartData(builder.build()))
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isCreated();
		
	}
}
