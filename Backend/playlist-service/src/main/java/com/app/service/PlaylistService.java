package com.app.service;

import java.util.List;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.app.model.Playlist;
import com.app.model.enums.PlaylistType;
import com.app.payload.request.RequestImage;
import com.app.payload.request.RequestPlaylist;
import com.app.payload.request.RequestToUpdatePlaylist;
import com.app.payload.response.ResponsePlaylist;
import com.app.repository.PlaylistRepository;
import com.app.util.PlaylistMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PlaylistService {

	private final PlaylistRepository playlistRepository;

	private final PlaylistMapper playlistMapper;

	private final WebClient.Builder builder;

	public Mono<ResponsePlaylist> getPlatlistById(Integer id) {
		return playlistRepository
				.findById(id)
				.map(playlistMapper::fromPlaylistToResponsePlaylist);
	}
	
	public Flux<ResponsePlaylist> getPlaylistsByIds(List<Integer> ids){
		return playlistRepository.findAllById(ids)
				.map(playlistMapper::fromPlaylistToResponsePlaylist);
	}
	
	public Flux<ResponsePlaylist> findPlaylistsBySymbol(String symbol){
		return playlistRepository.findByNameContainingIgnoreCase(symbol)
				.map(playlistMapper::fromPlaylistToResponsePlaylist);
	}

	public Mono<ResponseEntity<Integer>> createPlaylist(RequestPlaylist dto, Integer userId) {
		boolean coverIsPresent = dto.getCover().filename() != null && !dto.getCover().filename().isEmpty();
		
		Playlist playlist = playlistMapper.fromRequestPlaylistToPlaylist(dto, userId, coverIsPresent);

		if (coverIsPresent) 
			savePlaylistCover(playlist.getImageName().toString(), dto.getCover());			

		return playlistRepository.save(playlist)
				.map(t -> ResponseEntity.status(HttpStatus.CREATED).body(t.getId()));
	}

	private void savePlaylistCover(String name, FilePart filePart) {
		if (filePart == null) return;

		DataBufferUtils.join(filePart.content())
	    .map(dataBuffer -> {
	    	byte[] bs = new byte[dataBuffer.readableByteCount()];
	    	dataBuffer.read(bs);
	    	DataBufferUtils.release(dataBuffer);
	    	return bs;
	    })
	    .doOnNext(t -> {
	    	builder.build().post().uri("http://image-service/api/images/")
	    	.bodyValue(new RequestImage(name, t))
	    	.retrieve()
	    	.bodyToMono(Void.class).subscribe();
	    }).subscribe();

	}

	public Flux<PlaylistType> getPlaylistTypes() {
		return Flux.fromArray(PlaylistType.values());
	}

	public Mono<Boolean> userIsOwnerOfPlaylist(Integer playlistId, Integer userId){
		return playlistRepository.findById(playlistId)
				.flatMap(t -> t.getCreatedBy() == userId ? Mono.just(true) : Mono.just(false));
	}

	public Mono<ResponsePlaylist> updatePlaylist(RequestToUpdatePlaylist dto, Integer playlistId, Integer userId){
		return playlistRepository.findById(playlistId)
				.filter(t -> t.getCreatedBy() == userId)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "You are not a creator of the playlist")))
				.doOnNext(t -> savePlaylistCover(t.getImageName().toString(), dto.getCover()))
				.flatMap(t -> {
					if (dto.getName() != null) {
						t.setName(dto.getName());
					}
					return playlistRepository.save(t);
				})
				.map(playlistMapper::fromPlaylistToResponsePlaylist);
	}
}
