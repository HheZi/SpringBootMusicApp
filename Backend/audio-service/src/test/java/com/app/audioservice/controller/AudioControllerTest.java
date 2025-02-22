package com.app.audioservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.SneakyThrows;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnableAutoConfiguration(exclude = KafkaAutoConfiguration.class)
@AutoConfigureWebTestClient
@DirtiesContext
@TestInstance(Lifecycle.PER_CLASS)
class AudioControllerTest {

	@Autowired
	private WebTestClient testClient;
	
	@Value("${chunk.max-size}")
	public Integer CHUNK_OF_AUDIO;
	
	@Value("${audio.dir}")
	private String testAudioPath;
	
	@BeforeAll
	@SneakyThrows
	private void configeFile() {
		File file = ResourceUtils.getFile("classpath:file");
		
		Files.copy(file.toPath(), Paths.get(testAudioPath, "file"), StandardCopyOption.REPLACE_EXISTING);
	}
	
	@Test
	void test_get_audio_method_with_range_header() {
		testClient
		.get()
		.uri("/api/audio/file")
		.header(HttpHeaders.RANGE, "bytes=0-")
		.exchange()
		.expectHeader()
		.exists(HttpHeaders.CONTENT_RANGE)
		.expectStatus()
		.isEqualTo(HttpStatus.PARTIAL_CONTENT);
		
	}
	
	@Test
	void test_get_audio_method_without_range_header() {
		testClient
		.get()
		.uri("/api/audio/file")
		.exchange()
		.expectStatus()
		.isEqualTo(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
	}

	
	@Test
	void test_save_audio_method() throws JsonProcessingException, Exception {
		MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
		
		String filename = "savedFile";
		
		bodyBuilder.part("name", filename);
		bodyBuilder.part("file", new FileSystemResource(Paths.get(testAudioPath,"file")));
		
		testClient
		.post()
		.uri("/api/audio")
		.contentType(MediaType.MULTIPART_FORM_DATA)
		.body(BodyInserters.fromMultipartData(bodyBuilder.build()))
		.exchange()
		.expectStatus()
		.isOk();	
		
		assertThat(Paths.get(testAudioPath, filename)).exists();
		Files.deleteIfExists(ResourceUtils.getFile("file:"+testAudioPath+filename).toPath());
	}
	
	@AfterAll
	@SneakyThrows
	private void deleteFileAfterAll() {
		Files.deleteIfExists(ResourceUtils.getFile("file:"+testAudioPath+"/file").toPath());
	}
}
