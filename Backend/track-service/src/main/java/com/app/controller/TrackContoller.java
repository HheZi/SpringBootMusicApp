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
import com.app.payload.response.ResponseTotalDuration;
import com.app.payload.response.ResponseTrack;
import com.app.service.TrackService;

import jakarta.validation.Valid;
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
			@RequestParam(value = "albumId", required = false) List<Integer> albumId,
			@RequestParam(value = "id", required = false) List<Integer> ids
		){
		return trackService.getTracks(
				trackName != null ? URLDecoder.decode(trackName, Charset.defaultCharset()) : trackName, 
						albumId,
						ids
					);
	}

	@GetMapping("/count/{albumId}")
	public Mono<Integer> countTracks(@PathVariable("albumId") Long albumId){
		return trackService.countTracksByAlbumId(albumId);
	}
	
	@GetMapping("/duration")
	public Mono<ResponseTotalDuration> getTotalDuration(@RequestParam("ids") List<Long> ids){
		return  trackService.totalTimeOfTracks(ids);
	}
	
	@PostMapping
	public Mono<ResponseEntity<?>> createTrack(
			@Valid @ModelAttribute CreateTrackDto dto, 
			@RequestHeader("userId") Integer userId
		) {
		return trackService.createTrack(dto, userId);
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
	public Mono<Void> deleteTrack(@RequestParam("albumId") Integer id){
		return trackService.deleteTracksByAlbumId(id);
	} 
}
