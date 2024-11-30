package com.app.repository;

import java.util.List;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.app.model.Album;

import reactor.core.publisher.Flux;

public interface AlbumRepository extends ReactiveCrudRepository<Album, Integer>{

	Flux<Album> findByNameStartingWithAllIgnoreCase(String name);
	
	Flux<Album> findByAuthorIdIn(List<Integer> authorId);
}
