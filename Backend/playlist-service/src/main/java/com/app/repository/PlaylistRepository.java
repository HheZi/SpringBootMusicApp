package com.app.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.app.model.Playlist;

public interface PlaylistRepository extends ReactiveCrudRepository<Playlist, Integer>{
	
}
