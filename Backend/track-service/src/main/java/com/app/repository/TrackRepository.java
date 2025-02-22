package com.app.repository;

import com.app.model.Track;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TrackRepository extends ReactiveCrudRepository<Track, Long>{
	
	public Mono<Integer> countByAlbumId(Long albumId);
	
	Flux<Track> findByAlbumId(Integer albumId);
	
	
}
