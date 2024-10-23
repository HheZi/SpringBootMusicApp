package com.user.repository;

import java.util.Optional;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.user.model.User;

import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, Integer> {
	Mono<User> findByUsername(String username);
}
