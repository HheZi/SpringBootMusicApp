package com.app.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.app.model.PlaylistTrack;

public interface PlaylistTrackRepository extends ReactiveCrudRepository<PlaylistTrack, Integer>{

}
