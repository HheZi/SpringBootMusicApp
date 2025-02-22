package com.app.controller;


import com.app.kafka.KafkaAlbumProducer;
import com.app.kafka.KafkaImageProducer;
import com.app.payload.response.AlbumPreviewResponse;
import com.app.payload.response.ResponseAlbum;
import com.app.service.WebService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.io.File;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@EnableAutoConfiguration(exclude = KafkaAutoConfiguration.class)
@SpringBootTest
@AutoConfigureWebTestClient
@TestInstance(Lifecycle.PER_CLASS)
class AlbumControllerTest {

	@Autowired
	private WebTestClient testClient;
	
	@MockBean
	private WebService service;

	@MockBean
	private KafkaAlbumProducer kafkaAlbumProducer;

	@MockBean
	private KafkaImageProducer kafkaImageProducer;
	
	@Test
	public void get_albums_by_ids() {
		testClient
		.get()
		.uri(t -> t.path("/api/albums/").queryParam("ids", 1 , 2).build())
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().is2xxSuccessful()
		.expectBodyList(AlbumPreviewResponse.class)
		.hasSize(2);
	}
	
	@Test
	public void get_albums_by_album_ids() {
		testClient
		.get()
		.uri(t -> t.path("/api/albums/").queryParam("authorId", 201 , 202).build())
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().is2xxSuccessful()
		.expectBodyList(AlbumPreviewResponse.class)
		.hasSize(2);
	}
	
	@Test
	public void get_albums_where_albumId_and_ids_not_included() {
		testClient
		.get()
		.uri(t -> t.path("/api/albums/").build())
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isBadRequest();
	}
	
	@Test
	public void get_album() {
		ResponseAlbum expected = ResponseAlbum.builder()
		.name("Echoes of Eternity")
		.authorId(202)
		.id(2)
		.imageUrl("http://localhost:8080/api/images/default")
		.releaseDate(LocalDate.parse("2024-01-20"))
		.build();
		
		testClient
		.get()
		.uri(t -> t.path("/api/albums/2").build())
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().is2xxSuccessful()
		.expectBody(ResponseAlbum.class)
		.isEqualTo(expected);
	}
	
	@Test
	public void get_album_and_expected_not_found() {
		testClient
		.get()
		.uri(t -> t.path("/api/albums/100").build())
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isNotFound();
	}
	
	@Test
	public void get_album_by_full_name() {
		testClient
		.get()
		.uri(t -> t.path("/api/albums/symbol/"+"Echoes of Eternity").build())
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().is2xxSuccessful()
		.expectBodyList(AlbumPreviewResponse.class);
	}
	
	@Test
	public void get_album_by_starting_name() {
		testClient
		.get()
		.uri(t -> t.path("/api/albums/symbol/"+"Echoes of").build())
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().is2xxSuccessful()
		.expectBodyList(AlbumPreviewResponse.class);
	}
	
	@Test
	public void get_album_by_starting_name_ignore_case() {
		testClient
		.get()
		.uri(t -> t.path("/api/albums/symbol/"+"Echoes of".toLowerCase()).build())
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().is2xxSuccessful()
		.expectBodyList(AlbumPreviewResponse.class);
	}
	
	@Test
	public void get_album_by_starting_name_and_expected_not_found() {
		testClient
		.get()
		.uri(t -> t.path("/api/albums/symbol/"+"123324234").build())
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isNotFound();
	}
	
	@Test
	public void get_album_by_owner() {
		testClient
		.get()
		.uri(t -> t.path("/api/albums/owner/"+"1").build())
		.header("userId", "1")
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().is2xxSuccessful()
		.expectBody(Boolean.class)
		.isEqualTo(Boolean.TRUE);
	}
	
	@Test
	public void get_album_by_owner_but_expected_not_an_owner() {
		testClient
		.get()
		.uri(t -> t.path("/api/albums/owner/"+"2").build())
		.header("userId", "1")
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().is2xxSuccessful()
		.expectBody(Boolean.class)
		.isEqualTo(Boolean.FALSE);
	}
	
	@Test
	public void get_album_by_owner_but_it_not_found() {
		testClient
		.get()
		.uri(t -> t.path("/api/albums/owner/"+"100").build())
		.header("userId", "1")
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isNotFound();
	}
	
	@Test
	public void create_album_without_cover() {
		var bodyBuilder = new MultipartBodyBuilder();
		
		bodyBuilder.part("name", "test");
		bodyBuilder.part("releaseDate", "1990-07-07");
		bodyBuilder.part("authorId", "2");
		
		testClient
		.post()
		.uri(t -> t.path("/api/albums/").build())
		.header("userId", "1")
		.contentType(MediaType.MULTIPART_FORM_DATA)
		.body(BodyInserters.fromMultipartData(bodyBuilder.build()))
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isCreated();
	}
	
	@Test
	@SneakyThrows
	public void create_album() {
		doReturn(Mono.just(ResponseEntity.ok().build()))
		.when(service).saveAlbumCover(any(UUID.class), any(File.class));
		
		var bodyBuilder = new MultipartBodyBuilder();
		
		bodyBuilder.part("name", "newAlbum");
		bodyBuilder.part("releaseDate", "1992-07-07");
		bodyBuilder.part("authorId", "4");
		bodyBuilder.part("cover", new ClassPathResource("testImage.jpeg"));
		
		testClient
		.post()
		.uri(t -> t.path("/api/albums/").build())
		.contentType(MediaType.MULTIPART_FORM_DATA)
		.header("userId", "1")
		.body(BodyInserters.fromMultipartData(bodyBuilder.build()))
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isCreated();
		
	}
	
	@Test
	public void create_album_with_wrong_payload() {
		var bodyBuilder = new MultipartBodyBuilder();
		
		bodyBuilder.part("name", " ");
		bodyBuilder.part("releaseDate", LocalDate.now().plus(1, ChronoUnit.YEARS).toString());
		
		testClient
		.post()
		.uri(t -> t.path("/api/albums/").build())
		.header("userId", "1")
		.contentType(MediaType.MULTIPART_FORM_DATA)
		.body(BodyInserters.fromMultipartData(bodyBuilder.build()))
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isBadRequest();
	}
	
	@Test
	@SneakyThrows
	public void create_album_with_wrong_file() {
		var bodyBuilder = new MultipartBodyBuilder();
		
		bodyBuilder.part("name", "test");
		bodyBuilder.part("releaseDate", "1990-07-07");
		bodyBuilder.part("authorId", "2");
		bodyBuilder.part("cover", new ClassPathResource("testImage"));
		
		testClient
		.post()
		.uri(t -> t.path("/api/albums/").build())
		.header("userId", "1")
		.contentType(MediaType.MULTIPART_FORM_DATA)
		.body(BodyInserters.fromMultipartData(bodyBuilder.build()))
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isBadRequest()
		.expectBody();
		
		
	}
	
	@Test
	public void update_album_without_cover() {
		var bodyBuilder = new MultipartBodyBuilder();
		
		bodyBuilder.part("name", "test");
		bodyBuilder.part("releaseDate", "1990-07-07");
		
		testClient
		.put()
		.uri(t -> t.path("/api/albums/"+10).build())
		.header("userId", "10")
		.contentType(MediaType.MULTIPART_FORM_DATA)
		.body(BodyInserters.fromMultipartData(bodyBuilder.build()))
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk();
		
		ResponseAlbum expected = ResponseAlbum.builder()
				.name("test")
				.authorId(210)
				.id(10)
				.imageUrl("http://localhost:8080/api/images/default")
				.releaseDate(LocalDate.parse("1990-07-07"))
				.build();
				
		testClient
		.get()
		.uri(t -> t.path("/api/albums/10").build())
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().is2xxSuccessful()
		.expectBody(ResponseAlbum.class)
		.isEqualTo(expected);
	}
	
	@Test
	@SneakyThrows
	public void update_album() {
		doReturn(Mono.just(ResponseEntity.ok().build()))
		.when(service).saveAlbumCover(any(UUID.class), any(File.class));
		
		var bodyBuilder = new MultipartBodyBuilder();
		
		bodyBuilder.part("name", "test");
		bodyBuilder.part("releaseDate", "1990-07-07");
		bodyBuilder.part("cover", new ClassPathResource("testImage.jpeg"));
		
		testClient
		.put()
		.uri(t -> t.path("/api/albums/"+11).build())
		.header("userId", "11")
		.contentType(MediaType.MULTIPART_FORM_DATA)
		.body(BodyInserters.fromMultipartData(bodyBuilder.build()))
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk();
	}
	
	@Test
	public void delete_cover_of_album() {
		testClient
		.delete()
		.uri(t -> t.path("/api/albums/cover/"+3).build())
		.header("userId", "3")
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk();
	}
	
	@Test
	public void delete_album() {
		testClient
		.delete()
		.uri(t -> t.path("/api/albums/"+9).build())
		.header("userId", "9")
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk();
	}
}
