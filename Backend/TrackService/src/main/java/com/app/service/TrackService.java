package com.app.service;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.app.model.Track;
import com.app.model.projection.CreateTrackDto;
import com.app.model.projection.RequestSaveAudio;
import com.app.model.projection.ResponseTrack;
import com.app.repository.TrackRepository;
import com.app.util.TrackMapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
@RequiredArgsConstructor
public class TrackService {

	private final TrackRepository repository;

	private final TrackMapper mapper;

	private final WebClient.Builder webClient;

	
	public Flux<ResponseTrack> getTracks(){
		return Flux.fromIterable(repository.findAll())
				.map(mapper::fromTrackToResponseTrack);
	}
	
	@SneakyThrows
	public void createTrack(Tuple2<CreateTrackDto, FilePart> dto) {
		Track track = mapper.fromCreateTrackDtoToTrack(dto.getT1());

		DataBufferUtils.join(dto.getT2().content())
        .map(dataBuffer -> {
            byte[] content = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(content);  
            DataBufferUtils.release(dataBuffer);  
            return content;
        })
        .doOnNext(t -> webClient.build().post().uri("http://AudioService/api/audio")
            .bodyValue(new RequestSaveAudio(track.getAudioName(), t))
            .retrieve()
            .bodyToMono(Void.class)
            .subscribe())
        .subscribe();

		repository.save(track);

	}

}
