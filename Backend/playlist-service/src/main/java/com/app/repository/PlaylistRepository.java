package com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.model.Playlist;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Integer>{

}
