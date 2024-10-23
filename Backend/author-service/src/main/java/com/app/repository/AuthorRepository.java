package com.app.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.app.model.Author;

public interface AuthorRepository extends ReactiveCrudRepository<Author, Integer>{

}
