package com.app.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
	

	public ResponseEntity<Flux<DataBuffer>> getResource(String filename, String rangeHeader) throws IOException{
		FileSystemResource resource = new FileSystemResource(new File(audioDirName, filename).getAbsoluteFile());
		
		List<HttpRange> ranges = HttpRange.parseRanges(rangeHeader);
		long contentLength = resource.contentLength();
		
		HttpRange range = ranges.get(0); 
        long rangeStart = range.getRangeStart(contentLength);
        long rangeEnd = Math.min(range.getRangeEnd(contentLength), rangeStart + CHUNK_OF_AUDIO - 1);
        
        Flux<DataBuffer> body = DataBufferUtils.read(
	            resource,
	            rangeStart,
	            new DefaultDataBufferFactory(),
	            CHUNK_OF_AUDIO
	        )
			.take(1);
        
		return ResponseEntity
		.status(HttpStatus.PARTIAL_CONTENT)
		.contentType(MediaType.APPLICATION_OCTET_STREAM)
		.header(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", rangeStart, rangeEnd, contentLength))
		.body(body);
	}
	
	@SneakyThrows
	public Mono<Void> saveAudio(SaveAudioDTO dto) {
		return dto.getFile()
		.transferTo(new File(audioDirName, dto.getName()).getAbsoluteFile());
	}

	public Mono<Void> deleteAudio(String name) {
		return Mono.just(new File(audioDirName, name).getAbsoluteFile())
		.filter(t -> t.exists())
		.doOnNext(File::delete)
		.then();
	}

}
