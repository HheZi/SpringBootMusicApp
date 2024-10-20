package com.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.model.projection.CreateTrackDto;
import com.app.model.projection.ResponseTrack;
import com.app.service.TrackService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/tracks/")
@RequiredArgsConstructor
public class TrackContoller {

	private final TrackService trackService;

	@GetMapping
	public Flux<ResponseTrack> getTracks() {
		return trackService.getTracks();
	}
	
	
	@PostMapping
	public Mono<ResponseEntity<?>> createTrack(@ModelAttribute Mono<CreateTrackDto> dto,
			@RequestPart("file") Mono<FilePart> file) {

		return Mono.zip(dto, file).doOnNext(trackService::createTrack)
				.flatMap(t -> Mono.just(ResponseEntity.status(HttpStatus.CREATED).build()));
	}

}
