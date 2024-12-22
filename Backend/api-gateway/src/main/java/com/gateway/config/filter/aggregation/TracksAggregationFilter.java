package com.gateway.config.filter.aggregation;

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
import com.gateway.payload.response.PageResponseTrack;
import com.gateway.payload.response.PageResponseTrackFromAPI;
import com.gateway.payload.response.ResponsePreviewAlbumFromAPI;
import com.gateway.payload.response.ResponsePreviewAuthorFromAPI;
import com.gateway.payload.response.ResponseTrack;

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
		
		String userId = exchange.getRequest().getHeaders().getOrEmpty("userId").get(0);
		
		Mono<DataBuffer> dataBuffer = getTracks(substring, userId).map(t -> {
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
	
	protected Mono<PageResponseTrack>  getTracks(String param, String userId) { 
		Mono<PageResponseTrackFromAPI>  tracks = fetchTracksFromService(param);
		
		Flux<ResponsePreviewAlbumFromAPI> albums = fetchAlbumsFromService(tracks);
		Flux<ResponsePreviewAuthorFromAPI> authors = fetchAuthorsFromService(albums);
		Flux<Long> tracksInFavorites = fetchTracksInFavorites(tracks, userId);
		
		return Mono.zip(
				tracks, 
				authors.collectList(), 
				albums.collectList(),
				tracksInFavorites.collectList())
				.flatMap(t -> collectTracks(t.getT1(), t.getT2(), t.getT3(), t.getT4()));
	}
	
	private Mono<PageResponseTrack> collectTracks(
			PageResponseTrackFromAPI tracks, 
			List<ResponsePreviewAuthorFromAPI> authors,  
			List<ResponsePreviewAlbumFromAPI> albums,
			List<Long> tracksInFavorites
		){
		if (tracks.getEmpty()) {
			return Mono.just(new PageResponseTrack(List.of(), tracks));
		}
		
		List<ResponseTrack> list = tracks.getContent()
				.stream()
			.map(t -> {
				ResponsePreviewAlbumFromAPI album = albums.stream()
						.filter(p -> t.getAlbumId() == p.getId())
						.findFirst().orElseGet(ResponsePreviewAlbumFromAPI::new);

				ResponsePreviewAuthorFromAPI author = authors
						.stream()
						.filter(a -> a.getId() == album.getAuthorId())
						.findFirst().orElseGet(ResponsePreviewAuthorFromAPI::new);
				
				Boolean isInFavorites = tracksInFavorites
				.stream()
				.filter(r -> r == t.getId())
				.findFirst().map(r -> true).orElse(false);
								
				return new ResponseTrack(t.getId(), t.getTitle(), album, author, t.getAudioUrl(), t.getDuration(), isInFavorites);
			})
			.toList();
		
		return Mono.just(new PageResponseTrack(list, tracks));
	}
	
	
	private Mono<PageResponseTrackFromAPI> fetchTracksFromService(String param){
		return builder
				.baseUrl("http://track-service/api/tracks/" + param)
				.build()
				.get()
				.accept(MediaType.APPLICATION_JSON)
				.exchangeToMono(t -> t.bodyToMono(PageResponseTrackFromAPI.class));
	}
	
	private Flux<ResponsePreviewAuthorFromAPI> fetchAuthorsFromService(Flux<ResponsePreviewAlbumFromAPI> albums){
		return albums
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
			
	private Flux<ResponsePreviewAlbumFromAPI> fetchAlbumsFromService(Mono<PageResponseTrackFromAPI> tracks){
		return tracks
				.filter(t -> !t.getContent().isEmpty())
				.map(t -> t.getContent().stream().map(tr -> tr.getAlbumId()).toList())
				.flatMapMany(t -> 
					 	builder
							.baseUrl("http://album-service/api/albums/")
							.build()
							.get()
							.uri(u -> u.queryParam("ids", t).build())
							.accept(MediaType.APPLICATION_JSON)
							.exchangeToFlux(e -> e.bodyToFlux(ResponsePreviewAlbumFromAPI.class))
				);
	}
	
	private Flux<Long> fetchTracksInFavorites(Mono<PageResponseTrackFromAPI> tracks, String userId){
		return tracks
				.map(t -> t.getContent().stream().map(r -> r.getId()).toList())
				.flatMapMany(t -> 
						builder
						.baseUrl("http://favorite-service/api/favorites/tracks/")
						.build()
						.get()
						.uri(u -> u.queryParam("trackId", t).build())
						.header("userId", userId + "")
						.accept(MediaType.APPLICATION_JSON)
						.exchangeToFlux(e -> e.bodyToFlux(Long.class)));
	}
}
