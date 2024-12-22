package com.app.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.app.model.FavoriteTrack;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FavoriteTrackRepository extends ReactiveCrudRepository<FavoriteTrack, UUID>{

	Mono<FavoriteTrack> findByTrackIdAndUserId(Long trackId, Integer userId);
	
	Flux<FavoriteTrack> findByTrackIdInAndUserId(List<Long> trackId, Integer userId);
	
	Flux<FavoriteTrack> findByUserId(Integer userId);
}
