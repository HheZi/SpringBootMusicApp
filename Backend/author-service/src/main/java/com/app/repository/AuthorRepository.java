package com.app.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.app.model.Author;

import reactor.core.publisher.Flux;

@Repository
public interface AuthorRepository extends ReactiveCrudRepository<Author, Integer>{
	Flux<Author> findByNameStartingWithIgnoreCase(String name);
}
