package com.app.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.app.model.Playlist;

import reactor.core.publisher.Flux;


public interface PlaylistRepository extends ReactiveCrudRepository<Playlist, Integer>{
	Flux<Playlist> findByNameStartsWithAllIgnoreCase(String name);
	
	Flux<Playlist> findByCreatedBy(Integer createdBy);
}
