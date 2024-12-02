package com.gateway.config.filter.aggregation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gateway.payload.response.ResponsePreviewAlbumFromAPI;
import com.gateway.payload.response.ResponsePreviewAuthorFromAPI;
import com.gateway.payload.response.ResponseTrack;
import com.gateway.payload.response.ResponseTrackFromAPI;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RefreshScope
public class TracksAggregationFilter implements GatewayFilter {

	@Autowired
	private WebClient.Builder builder;

	@Autowired
	private ObjectMapper mapper;
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String path = exchange.getRequest().getURI().toString();
		int indexOf = path.indexOf('?');
		String substring = indexOf == -1 ?  "" : path.substring(indexOf);
		
		Mono<DataBuffer> dataBuffer = getTracks(substring).collectList().map(t -> {
				try {
					return mapper.writeValueAsBytes(t);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
					return null;
				}
		})
		.map(b -> exchange.getResponse().bufferFactory().wrap(b));

		exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
		
		return exchange.getResponse().writeWith(dataBuffer);
	}
	
	private Flux<ResponseTrack> collectTracks(
			List<ResponseTrackFromAPI> tracks, 
			List<ResponsePreviewAuthorFromAPI> authors,  
			List<ResponsePreviewAlbumFromAPI> albums
		){
		if (tracks == null || tracks.isEmpty()) {
			return Flux.empty();
		}
		
		return Flux.fromIterable(tracks)
			.map(t -> {
				ResponsePreviewAlbumFromAPI album = albums.stream()
						.filter(p -> t.getAlbumId() == p.getId())
						.findFirst().orElseGet(ResponsePreviewAlbumFromAPI::new);

				ResponsePreviewAuthorFromAPI author = authors
						.stream()
						.filter(a -> a.getId() == album.getAuthorId())
						.findFirst().orElseGet(ResponsePreviewAuthorFromAPI::new);
				
				return new ResponseTrack(t.getId(), t.getTitle(), album, author, t.getAudioUrl(), t.getDuration());
			});
	}
	
	protected Flux<ResponseTrack>  getTracks(String param) { 
		Flux<ResponseTrackFromAPI> tracks = fetchTracksFromService(param);
		Flux<ResponsePreviewAlbumFromAPI> albums = fetchAlbumsFromService(tracks);
		Flux<ResponsePreviewAuthorFromAPI> authors = fetchAuthorsFromService(albums);
		
		return Mono.zip(
				tracks.collectList(), 
				authors.collectList(), 
				albums.collectList())
		.flatMapMany(t -> collectTracks(t.getT1(), t.getT2(), t.getT3()));
	}
	
	private Flux<ResponseTrackFromAPI> fetchTracksFromService(String param){
		return builder
				.baseUrl("http://track-service/api/tracks/" + param)
				.build()
				.get()
				.accept(MediaType.APPLICATION_JSON)
				.exchangeToFlux(t -> t.bodyToFlux(ResponseTrackFromAPI.class));
	}
	
	private Flux<ResponsePreviewAuthorFromAPI> fetchAuthorsFromService(Flux<ResponsePreviewAlbumFromAPI> tracks){
		
		return tracks
				.map(ResponsePreviewAlbumFromAPI::getAuthorId)
				.collectList()
				.flatMapMany(t -> {
					return builder
							.baseUrl("http://author-service/api/authors/")
							.build()
							.get()
							.uri(u -> u.queryParam("ids", t).build())
							.accept(MediaType.APPLICATION_JSON)
							.exchangeToFlux(e -> e.bodyToFlux(ResponsePreviewAuthorFromAPI.class));
				});
	}
			
	private Flux<ResponsePreviewAlbumFromAPI> fetchAlbumsFromService(Flux<ResponseTrackFromAPI> tracks){
		return tracks
				.map(ResponseTrackFromAPI::getAlbumId)
				.collectList()
				.flatMapMany(t -> {
					return builder
							.baseUrl("http://album-service/api/albums/")
							.build()
							.get()
							.uri(u -> u.queryParam("ids", t).build())
							.accept(MediaType.APPLICATION_JSON)
							.exchangeToFlux(e -> e.bodyToFlux(ResponsePreviewAlbumFromAPI.class));
				});
	}
}
