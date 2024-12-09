package com.app.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.app.model.Playlist;

import reactor.core.publisher.Flux;

@Repository
public interface PlaylistRepository extends ReactiveCrudRepository<Playlist, Integer>{
	Flux<Playlist> findByNameStartsWithAllIgnoreCase(String name);
	
	Flux<Playlist> findByCreatedBy(Integer createdBy);
}
