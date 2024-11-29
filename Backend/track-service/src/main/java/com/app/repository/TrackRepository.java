package com.app.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.app.model.Track;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TrackRepository extends ReactiveCrudRepository<Track, Long>{
	
	public Mono<Long> countByAlbumId(Long albumId);
	
	Flux<Track> findByAlbumId(Integer albumId);
	
}
