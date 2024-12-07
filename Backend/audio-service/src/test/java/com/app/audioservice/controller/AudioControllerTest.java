package com.app.audioservice.controller;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.app.audioservice.payload.SaveAudioDTO;
import com.app.audioservice.service.AudioService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(properties = "audio.path=/Programming/Java/StreamingService/Backend/AudioService/audio/test/")
@AutoConfigureMockMvc
@Disabled
class AudioControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private AudioService audioService;
	
	@Value("${audio.path}")
	private String testAudioPath;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Test
	void test_get_audio_method_with_range_header() throws Exception {
		
		mockMvc.perform(MockMvcRequestBuilders.get("/api/audio/file")
				.header(HttpHeaders.RANGE, "bytes=0-").accept("audio/mpeg"))
				.andExpect(status().isPartialContent())
				.andExpect(header().exists(HttpHeaders.CONTENT_RANGE))
				.andExpect(header().string(HttpHeaders.CONTENT_LENGTH, audioService.CHUNK_OF_AUDIO + ""));
		
	}
	
	@Test
	void test_get_audio_method_without_range_header() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/audio/test"))
				.andExpect(status().isRequestedRangeNotSatisfiable());
	}

	
	@Test
	void test_save_audio_method() throws JsonProcessingException, Exception {
		FileInputStream fileInputStream = new FileInputStream(testAudioPath + "file");
		
		String filenameToSave = "saveFile";
		byte[] contentToSave = fileInputStream.readNBytes(100);
		
		fileInputStream.close();
		
		Path path = Path.of(testAudioPath, filenameToSave);
		
		SaveAudioDTO dto = new SaveAudioDTO();
		dto.setName(filenameToSave);
//		dto.setContent(contentToSave);
		
		
		mockMvc.perform(MockMvcRequestBuilders.post("/api/audio")
				.content(mapper.writeValueAsBytes(dto))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated());
		
		byte[] allBytes = Files.readAllBytes(path);
		
		Files.delete(path);
		
		assertArrayEquals(contentToSave, allBytes);
		
	}
}
