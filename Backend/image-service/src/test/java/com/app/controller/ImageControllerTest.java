package com.app.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.BodyInserters;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.SneakyThrows;

@SpringBootTest
@EnableAutoConfiguration(exclude = KafkaAutoConfiguration.class)
@AutoConfigureWebTestClient
@DirtiesContext
@TestInstance(Lifecycle.PER_CLASS)
class ImageControllerTest {

	@Autowired
	private WebTestClient testClient;
	
	@Value("${image.dir}")
	private String imageDir;
	
	@Value("${image.default}")
	private String defaultImageName;
	
	@BeforeAll
	@SneakyThrows
	private void configeFile() {
		File file = ResourceUtils.getFile("classpath:testImage");
		
		Files.copy(file.toPath(), Paths.get(imageDir, "testImage"), StandardCopyOption.REPLACE_EXISTING);
	}
	
	@Test
	public void get_image() {
		testClient
		.get()
		.uri("/api/images/testImage")
		.exchange()
		.expectHeader()
		.contentType(MediaType.IMAGE_JPEG)
		.expectStatus().isOk()
		.expectBody();
	}
	
	@Test
	public void get_default_image() {
		testClient
		.get()
		.uri("/api/images/default")
		.exchange()
		.expectHeader()
		.contentType(MediaType.IMAGE_PNG)
		.expectStatus().isOk()
		.expectBody();
	}
	
	@Test
	void test_save_audio_method() throws JsonProcessingException, Exception {
		MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
		
		String filename = "savedFile";
		
		bodyBuilder.part("name", filename);
		bodyBuilder.part("file", new FileSystemResource(Paths.get(imageDir,"testImage")));
		
		testClient
		.post()
		.uri("/api/images/")
		.contentType(MediaType.MULTIPART_FORM_DATA)
		.body(BodyInserters.fromMultipartData(bodyBuilder.build()))
		.exchange()
		.expectStatus()
		.isOk();	
		
		Path path = Paths.get(imageDir, filename);
		
		assertThat(path).exists();
		Files.deleteIfExists(path);
	}
	
	@AfterAll
	@SneakyThrows
	private void deleteFileAfterAll() {
		Files.deleteIfExists(Paths.get(imageDir, "testImage"));
	}
	
}
