package com.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.app.model.Playlist;
import com.app.model.projection.RequestPlaylist;
import com.app.model.projection.ResponseNamePlaylist;
import com.app.repository.PlaylistRepository;
import com.app.util.PlaylistMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class PlaylistService {

	private final PlaylistRepository playlistRepository;
	
	private final PlaylistMapper playlistMapper;
	
	public Flux<ResponseNamePlaylist> getPlatlistById(List<Integer> id) {
		return Flux.fromIterable(playlistRepository.findAllById(id))
				.map(playlistMapper::fromPlaylistToResponseNamePlaylist);
		
	}
	
	
	public void createPlaylist(RequestPlaylist dto, Integer userId) {
		Playlist playlist = playlistMapper.fromRequestPlaylistToPlaylist(dto, userId);
		
		playlistRepository.save(playlist);
	}
}
