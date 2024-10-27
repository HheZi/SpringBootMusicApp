package com.app.service;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.app.model.Track;
import com.app.model.projection.AuthorResponse;
import com.app.model.projection.CreateTrackDto;
import com.app.model.projection.RequestSaveAudio;
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
	
	public Mono<ResponseEntity<?>> createTrack(CreateTrackDto dto, Integer userId) {
		return repository.save(mapper.fromCreateTrackDtoToTrack(dto, userId))
				.doOnNext(t -> saveAudio(t.getAudioName(), dto.getAudio()))
				.map(t -> ResponseEntity.status(HttpStatus.CREATED).build());
	}

	private void saveAudio(String name, FilePart audio) {
		
		DataBufferUtils.join(audio.content())
        .map(dataBuffer -> {
            byte[] content = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(content);  
            DataBufferUtils.release(dataBuffer);  
            return content;
        })
        .doOnNext(t -> webClient.build().post().uri("http://audio-service/api/audio")
            .bodyValue(new RequestSaveAudio(name, t))
            .retrieve()
            .bodyToMono(Void.class)
            .subscribe())
        .subscribe();
		
	}
	
}
