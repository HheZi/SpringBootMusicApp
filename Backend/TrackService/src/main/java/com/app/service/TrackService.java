package com.app.service;

import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.app.model.Track;
import com.app.model.projection.AuthorResponse;
import com.app.model.projection.CreateTrackDto;
import com.app.model.projection.ResponseTrack;
import com.app.repository.TrackRepository;
import com.app.util.TrackMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TrackService {

	private final TrackRepository repository;

	private final TrackMapper mapper;

	private final WebClient.Builder webClient;

	
	public Flux<ResponseTrack> getTracks(){
		return repository.findAll()
				.map(mapper::fromTrackToResponseTrack);
	}
	
	public void createTrack(CreateTrackDto dto, Integer userId) {
		findAuthorIdByName(dto.getAuthor())
				.doOnNext(t -> {
					Track track = mapper.fromCreateTrackDtoToTrack(dto, userId, t.getId());
					saveAudio(track.getAudioName(), dto.getAudio());
					
					repository.save(track);
				});
	}

	private void saveAudio(String name, FilePart filepart) {
		MultipartBodyBuilder builder = new MultipartBodyBuilder();
		builder.part("name", name);
		builder.part("content", filepart);
		
		webClient.baseUrl("http://audio-service/api/audio")
		.build()
		.post()
		.bodyValue(BodyInserters.fromMultipartData(builder.build()))
		.retrieve()
		.bodyToMono(Void.class)
		.subscribe();
		
	}
	
	private Mono<AuthorResponse> findAuthorIdByName(String name) {
		return webClient.baseUrl("http://author-service/api/authors/" + name)
		.build()
		.get()
		.retrieve()
		.bodyToMono(AuthorResponse.class);
		
	}
}
