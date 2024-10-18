package com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.model.Track;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long>{

}
