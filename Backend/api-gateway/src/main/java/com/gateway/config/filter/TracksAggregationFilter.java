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
import com.gateway.payload.response.ResponseAlbumFromAPI;
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
		Flux<ResponseAlbumFromAPI> albums = fetchAlbumsFromService(tracks);
		
		Flux<ResponseTracks> result = Mono.zip(tracks.collectList(), authors.collectList(), albums.collectList())
		.flatMapMany(t -> collectTracks(t.getT1(), t.getT2(), t.getT3()));
		
		
		
		Mono<DataBuffer> dataBuffer = result.collectList().map(t -> {
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
	
	private Flux<ResponseTracks> collectTracks(
			List<ResponseTrackFromAPI> tracks, 
			List<ResponseAuthorFromAPI> authors,  
			List<ResponseAlbumFromAPI> albums
		){
		
		return Flux.fromIterable(tracks)
			.map(t -> {
				ResponseAuthorFromAPI author = authors
						.stream()
						.filter(a -> t.getAuthorId() == a.getId())
						.findFirst().orElseGet(ResponseAuthorFromAPI::new);
				
				ResponseAlbumFromAPI album = albums.stream()
						.filter(p -> t.getAlbumId() == p.getId())
						.findFirst().orElseGet(ResponseAlbumFromAPI::new);
				
				return new ResponseTracks(t.getId(), t.getTitle(), album, author, t.getAudioUrl(), t.getDuration());
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
							.uri(u -> u.queryParam("ids", t).build())
							.accept(MediaType.APPLICATION_JSON)
							.exchangeToFlux(e -> e.bodyToFlux(ResponseAuthorFromAPI.class));
				});
	}
			
	private Flux<ResponseAlbumFromAPI> fetchAlbumsFromService(Flux<ResponseTrackFromAPI> tracks){
		return tracks.map(ResponseTrackFromAPI::getAlbumId)
				.collectList()
				.flatMapMany(t -> {
					return builder
							.baseUrl("http://album-service/api/albums/")
							.build()
							.get()
							.uri(u -> u.queryParam("ids", t).build())
							.accept(MediaType.APPLICATION_JSON)
							.exchangeToFlux(e -> e.bodyToFlux(ResponseAlbumFromAPI.class));
				});
	}
}
