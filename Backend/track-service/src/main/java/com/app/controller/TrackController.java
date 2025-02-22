package com.app.controller;

import com.app.payload.request.CreateTrackDto;
import com.app.payload.request.UpdateTrackRequest;
import com.app.payload.response.ResponseTotalDuration;
import com.app.payload.response.ResponseTrack;
import com.app.service.AudioValidatorService;
import com.app.service.TrackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.List;


@RestController
@RequestMapping("/api/tracks/")
@RequiredArgsConstructor
public class TrackController {

	private final TrackService trackService;
	
	private final AudioValidatorService audioValidatorService;

	@GetMapping
	public Mono<Page<ResponseTrack>> getTracks(
			@RequestParam(value = "name", required = false) String trackName, 
			@RequestParam(value = "albumId", required = false) List<Integer> albumId,
			@RequestParam(value = "id", required = false) List<Long> ids,
			@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
			@RequestParam(value = "size", required = false, defaultValue = "5") Integer size
		){
		return trackService.getTracks(
				trackName != null ? URLDecoder.decode(trackName, Charset.defaultCharset()) : trackName, 
						albumId,
						ids,
						page,
						size
					);
	}

	@GetMapping("/count/{albumId}")
	public Mono<Integer> countTracks(@PathVariable("albumId") Long albumId){
		return trackService.countTracksByAlbumId(albumId);
	}
	
	@GetMapping("/duration")
	public Mono<ResponseTotalDuration> getTotalDuration(
			@RequestParam(value = "ids", required = false) List<Long> ids,
			@RequestParam(value = "albumId", required = false) Integer albumId
		){
		return  trackService.totalTimeOfTracks(ids, albumId);
	}
	
	@PostMapping
	public Mono<ResponseEntity<?>> createTrack(
			@Valid @ModelAttribute CreateTrackDto dto, 
			@RequestHeader("userId") Integer userId
		) {
		return audioValidatorService.validateAudioFile(dto.getAudio())
				.flatMap(t -> trackService.createTrack(dto, userId));
	}
	
	@PatchMapping("/{id}")
	public Mono<Void> updateTitle(
			@Valid @RequestBody UpdateTrackRequest title, 
			@RequestHeader("userId") Integer userId, 
			@PathVariable("id") Long trackId
		){
		return trackService.updateTrackTitle(title, trackId, userId);
	}
	
	@DeleteMapping("/{id}")
	public Mono<Void> deleteTrack(
			@PathVariable("id") Long id,
			@RequestHeader("userId") Integer userId
		){
		return trackService.deleteTrack(id, userId);
	}
	
	@DeleteMapping
	public Mono<Void> deleteTrackByAlbum(@RequestParam("albumId") Integer id){
		return trackService.deleteTracksByAlbumId(id);
	} 
}
