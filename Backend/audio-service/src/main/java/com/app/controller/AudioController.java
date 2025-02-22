package com.app.controller;

import com.app.payload.SaveAudioDTO;
import com.app.service.AudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;


@RestController
@RequestMapping("/api/audio")
public class AudioController {

	@Autowired
	private AudioService audioService;
	
	@GetMapping("/{filename}")
	public Mono<ResponseEntity<Flux<DataBuffer>>> getAudio(
			@PathVariable("filename") String filename, 
			@RequestHeader(value = "Range", required = false) String rangeHeader
		) throws IOException{
		
		if (rangeHeader == null) {
			return Mono.just(ResponseEntity
					.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
					.build());
		}
		
		return audioService.getResource(filename, rangeHeader);
	}
	
	@PostMapping
	public Mono<Void> saveAudio(@ModelAttribute SaveAudioDTO dto) {
		return audioService.saveAudio(dto);
	}
}
