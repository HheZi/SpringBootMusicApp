package com.app.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.app.payload.SaveAudioDTO;

import lombok.SneakyThrows;

@Service
public class AudioService {

	@Value("${chunk.max-size}")
	public Integer CHUNK_OF_AUDIO;

	@Value("${audio.dir}")
	private String audioDirName;
	
	public ResourceRegion getResource(String filename, String rangeHeader) throws IOException {
		File file = new File(audioDirName, filename).getAbsoluteFile();
		if (!file.exists()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		FileSystemResource resource = new FileSystemResource(file);

		List<HttpRange> ranges = HttpRange.parseRanges(rangeHeader);
		long contentLength = resource.contentLength();

		HttpRange httpRange = ranges.get(0);

		long rangeStart = httpRange.getRangeStart(contentLength);
		long rangeEnd = Math.min(contentLength - 1, rangeStart + CHUNK_OF_AUDIO - 1);
		
		return new ResourceRegion(resource, rangeStart, rangeEnd - rangeStart);

	}

//	public ResponseEntity<Flux<DataBuffer>> getResource(String filename, String rangeHeader) throws IOException{
//		FileSystemResource resource = new FileSystemResource(Path.of(audioPath, filename));
//		
//		List<HttpRange> ranges = HttpRange.parseRanges(rangeHeader);
//		long contentLength = resource.contentLength();
//		
//		HttpRange range = ranges.get(0); 
//        long rangeStart = range.getRangeStart(contentLength);
//        long rangeEnd = Math.min(contentLength - 1, rangeStart + CHUNK_OF_AUDIO - 1);
//        long rangeLength = rangeEnd - rangeStart + 1;
//		
//        Flux<DataBuffer> body = DataBufferUtils.read(
//	            resource,
//	            new DefaultDataBufferFactory(),
//	            CHUNK_OF_AUDIO
//	        )
//        	.skip(rangeStart / CHUNK_OF_AUDIO)
//			.take(rangeLength / CHUNK_OF_AUDIO);
//        
//		return ResponseEntity
//		.status(HttpStatus.PARTIAL_CONTENT)
//		.contentType(MediaType.APPLICATION_OCTET_STREAM)
//		.header(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", rangeStart, rangeEnd, contentLength))
//		.body(body);
//	}
	
	@SneakyThrows
	public void saveAudio(SaveAudioDTO dto) {
		if (dto.getFile() == null || dto.getName() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		
		Files.write(new File(audioDirName, dto.getName()).getAbsoluteFile().toPath(), dto.getFile().getBytes());
	}

	public void deleteAudio(String name) {
		File file = new File(audioDirName, name).getAbsoluteFile();
		
		if (file.exists()) {
			file.delete();
		}
		else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
	}

}
