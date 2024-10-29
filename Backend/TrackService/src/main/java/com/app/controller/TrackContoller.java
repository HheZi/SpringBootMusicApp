package com.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.model.projection.CreateTrackDto;
import com.app.model.projection.ResponseTrack;
import com.app.service.TrackService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/tracks/")
@RequiredArgsConstructor
public class TrackContoller {

	private final TrackService trackService;

	@GetMapping
	public Flux<ResponseTrack> getTracks(
			@RequestParam(value = "name", required = false) String trackName, 
			@RequestParam(value = "authorId", required = false) List<Integer> authorId,
			@RequestParam(value = "playlistId", required = false) List<Integer> playlistId
		){
		return trackService.getTracks(trackName, authorId, playlistId);
	}
	
	@PostMapping
	public Mono<ResponseEntity<?>> createTrack(
			@ModelAttribute Mono<CreateTrackDto> dto, 
			@RequestHeader("userId") Integer userId
		) {
		return dto.flatMap(t -> trackService.createTrack(t, userId));
	}

}
