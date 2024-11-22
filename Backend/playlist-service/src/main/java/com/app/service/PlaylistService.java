package com.app.service;

import java.util.UUID;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.app.payload.request.CreatePlaylist;
import com.app.payload.request.SavePlaylistImageRequest;
import com.app.payload.response.ResponsePlaylist;
import com.app.repository.PlaylistRepository;
import com.app.util.PlaylistMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PlaylistService {

	private final PlaylistRepository playlistRepository;
	
	private final PlaylistMapper playlistMapper;
	
	private final WebClient.Builder builder;
	
	public Mono<ResponsePlaylist> getPlaylistById(Integer id){
		return playlistRepository.findById(id)
				.map(playlistMapper::fromPlaylistToResponsePlaylist);
	}
	
	public Mono<ResponseEntity<?>> createPlaylist(CreatePlaylist dto, Integer userId){
		return Mono.just(
				playlistMapper.fromCreatePlaylistToPlaylist(dto, userId, dto.getCover() == null))
				.doOnNext(t -> saveAuthorImage(t.getImageName(), dto.getCover()))
				.flatMap(playlistRepository::save)
				.map(t -> ResponseEntity.status(HttpStatus.CREATED).build());
		
	}
	
	private void saveAuthorImage(UUID name, FilePart filePart) {
		if (name ==  null || filePart == null) return;			
		
		DataBufferUtils.join(filePart.content())
	    .map(dataBuffer -> {
	    	byte[] bs = new byte[dataBuffer.readableByteCount()];
	    	dataBuffer.read(bs);
	    	DataBufferUtils.release(dataBuffer);
	    	return bs;
	    })
	    .flatMap(t -> {
	    	return builder.build()
	    			.post()
	    			.uri("http://image-service/api/images/")
	    			.bodyValue(new SavePlaylistImageRequest(name.toString(), t))
	    			.retrieve()
	    			.bodyToMono(Void.class);
	    }).subscribe();
		
	}
}
