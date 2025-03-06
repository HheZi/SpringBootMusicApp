package com.app.controller;

import com.app.enums.UserRole;
import com.app.payload.request.RequestAlbum;
import com.app.payload.request.RequestToUpdateAlbum;
import com.app.payload.response.AlbumPreviewResponse;
import com.app.payload.response.ResponseAlbum;
import com.app.service.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.List;

@RestController
@RequestMapping("/api/albums/")
@RequiredArgsConstructor
public class AlbumController {

	private final AlbumService albumService;
	
	@GetMapping
	public Flux<AlbumPreviewResponse> getAlbums(
			@RequestParam(value = "ids", required = false) List<Integer> ids,
			@RequestParam(value = "authorId", required = false) List<Integer> authorId
		){
		if (ids == null && authorId == null ) {
			return Flux.error(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
		}
		
		return albumService.getAlbumByIds(ids, authorId);
	}
	
	@GetMapping("/{id}")
	public Mono<ResponseAlbum> getAlbum(@PathVariable("id") Integer id) {
		return albumService.getAlbumById(id);
	}
	
	
	@GetMapping("/symbol/{symbol}")
	public Flux<AlbumPreviewResponse> getAlbumBySymbol(@PathVariable("symbol") String symbol){
		return albumService.findAlbumBySymbol(URLDecoder.decode(symbol, Charset.defaultCharset()));
	}
	
	@PostMapping
	public Mono<ResponseEntity<?>> createAlbum(
			@Valid @ModelAttribute RequestAlbum dto,
			@RequestHeader("userRole") UserRole userRole
		) {
		return albumService.createAlbum(dto, userRole);
	}
	
	@PutMapping("/{id}")
	public Mono<Void> updateAlbum(
			@Valid @ModelAttribute RequestToUpdateAlbum dto, 
			@PathVariable("id") Integer id,
			@RequestHeader("userRole") UserRole userRole
		){
		return albumService.updateAlbum(dto, id, userRole);
	}
	
	@DeleteMapping("cover/{id}")
	public Mono<Void> deleteCoverOfAlbum(
			@PathVariable("id") Integer id,
			@RequestHeader("userRole") UserRole userRole
		){
		return albumService.deleteCoverById(id, userRole);
	}
	
	@DeleteMapping("/{id}")
	public Mono<Void> deleteAlbum(
			@PathVariable("id") Integer id,
			@RequestHeader("userRole") UserRole userRole
		){
		return albumService.deleteAlbum(id, userRole);
	}
	
	
}
