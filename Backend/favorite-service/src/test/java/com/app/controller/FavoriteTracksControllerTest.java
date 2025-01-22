package com.app.controller;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@DirtiesContext
@TestMethodOrder(value = OrderAnnotation.class)
@AutoConfigureWebTestClient
class FavoriteTracksControllerTest {

	@Autowired
	private WebTestClient testClient;

	@Test
	@Order(Integer.MAX_VALUE)
	public void test_get_tracks_in_favorites() {
		testClient
		.get()
		.uri("/api/favorites/tracks/")
		.header("userId", "1")
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectBodyList(Long.class)
		.contains(1L, 3L)
		.hasSize(2);
	}
	
	@Test
	public void test_add_track_to_favorites() {
		testClient
		.post()
		.uri("/api/favorites/tracks/3")
		.header("userId", "1")
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isNoContent();
	}
	
	@Test
	public void test_delete_track_from_favorites() {
		testClient
		.delete()
		.uri("/api/favorites/tracks/2")
		.header("userId", "1")
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isNoContent();
	}
	
	

}
