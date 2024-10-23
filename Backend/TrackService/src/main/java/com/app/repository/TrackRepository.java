package com.app.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.app.model.Track;

public interface TrackRepository extends ReactiveCrudRepository<Track, Long>{

}
