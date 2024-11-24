package com.app.audioservice.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Service;

import com.app.audioservice.payload.SaveAudioDTO;

import lombok.SneakyThrows;

@Service
public class AudioService {

	public final int CHUNK_OF_AUDIO = 1024 * 1024;

	@Value("${audio.path}")
	private String audioPath;

	public ResourceRegion getResource(String filename, String rangeHeader) throws IOException {
		FileSystemResource resource = new FileSystemResource(Path.of(audioPath, filename));

		List<HttpRange> ranges = HttpRange.parseRanges(rangeHeader);
		long contentLength = resource.contentLength();

		HttpRange httpRange = ranges.get(0);

		long rangeStart = httpRange.getRangeStart(contentLength);
		long rangeEnd = Math.min(contentLength - 1, rangeStart + CHUNK_OF_AUDIO - 1);
		
		return new ResourceRegion(resource, rangeStart, rangeEnd - rangeStart);

	}

	@SneakyThrows
	public void saveAudio(SaveAudioDTO dto) {
		Files.write(Path.of(audioPath, dto.getName()), dto.getFile().getBytes());
	}

	@SneakyThrows
	public void deleteAudio(String name) {
		Files.delete(Path.of(audioPath, name));
	}

}
