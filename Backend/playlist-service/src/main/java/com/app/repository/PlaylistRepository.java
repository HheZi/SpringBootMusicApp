package com.app.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.app.model.Playlist;

import reactor.core.publisher.Flux;

import java.util.List;


public interface PlaylistRepository extends ReactiveCrudRepository<Playlist, Integer>{
	Flux<Playlist> findByNameStartsWithAllIgnoreCase(String name);
}
