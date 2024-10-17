package com.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.model.projection.CreateTrackDto;
import com.app.service.TrackService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/tracks/")
@RequiredArgsConstructor
public class TrackContoller {

	private final TrackService trackService;
	
	@PostMapping
	public Mono<ResponseEntity<?>> createTrack(@ModelAttribute Mono<CreateTrackDto> dto, @RequestHeader("id") Integer id){
		
		return dto
				.flatMap(t -> Mono.just(ResponseEntity.status(HttpStatus.CREATED).build()));
	}
	
}
