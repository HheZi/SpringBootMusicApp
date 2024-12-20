package com.app.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.app.model.FavoriteTrack;
import com.app.repository.FavoriteTrackRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FavoriteTrackService {
	
	private final FavoriteTrackRepository favoriteTrackRepository; 
	
	public Flux<Long> getTrackInFavorites(List<Long> trackIds, Integer userId){
		return favoriteTrackRepository
				.findByTrackIdInAndUserId(trackIds, userId)
				.switchIfEmpty(Flux.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.map(t -> t.getTrackId());
	}
	
	@Transactional
	public Mono <FavoriteTrack> addTrackToFavorites(Long trackId,Integer userId){
		return favoriteTrackRepository
				.findByTrackIdAndUserId(trackId, userId)
				.hasElement()
				.flatMap(t -> {
					return t ? Mono.error(() -> new ResponseStatusException(HttpStatus.CONFLICT)) 
							: Mono.just(new FavoriteTrack(trackId, userId));
				})
				.flatMap(favoriteTrackRepository::save);
	}
	
	@Transactional
	public Mono<Void> deleteTrackFromFavorites(Long trackId,Integer userId){
		return favoriteTrackRepository
				.findByTrackIdAndUserId(trackId, userId)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.flatMap(favoriteTrackRepository::delete);
	}
	
}
