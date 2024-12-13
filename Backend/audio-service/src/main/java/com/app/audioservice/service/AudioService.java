package com.app.audioservice.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.app.audioservice.payload.SaveAudioDTO;

import lombok.SneakyThrows;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AudioService {

	@Value("${chunk.max-size}")
	public Integer CHUNK_OF_AUDIO;

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
	public Mono<ResponseEntity<?>> saveAudio(SaveAudioDTO dto) {
		return dto.getFile().transferTo(Path.of(audioPath, dto.getName()))
		.map(t -> ResponseEntity.status(HttpStatus.CREATED).build());
	}

	@SneakyThrows
	public Mono<Void> deleteAudio(String name) {
		return Mono.just(new File(audioPath, name))
				.filter(t -> t.exists())
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.doOnNext(t -> t.delete())
				.then();
	}

}
