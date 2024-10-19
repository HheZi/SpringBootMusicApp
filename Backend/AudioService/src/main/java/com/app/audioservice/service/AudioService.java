package com.app.audioservice.service;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
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

	@Value("${audio.path}")
	private String audioPath;


	public AudioFragment getResource(String filename, String rangeHeader) throws IOException {
		File file = new File(audioPath, filename);

		try (InputStream input = new FileInputStream(file)) {
			List<HttpRange> ranges = HttpRange.parseRanges(rangeHeader);
			long contentLength = file.length();

			HttpRange httpRange = ranges.get(0);

			long rangeStart = httpRange.getRangeStart(contentLength);
			long rangeEnd = rangeStart + MAX_CHUNK_OF_AUDIO;

			input.skipNBytes(rangeStart);
			return new AudioFragment(input.readNBytes(MAX_CHUNK_OF_AUDIO),
					createRangeHeaderValue(rangeStart, rangeEnd, contentLength));

		}

	}

	@SneakyThrows
	public void saveAudio(SaveAudioDTO dto) {
		Path path = Path.of(audioPath).resolve(dto.getName());
		Files.write(path, dto.getContent());
	}

	private String createRangeHeaderValue(long startRange, long endRange, long contentLength) {
		return String.format("bytes %d-%d/%d", startRange, endRange, contentLength);
	}
}
