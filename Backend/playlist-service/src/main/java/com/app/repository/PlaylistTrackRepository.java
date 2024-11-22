package com.app.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.app.model.PlaylistTrack;

import reactor.core.publisher.Flux;

import java.util.List;


public interface PlaylistTrackRepository extends ReactiveCrudRepository<PlaylistTrack, Integer>{
	Flux<PlaylistTrack> findByPlaylistId(Integer playlistId);
}
