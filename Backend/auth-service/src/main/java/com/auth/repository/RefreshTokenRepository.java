package com.auth.repository;

import com.auth.model.RefreshToken;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends ReactiveCrudRepository<RefreshToken, Integer>{

	Mono<RefreshToken> findByUserId(Integer userId);
	
	Mono<RefreshToken> findByToken(UUID token);
}
