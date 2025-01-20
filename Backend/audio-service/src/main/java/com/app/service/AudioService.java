package com.app.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.app.payload.SaveAudioDTO;

import lombok.SneakyThrows;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AudioService {

	@Value("${chunk.max-size}")
	public Integer CHUNK_OF_AUDIO;

	@Value("${audio.dir}")
	private String audioDirName;
	
	public Mono<ResponseEntity<Flux<DataBuffer>>> getResource(String filename, String rangeHeader) {
	    Path filePath = Paths.get(audioDirName, filename);
	    
	    return Mono.fromCallable(() -> Files.exists(filePath) ? filePath : null)
	        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
	        .flatMap(t -> buildResponse(HttpRange.parseRanges(rangeHeader).get(0), filePath));
	}
	
	@SneakyThrows
	private Mono<ResponseEntity<Flux<DataBuffer>>> buildResponse(HttpRange range, Path filePath) {
		long contentLength = Files.size(filePath);
        long rangeStart = range.getRangeStart(contentLength);
        long rangeEnd = Math.min(range.getRangeEnd(contentLength), rangeStart + CHUNK_OF_AUDIO - 1);
        
        return Mono.just(ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", rangeStart, rangeEnd, contentLength))
                .body(getResourceForResponse(rangeStart, filePath)));
	}
	
	private Flux<DataBuffer> getResourceForResponse(Long rangeStart, Path path){
		return DataBufferUtils.read(
                new FileSystemResource(path),
                rangeStart,
                new DefaultDataBufferFactory(),
                CHUNK_OF_AUDIO
            )
            .take(1);
	}
	
	public Mono<Void> saveAudio(SaveAudioDTO dto) {
		return dto.getFile()
		.transferTo(Paths.get(audioDirName, dto.getName()));
	}

	public Mono<Void> deleteAudio(String name) {
		return Mono.just(Paths.get(audioDirName, name))
		.doOnNext(this::deleteAudio)
		.then();
	}

	@SneakyThrows
	public void deleteAudio(Path path) {
		Files.deleteIfExists(path);
	}
	
}
