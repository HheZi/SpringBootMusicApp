package com.app.controller;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.app.payload.RequestImage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "image.path=/Programming/Java/StreamingService/Backend/image-service/images/test/")
@AutoConfigureMockMvc
class ImageControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Value("${image.path}")
	private String imagePath;

	@Value("${image.default}")
	private String imageDefaultImagePath;

	@Autowired
	private ObjectMapper mapper;
	
	@Test
	void test_get_test_image() throws Exception {

		FileInputStream inputStream = new FileInputStream(imagePath + "testImage");

		byte[] expected = inputStream.readAllBytes();

		inputStream.close();

		byte[] array = mockMvc.perform(MockMvcRequestBuilders.get("/api/images/testImage").accept(MediaType.IMAGE_JPEG))
				.andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();

		assertArrayEquals(expected, array);
	}

	@Test
	void test_get_default_image() throws Exception {
		FileInputStream inputStream = new FileInputStream(imageDefaultImagePath);

		byte[] expected = inputStream.readAllBytes();

		inputStream.close();

		byte[] array = mockMvc.perform(MockMvcRequestBuilders.get("/api/images/default").accept(MediaType.IMAGE_JPEG))
				.andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
		
		assertArrayEquals(expected, array);
	}
	
	@Test
	void test_post_save_image() throws JsonProcessingException, Exception {
		String imageName = "savedImage";
		
		RequestImage requestImage = new RequestImage();
//		requestImage.setContent(new byte[] {100, 45, 23, 65, 4});
		requestImage.setName(imageName);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/api/images/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(requestImage)))
			.andExpect(status().isCreated());
		
		Path path = Path.of(imagePath, imageName);
		
		assertTrue(Files.exists(path));
		
		Files.delete(path);
	}
}
