package com.app.repository;

import com.app.model.PlaylistTrack;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PlaylistTrackRepository extends ReactiveCrudRepository<PlaylistTrack, Integer>{
	Flux<PlaylistTrack> findByPlaylistId(Integer playlistId);
	
	@Modifying
	Mono<Void> deleteByPlaylistId(Integer playlistId);
	
	@Modifying
	Mono<Void> deleteByTrackId(Long trackId);
	
	Mono<PlaylistTrack> findByPlaylistIdAndTrackId(Integer playlistId, Long trackId);
	
	Mono<Boolean> existsByPlaylistIdAndTrackId(Integer playlistId, Long trackId);
}
