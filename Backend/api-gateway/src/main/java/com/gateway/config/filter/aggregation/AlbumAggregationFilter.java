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
import com.gateway.payload.response.ResponseAlbum;
import com.gateway.payload.response.ResponseAlbumDuration;
import com.gateway.payload.response.ResponseAlbumFromService;
import com.gateway.payload.response.ResponseAuthorFromService;
import com.gateway.payload.response.ResponseTrack;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RefreshScope
public class AlbumAggregationFilter implements GatewayFilter{

	@Autowired
	private WebClient.Builder builder;

	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private TracksAggregationFilter tracksAggregationFilter;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String[] path = exchange.getRequest().getURI().getPath().split("/");
		
		String idString = path[path.length-1];
		try {
			Integer id = Integer.parseInt(idString);
			
			Mono<DataBuffer> resp = fetchAlbumsFromService(id)
				.flatMap(t -> {
					Flux<ResponseTrack> tracks = tracksAggregationFilter.getTracks("?albumId="+t.getId());
					Mono<String> albumDuration = fetchAlbumDuration(tracks);
					Mono<Integer> tracksInAlbum = fetchTracksInAlbum(t.getId());
					Mono<ResponseAuthorFromService> author = fetchAuthorsFromService(t.getAuthorId());
					
					return buildAlbum(t, tracks.collectList(), albumDuration, tracksInAlbum, author);
				})
				.map(t -> {
					try {
						return mapper.writeValueAsBytes(t);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
						return null;
					}
				})
				.map(b -> exchange.getResponse().bufferFactory().wrap(b));
			
			exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
			
			return exchange.getResponse().writeWith(resp);
			
		} catch (NumberFormatException e) {
			return Mono.error(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
		}
	}
	
	
	private Mono<ResponseAlbumFromService> fetchAlbumsFromService(Integer id){
		return builder
				.baseUrl("http://album-service/api/albums/"+id)
				.build()
				.get()
				.accept(MediaType.APPLICATION_JSON)
				.exchangeToMono(e -> e.bodyToMono(ResponseAlbumFromService.class));
	}
	
	private Mono<String> fetchAlbumDuration(Flux<ResponseTrack> tracks){ 
		return tracks.map(ResponseTrack::getId)
		.collectList()
		.flatMap(t -> builder
				.baseUrl("http://track-service/api/tracks/duration")
				.build()
				.get()
				.uri(u -> u.queryParam("ids", t).build())
				.accept(MediaType.APPLICATION_JSON)
				.exchangeToMono(e -> e.bodyToMono(ResponseAlbumDuration.class))
				.map(ResponseAlbumDuration::getDuration));
	}

	private Mono<Integer> fetchTracksInAlbum(Integer id){
		return builder
				.baseUrl("http://track-service/api/tracks/count/"+ id)
				.build()
				.get()
				.accept(MediaType.APPLICATION_JSON)
				.exchangeToMono(e -> e.bodyToMono(Integer.class));
	}
	
	private Mono<ResponseAuthorFromService> fetchAuthorsFromService(Integer authorid) {
		return builder.baseUrl("http://author-service/api/authors/"+authorid)
				.build()
				.get()
				.accept(MediaType.APPLICATION_JSON)
				.exchangeToMono(e -> e.bodyToMono(ResponseAuthorFromService.class));

	}
	
	private Mono<ResponseAlbum> buildAlbum(
			ResponseAlbumFromService albumFromService,
			Mono<List<ResponseTrack>> tracks,
			Mono<String> albumDuration,
			Mono<Integer> tracksInAlbum,
			Mono<ResponseAuthorFromService> author
		) {
		return Mono.zip(tracks, albumDuration, tracksInAlbum, author)
		.map(t -> ResponseAlbum.builder()
				.id(albumFromService.getId())
				.albumType(albumFromService.getAlbumType())
				.author(t.getT4())
				.numberOfTracks(t.getT3())
				.totalDuration(t.getT2())
				.imageUrl(albumFromService.getImageUrl())
				.name(albumFromService.getName())
				.releaseDate(albumFromService.getReleaseDate())
				.tracks(t.getT1())
				.build());
		
	}

}
