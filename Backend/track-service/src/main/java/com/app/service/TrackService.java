package com.app.service;

import com.app.enums.UserRole;
import com.app.kafka.message.TrackDeletionMessage;
import com.app.kafka.producer.KafkaTrackProducer;
import com.app.model.Track;
import com.app.payload.UploadTrack;
import com.app.payload.request.CreateTrackDto;
import com.app.payload.request.UpdateTrackRequest;
import com.app.payload.response.ResponseTotalDuration;
import com.app.payload.response.ResponseTrack;
import com.app.repository.TrackRepository;
import com.app.util.TrackMapper;
import com.mpatric.mp3agic.Mp3File;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrackService {

	private final TrackRepository repository;

	private final TrackMapper mapper;

	private final AudioClient audioClient;

	private final R2dbcEntityTemplate template;

	private final KafkaTrackProducer kafkaTrackProducer;

	private final String TEMP_FOLDER_NAME = "temp";

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
				.map(Track::getDuration)
				.reduce(Long::sum)
				.map(mapper::getDurationOfTrack);
	}

	@Transactional
	public Mono<ResponseEntity<?>> createTrack(CreateTrackDto dto, UserRole userRole) {
		if (userRole != UserRole.ADMIN){
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}

		Path path = Paths.get(TEMP_FOLDER_NAME, dto.getAudio().filename());

		return dto.getAudio().transferTo(path)
				.then(Mono.just(new UploadTrack(path, UUID.randomUUID())))
				.flatMap(audioClient::saveAudio)
				.flatMap(t ->
                        Mono.zip(
                                Mono.fromCallable(() -> new Mp3File(t.getPath())),
								Mono.just(t.getName())
                        )
				)
				.map(objects -> mapper.fromCreateTrackDtoToTrack(dto, objects.getT2(), objects.getT1()))
				.flatMap(repository::save)
				.doFinally(t -> this.deleteTrackFile(path))
				.map(track -> ResponseEntity.status(HttpStatus.CREATED).build());
	}

	@SneakyThrows
	public void deleteTrackFile(Path path) {
		Files.delete(path);
	}

	@Transactional
	public Mono<Void> deleteTrack(Long id, UserRole userRole){
		if (userRole != UserRole.ADMIN){
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}

		return repository.findById(id)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.doOnNext(t -> {
					kafkaTrackProducer.sendMessage(new TrackDeletionMessage(t.getId(), t.getAudioName().toString()));
				})
				.flatMap(repository::delete);
	}

	@Transactional
	public Mono<Void> deleteTracksByAlbumId(Integer albumId) {
		return repository.findByAlbumId(albumId)
		.doOnNext(t -> {
			kafkaTrackProducer.sendMessage(new TrackDeletionMessage(t.getId(), t.getAudioName().toString()));
		})
		.collectList()
		.flatMap(repository::deleteAll);
	}

	@Transactional
	public Mono<Void> updateTrackTitle(UpdateTrackRequest updateTrack, Long trackId, UserRole userRole){
		if (userRole != UserRole.ADMIN){
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}

		return repository.findById(trackId)
		.doOnNext(t -> {
				t.setTitle(updateTrack.getTitle());
		})
		.flatMap(repository::save)
		.then();
	}

}
