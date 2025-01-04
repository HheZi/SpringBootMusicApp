package com.auth.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.auth.model.RefreshToken;

import reactor.core.publisher.Mono;

@Repository
public interface RefreshTokenRepository extends ReactiveCrudRepository<RefreshToken, Integer>{

	Mono<RefreshToken> findByUserId(Integer userId);
	
	Mono<RefreshToken> findByToken(UUID token);
}
