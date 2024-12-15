package com.app.service;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.app.kafka.KafkaTrackProducer;
import com.app.kafka.message.TrackDeletionMessage;
import com.app.model.Track;
import com.app.payload.request.CreateTrackDto;
import com.app.payload.request.UpdateTrackRequest;
import com.app.payload.response.ResponseTotalDuration;
import com.app.payload.response.ResponseTrack;
import com.app.repository.TrackRepository;
import com.app.util.TrackMapper;
import com.mpatric.mp3agic.Mp3File;

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
	
	private final KafkaTrackProducer kafkaTrackProducer;
	
	@Value("${file.temp}")
	private String tempFolder;
	
	@Transactional
	public Mono<Page<ResponseTrack>> getTracks(
			String trackName, 
			List<Integer> albumId,
			List<Long> ids,
			Integer page,
			Integer size
		){
		Criteria criteria = Criteria.empty();
		
		PageRequest pageable =  PageRequest.of(page, size);
		Mono<Long> count = template.count(Query.empty(), Track.class);

		if (trackName != null) {
			criteria = Criteria.where("title").like(trackName+ "%").ignoreCase(true);
			count = template.count(Query.query(criteria), Track.class);
		}
		else if (albumId != null) {
			criteria = Criteria.where("album_id").in(albumId);
			count = template.count(Query.query(criteria), Track.class);
		}
		else if (ids != null) {
			criteria = Criteria.where("id").in(ids);
			count = template.count(Query.query(criteria), Track.class);
		}
		
		Flux<ResponseTrack> tracks = template.select(Query.query(criteria).with(pageable), Track.class)
		.map(mapper::fromTrackToResponseTrack);
		
		return Mono.zip(tracks.collectList(), count)
				.map(t -> new PageImpl<ResponseTrack>(t.getT1(), pageable, t.getT2()));
		
	}
	
	public Mono<Integer> countTracksByAlbumId(Long albumId){
		return repository.countByAlbumId(albumId);
	}

	public Mono<ResponseTotalDuration> totalTimeOfTracks(List<Long> ids, Integer albumId) {
		Flux<Track> tracks = Flux.empty();
		
		if (ids == null && albumId == null ) {
			return Mono.error(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
		}
		else if(ids != null){
			tracks = repository.findAllById(ids);
		}
		else if (albumId != null) {
			tracks = repository.findByAlbumId(albumId);
		}
		
		return tracks
				.switchIfEmpty(Flux.just(new Track()))
				.map(t -> t.getDuration())
				.reduce(Long::sum)
				.map(mapper::getDurationOfTrack);
	}
	
	@Transactional
	public Mono<ResponseEntity<?>> createTrack(CreateTrackDto dto, Integer userId) {
		Path path = Path.of(tempFolder, dto.getAudio().filename());
		
		return dto.getAudio().transferTo(path)
				.then(Mono.fromCallable(() -> new Mp3File(path.toString())))
				.flatMap(t -> repository.save(mapper.fromCreateTrackDtoToTrack(dto, userId, t)))
				.flatMap(t -> saveAudio(t.getAudioName().toString(), path))
				.doFinally(t -> path.toFile().delete())
				.map(t -> ResponseEntity.status(HttpStatus.CREATED).build());
	}

	private Mono<Void> saveAudio(String name, Path pathToTempAudio) {
		if(name  == null || pathToTempAudio == null) return Mono.empty();
		
		MultipartBodyBuilder builder = new MultipartBodyBuilder();
		
		builder.part("file", new FileSystemResource(pathToTempAudio));
		builder.part("name", name);
		
		return webClient.build()
    	.post().uri("http://audio-service/api/audio")
        .body(BodyInserters.fromMultipartData(builder.build()))
        .retrieve()
        .bodyToMono(Void.class);
		
		
	}
	
	@Transactional
	public Mono<Void> deleteTrack(Long id){
		return repository.findById(id)
		.doOnNext(t -> {
			deleteTrackFile(t.getAudioName());
			kafkaTrackProducer.sendMessage(new TrackDeletionMessage(t.getId()));
		})
		.flatMap(repository::delete);
	}
	
	@Transactional
	public Mono<Void> deleteTracksByAlbumId(Integer albumId) {
		return repository.findByAlbumId(albumId)
		.doOnNext(t -> {
			deleteTrackFile(t.getAudioName());
			kafkaTrackProducer.sendMessage(new TrackDeletionMessage(t.getId()));
		})
		.collectList()
		.flatMap(repository::deleteAll);
	}
	
	private void deleteTrackFile(UUID trackName) {
		webClient.baseUrl("http://audio-service/api/audio/" + trackName.toString())
		.build()
		.delete()
		.retrieve()
		.bodyToMono(Void.class)
		.subscribe();
	}
	
	@Transactional
	public Mono<Void> updateTrackTitle(UpdateTrackRequest updateTrack, Long trackId, Integer userId){
		return repository.findById(trackId)
		.filter(t -> t.getCreatedBy() == userId)
		.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE)))
		.doOnNext(t -> {
				t.setTitle(updateTrack.getTitle());
		})
		.flatMap(repository::save)
		.then();
	}

}
