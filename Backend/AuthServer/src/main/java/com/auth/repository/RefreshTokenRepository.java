package com.auth.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.auth.model.RefreshToken;

public interface RefreshTokenRepository extends ReactiveCrudRepository<RefreshToken, Integer>{

}
