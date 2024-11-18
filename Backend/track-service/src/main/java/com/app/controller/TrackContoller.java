package com.app.controller;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.payload.request.CreateTrackDto;
import com.app.payload.request.UpdateTrackRequest;
import com.app.payload.response.ResponseTrack;
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
		return trackService.getTracks(
				trackName != null ? URLDecoder.decode(trackName, Charset.defaultCharset()) : trackName, 
						authorId, 
						playlistId
					);
	}

	@GetMapping("/count/{playlistId}")
	public Mono<Long> countTracks(@PathVariable("playlistId") Long playlistId){
		return trackService.countTracksByPlaylistId(playlistId);
	}
	
	@PostMapping
	public Mono<ResponseEntity<?>> createTrack(
			@ModelAttribute Mono<CreateTrackDto> dto, 
			@RequestHeader("userId") Integer userId
		) {
		return dto.flatMap(t -> trackService.createTrack(t, userId));
	}
	
	@PatchMapping("/{id}")
	public Mono<Void> updateTitle(
			@RequestBody UpdateTrackRequest title, 
			@RequestHeader("userId") Integer userId, 
			@PathVariable("id") Long trackId
		){
		return trackService.updateTrackTitle(title, trackId, userId);
	}
	
	@DeleteMapping("/{id}")
	public Mono<Void> deleteTrack(@PathVariable("id") Long id){
		return trackService.deleteTrack(id);
	}
	
	@DeleteMapping
	public Mono<Void> deleteTrack(@RequestParam("playlistId") Integer id){
		return trackService.deleteTracksByPlaylistId(id);
	} 
}
