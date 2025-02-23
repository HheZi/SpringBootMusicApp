package com.app.audioservice.controller;

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
import org.springframework.core.io.ClassPathResource;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

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

	private final static String NAME_OF_TEST_FILE = "file";

	@BeforeAll
	@SneakyThrows
	private void configeFile() {
		File file = ResourceUtils.getFile("classpath:file");
		
		Files.copy(file.toPath(), Paths.get(testAudioPath, NAME_OF_TEST_FILE), StandardCopyOption.REPLACE_EXISTING);
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
	void test_save_audio_method() throws Exception {
		MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
		
		String filename = "savedFile";
		
		bodyBuilder.part("name", filename);
		bodyBuilder.part("file", new ClassPathResource("validFile.mp3"));
		
		testClient
		.post()
		.uri("/api/audio")
		.contentType(MediaType.MULTIPART_FORM_DATA)
		.body(BodyInserters.fromMultipartData(bodyBuilder.build()))
		.exchange()
		.expectStatus()
		.isOk();

		Path path = Paths.get(testAudioPath, filename);

		assertThat(path).exists();
		Files.deleteIfExists(path);
	}

	@Test
	void test_save_audio_method_with_wrong_file_type() throws Exception {
		MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

		String filename = "savedFile";

		bodyBuilder.part("name", filename);
		bodyBuilder.part("file", new ClassPathResource("file"));

		testClient
				.post()
				.uri("/api/audio")
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.body(BodyInserters.fromMultipartData(bodyBuilder.build()))
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.reason").value(is("Wrong format of file. Can be only MP3"));

		Path path = Paths.get(testAudioPath, filename);

		Files.deleteIfExists(path);
	}

	@Test
	void test_save_audio_method_when_file_not_specified() throws Exception {
		MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

		String filename = "savedFile";

		bodyBuilder.part("name", filename);

		testClient
				.post()
				.uri("/api/audio")
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.body(BodyInserters.fromMultipartData(bodyBuilder.build()))
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.reason").value(is("Audio file is not specified."));

		Path path = Paths.get(testAudioPath, filename);

		Files.deleteIfExists(path);
	}
	
	@AfterAll
	@SneakyThrows
	private void deleteFileAfterAll() {
		Path path = Paths.get(testAudioPath, NAME_OF_TEST_FILE);
		Files.deleteIfExists(path);
	}
}
