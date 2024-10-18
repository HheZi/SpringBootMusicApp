package com.app.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.app.model.Track;
import com.app.model.projection.CreateTrackDto;
import com.app.model.projection.RequestSaveAudio;
import com.app.repository.TrackRepository;
import com.app.util.TrackMapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackService {
	
	private final TrackRepository repository;
	
	private final TrackMapper mapper;
	
	private final WebClient.Builder webClient;
	
	@SneakyThrows
	public void createTrack(CreateTrackDto dto) {
		Track track = mapper.fromCreateTrackDtoToTrack(dto);
		
		log.warn("dto is {}", dto);
		
		webClient
		.build()
		.post()
		.uri("http://AudioService/api/audio")
		.bodyValue(new RequestSaveAudio(track.getAudioName(), dto.getFile().getBytes()));
		
		repository.save(track);
	}
	
}
