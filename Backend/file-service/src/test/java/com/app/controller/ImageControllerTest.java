package com.app.controller;

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
import org.springframework.core.io.ClassPathResource;
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
class ImageControllerTest {

	@Autowired
	private WebTestClient testClient;
	
	@Value("${image.dir}")
	private String imageDir;
	
	@Value("${image.default}")
	private String defaultImageName;

	private final String NAME_OF_TEST_INVALID_FILE = "file";

	private final String NAME_OF_TEST_VALID_FILE = "validFile.jpeg";

	@BeforeAll
	@SneakyThrows
	private void configeFile() {
		File file = ResourceUtils.getFile("classpath:"+NAME_OF_TEST_VALID_FILE);
		
		Files.copy(file.toPath(), Paths.get(imageDir, NAME_OF_TEST_VALID_FILE), StandardCopyOption.REPLACE_EXISTING);
	}
	
	@Test
	public void get_image() {
		testClient
		.get()
		.uri("/api/files/images/"+NAME_OF_TEST_VALID_FILE)
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
		.uri("/api/files/images/default")
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
		bodyBuilder.part("file", new ClassPathResource(NAME_OF_TEST_VALID_FILE));
		
		testClient
		.post()
		.uri("/api/files/images/")
		.contentType(MediaType.MULTIPART_FORM_DATA)
		.body(BodyInserters.fromMultipartData(bodyBuilder.build()))
		.exchange()
		.expectStatus()
		.isOk();	
		
		Path path = Paths.get(imageDir, filename);
		
		assertThat(path).exists();
		Files.deleteIfExists(path);
	}

	@Test
	void test_save_audio_method_with_wrong_file_type() throws Exception {
		MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

		String filename = "savedFile";

		bodyBuilder.part("name", filename);
		bodyBuilder.part("file", new ClassPathResource(NAME_OF_TEST_INVALID_FILE));

		testClient
				.post()
				.uri("/api/files/images/")
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.body(BodyInserters.fromMultipartData(bodyBuilder.build()))
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.reason").value(is("Wrong format of file. Can be only JPEG and PNG"));

		Path path = Paths.get(imageDir, filename);
		Files.deleteIfExists(path);
	}

	@Test
	void test_save_audio_method_without_file() throws Exception {
		MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

		String filename = "savedFile";

		bodyBuilder.part("name", filename);

		testClient
				.post()
				.uri("/api/files/images/")
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.body(BodyInserters.fromMultipartData(bodyBuilder.build()))
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.reason").value(is("Image file is not specified"));

		Path path = Paths.get(imageDir, filename);
		Files.deleteIfExists(path);
	}
	
	@AfterAll
	@SneakyThrows
	private void deleteFileAfterAll() {
		Files.deleteIfExists(Paths.get(imageDir, NAME_OF_TEST_VALID_FILE));
	}
	
}
