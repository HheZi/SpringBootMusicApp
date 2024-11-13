package com.app.service;

import java.util.List;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.app.model.Track;
import com.app.payload.request.CreateTrackDto;
import com.app.payload.request.RequestSaveAudio;
import com.app.payload.response.ResponseTrack;
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

	private final R2dbcEntityTemplate template;
	
	public Flux<ResponseTrack> getTracks(
			String trackName, 
			List<Integer> authorId, 
			List<Integer> playlistId
		){
		Criteria criteria;

		if (trackName != null) {
			criteria = Criteria.where("title").like(trackName+ "%");
		}
		else if (authorId != null) {
			criteria = Criteria.where("author_id").in(authorId);
		}
		else if (playlistId != null) {
			criteria = Criteria.where("playlist_id").in(playlistId);
		}
		else {
			return repository.findAll()
					.map(mapper::fromTrackToResponseTrack);			
		}
		
		return template.select(Query.query(criteria), Track.class)
				.map(mapper::fromTrackToResponseTrack);
		
	}
	
	@Transactional
	public Mono<ResponseEntity<?>> createTrack(CreateTrackDto dto, Integer userId) {
		return repository.save(mapper.fromCreateTrackDtoToTrack(dto, userId))
				.doOnNext(t -> saveAudio(t.getAudioName().toString(), dto.getAudio()))
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
	
	public Mono<Long> countTracksByPlaylistId(Long playlistId){
		return repository.countByPlaylistId(playlistId);
	}

}
