package com.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.payload.response.FavoriteTrackResponse;
import com.app.service.FavoriteTrackService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/favorites/tracks/")
@RequiredArgsConstructor
public class FavoriteTracksController {

	private final FavoriteTrackService service;
	
	@GetMapping
	public Flux<FavoriteTrackResponse> getTracksInFavorites(
			@RequestParam("trackIds") List<Long> trackIds,
			@RequestHeader("userId") Integer userId
		) {
		return service.getTrackInFavorites(trackIds, userId);
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
