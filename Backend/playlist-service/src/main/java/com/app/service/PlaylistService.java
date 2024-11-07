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

import com.app.model.Playlist;
import com.app.model.enums.PlaylistType;
import com.app.payload.request.RequestImage;
import com.app.payload.request.RequestPlaylist;
import com.app.payload.response.ResponseNamePlaylist;
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

	public Mono<ResponseNamePlaylist> getPlatlistById(Integer id) {
		return playlistRepository
				.findById(id)
				.map(playlistMapper::fromPlaylistToResponseNamePlaylist);
	}
	
	public Flux<ResponseNamePlaylist> getPlaylistsByIds(List<Integer> ids){
		return playlistRepository.findAllById(ids)
				.map(playlistMapper::fromPlaylistToResponseNamePlaylist);
	}
	
	public Flux<ResponseNamePlaylist> findPlaylistsBySymbol(String symbol){
		return playlistRepository.findByNameContainingIgnoreCase(symbol)
				.map(playlistMapper::fromPlaylistToResponseNamePlaylist);
	}

	public Mono<ResponseEntity<Integer>> createPlaylist(RequestPlaylist dto, FilePart cover, Integer userId) {
		boolean coverIsPresent = cover.filename() != null && !cover.filename().isEmpty();
		
		Playlist playlist = playlistMapper.fromRequestPlaylistToPlaylist(dto, userId, coverIsPresent);

		if (coverIsPresent) 
			savePlaylistCover(playlist.getImageName().toString(), cover);			

		return playlistRepository.save(playlist)
				.map(t -> ResponseEntity.status(HttpStatus.CREATED).body(t.getId()));
	}

	private void savePlaylistCover(String name, FilePart filePart) {

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

}
