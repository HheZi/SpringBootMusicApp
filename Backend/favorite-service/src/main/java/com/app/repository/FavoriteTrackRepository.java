package com.app.repository;

import com.app.model.FavoriteTrack;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Repository
public interface FavoriteTrackRepository extends ReactiveCrudRepository<FavoriteTrack, UUID>{

	Mono<FavoriteTrack> findByTrackIdAndUserId(Long trackId, Integer userId);
	
	Flux<FavoriteTrack> findByTrackIdInAndUserId(List<Long> trackId, Integer userId);
	
	Flux<FavoriteTrack> findByUserId(Integer userId);
	
	Mono<Void> deleteByTrackId(Long trackId);
}
