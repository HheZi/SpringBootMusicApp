package com.app.audioservice.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Service;

import com.app.audioservice.model.SaveAudioDTO;
import com.app.audioservice.utils.AudioFragment;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


@Service
public class AudioService {

	public final int MAX_CHUNK_OF_AUDIO = 1024 * 1024;
	
	private final String AUDIO_FILE;
	
	private final String AUDIO_PATH;
	
	@Value("${audio.type}")
	private String audioType;
	
	@Autowired
	private ResourceLoader resourceLoader;

	public AudioService(@Value("${audio.path}") String path, @Value("${audio.type}") String type) {
		this.AUDIO_PATH = !path.endsWith("/") ? path + "/" : path;
		this.AUDIO_FILE = AUDIO_PATH + "%s." + type;
	}
	
	public AudioFragment getResource(String filename, String rangeHeader) throws IOException {
		Resource resource = resourceLoader.getResource(AUDIO_FILE.formatted(filename));

		List<HttpRange> ranges = HttpRange.parseRanges(rangeHeader);
		long contentLength = resource.contentLength();

		HttpRange httpRange = ranges.get(0);

		long rangeStart =  httpRange.getRangeStart(contentLength);
		long rangeEnd =  rangeStart + MAX_CHUNK_OF_AUDIO;
		
		try (InputStream input = resource.getInputStream()) {
			input.skipNBytes(rangeStart);
			return new AudioFragment(input.readNBytes(MAX_CHUNK_OF_AUDIO), createRangeHeaderValue(rangeStart, rangeEnd, contentLength));
		}

	}

	@SneakyThrows
	public void saveAudio(SaveAudioDTO dto) {
		Path path = Paths.get(resourceLoader.getResource(AUDIO_PATH + dto.getName() + audioType).getURI());
		Files.write(path, dto.getContent(), StandardOpenOption.CREATE_NEW);
	}
	
	private String createRangeHeaderValue(long startRange, long endRange, long contentLength) {
		return String.format("bytes %d-%d/%d", startRange, endRange, contentLength);
	}
}
