package com.app.repository;

import com.app.model.Playlist;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PlaylistRepository extends ReactiveCrudRepository<Playlist, Integer>{
	Flux<Playlist> findByNameStartsWithAllIgnoreCase(String name);
	
	Flux<Playlist> findByCreatedBy(Integer createdBy);
}
