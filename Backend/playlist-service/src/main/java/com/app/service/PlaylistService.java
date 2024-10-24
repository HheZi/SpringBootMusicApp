package com.app.service;

import static com.app.model.enums.PlaylistType.*;

import java.util.List;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.app.model.Playlist;
import com.app.model.enums.PlaylistType;
import com.app.model.projection.RequestPlaylist;
import com.app.model.projection.ResponseNamePlaylist;
import com.app.repository.PlaylistRepository;
import com.app.util.PlaylistMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class PlaylistService {

	private final PlaylistRepository playlistRepository;

	private final PlaylistMapper playlistMapper;

	private WebClient.Builder builder;

	public Flux<ResponseNamePlaylist> getPlatlistById(List<Integer> id) {
		return playlistRepository.findAllById(id)
				.map(playlistMapper::fromPlaylistToResponseNamePlaylist);

	}

	public void createPlaylist(RequestPlaylist dto, Integer userId) {

		Playlist playlist = playlistMapper.fromRequestPlaylistToPlaylist(dto, userId, PLAYLIST);

		savePlaylistCover(playlist.getImageName(), dto.getCover());

		playlistRepository.save(playlist);
	}

	private void savePlaylistCover(String name, FilePart filePart) {
		MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
		multipartBodyBuilder.part("content", filePart);
		multipartBodyBuilder.part("name", name);

		builder.baseUrl("http://image-service/api/images").build().post()
				.bodyValue(BodyInserters.fromMultipartData(multipartBodyBuilder.build())).retrieve()
				.bodyToMono(Void.class).subscribe();

	}

	public Flux<PlaylistType> getPlaylistTypes(){
		return Flux.fromArray(PlaylistType.values());
	}
	
}
