package com.app.controller;

import com.app.service.FavoriteTrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@RestController
@RequestMapping("/api/favorites/tracks/")
@RequiredArgsConstructor
public class FavoriteTracksController {

	private final FavoriteTrackService service;
	
	@GetMapping
	public Flux<Long> getTracksInFavorites(
			@RequestParam(value = "trackId", required = false) List<Long> trackIds,
			@RequestHeader("userId") Integer userId
		) {
		if (trackIds != null) {
			return service.getTrackInFavorites(trackIds, userId);
		}
		return service.getUserFavoritesTracks(userId);
	}
	
	@PostMapping("{trackId}")
	public Mono<ResponseEntity<?>> addTrackToFavorites(
			@PathVariable("trackId") Long trackId,
			@RequestHeader("userId") Integer userId
		) {
		return service.addTrackToFavorites(trackId, userId)
				.then(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build()));
	}
	
	@DeleteMapping("{trackId}")
	public Mono<ResponseEntity<?>> deleteTrackFromFavorites(
			@PathVariable("trackId") Long trackId,
			@RequestHeader("userId") Integer userId
		) {
		return service.deleteTrackFromFavorites(trackId, userId)
				.then(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build()));
	}
}
