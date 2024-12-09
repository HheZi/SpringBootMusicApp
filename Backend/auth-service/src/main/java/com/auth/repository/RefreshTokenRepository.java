package com.auth.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.auth.model.RefreshToken;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends ReactiveCrudRepository<RefreshToken, Integer>{

	Mono<RefreshToken> findByUserId(Integer userId);
	
	Mono<RefreshToken> findByToken(UUID token);
}
