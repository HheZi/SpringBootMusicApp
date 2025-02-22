package com.app.repository;

import com.app.model.Album;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public interface AlbumRepository extends ReactiveCrudRepository<Album, Integer>{

	Flux<Album> findByNameStartingWithAllIgnoreCase(String name);
	
	Flux<Album> findByAuthorIdIn(List<Integer> authorId);
}
