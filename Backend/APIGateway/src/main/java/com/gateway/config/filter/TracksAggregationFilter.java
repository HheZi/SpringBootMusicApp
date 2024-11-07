package com.gateway.config.filter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gateway.payload.response.ResponseAuthorFromAPI;
import com.gateway.payload.response.ResponsePlaylistFromAPI;
import com.gateway.payload.response.ResponseTrackFromAPI;
import com.gateway.payload.response.ResponseTracks;

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
		Flux<ResponseTrackFromAPI> tracks = fetchTracksFromService(exchange);
		
		Flux<ResponseAuthorFromAPI> authors = fetchAuthorsFromService(tracks);
		Flux<ResponsePlaylistFromAPI> playlists = fetchPlaylistsFromService(tracks);
		
		Flux<ResponseTracks> result = Mono.zip(tracks.collectList(), authors.collectList(), playlists.collectList())
		.flatMapMany(t -> collectTracks(t.getT1(), t.getT2(), t.getT3()));
		
		exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
		
		Mono<DataBuffer> dataBuffer = result.collectList().map(t -> {
				try {
					return mapper.writeValueAsBytes(t);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
					return null;
				}
		})
		.map(b -> exchange.getResponse().bufferFactory().wrap(b));

		
		return exchange.getResponse().writeWith(dataBuffer);
	}
	
	private Flux<ResponseTracks> collectTracks(
			List<ResponseTrackFromAPI> tracks, 
			List<ResponseAuthorFromAPI> authors,  
			List<ResponsePlaylistFromAPI> playlists
		){
		
		return Flux.fromIterable(tracks)
			.map(t -> {
				ResponseAuthorFromAPI author = authors
						.stream()
						.filter(a -> t.getAuthorId() == a.getId())
						.findFirst().orElseGet(ResponseAuthorFromAPI::new);
				
				ResponsePlaylistFromAPI playlist = playlists.stream()
						.filter(p -> t.getPlaylistId() == p.getId())
						.findFirst().orElseGet(ResponsePlaylistFromAPI::new);
				
				return new ResponseTracks(t.getId(), t.getTitle(), playlist, author, t.getAudioUrl());
			});
	}
	
	private Flux<ResponseTrackFromAPI> fetchTracksFromService(ServerWebExchange exchange){
		String path = exchange.getRequest().getURI().toString();
		int indexOf = path.indexOf('?');
		String substring = indexOf == -1 ?  "" : path.substring(indexOf);
		
		return builder
				.baseUrl("http://track-service/api/tracks/" + substring)
				.build()
				.get()
				.accept(MediaType.APPLICATION_JSON)
				.exchangeToFlux(t -> t.bodyToFlux(ResponseTrackFromAPI.class));
	}
	
	private Flux<ResponseAuthorFromAPI> fetchAuthorsFromService(Flux<ResponseTrackFromAPI> tracks){
		return tracks.map(ResponseTrackFromAPI::getAuthorId)
				.collectList()
				.flatMapMany(t -> {
					return builder
							.baseUrl("http://author-service/api/authors/")
							.build()
							.get()
							.uri(u -> u.queryParam("id[]", t).build())
							.accept(MediaType.APPLICATION_JSON)
							.exchangeToFlux(e -> e.bodyToFlux(ResponseAuthorFromAPI.class));
				});
	}
			
	private Flux<ResponsePlaylistFromAPI> fetchPlaylistsFromService(Flux<ResponseTrackFromAPI> tracks){
		return tracks.map(ResponseTrackFromAPI::getPlaylistId)
				.collectList()
				.flatMapMany(t -> {
					return builder
							.baseUrl("http://playlist-service/api/playlists/")
							.build()
							.get()
							.uri(u -> u.queryParam("id[]", t).build())
							.accept(MediaType.APPLICATION_JSON)
							.exchangeToFlux(e -> e.bodyToFlux(ResponsePlaylistFromAPI.class));
				});
	}
}
