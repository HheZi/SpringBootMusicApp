package com.gateway.config.filter.aggregation;

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

import reactor.core.publisher.Mono;

@Component
@RefreshScope
public class AlbumAggregationFilter implements GatewayFilter{

	@Autowired
	private WebClient.Builder builder;

	@Autowired
	private ObjectMapper mapper;
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String[] path = exchange.getRequest().getURI().getPath().split("/");
		
		String idString = path[path.length-1];
		try {
			Integer id = Integer.parseInt(idString);
			
			Mono<DataBuffer> resp = fetchAlbumsFromService(id)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.flatMap(t -> {
					Mono<String> albumDuration = fetchAlbumDuration(t.getId());
					Mono<Integer> tracksInAlbum = fetchTracksInAlbum(t.getId());
					Mono<ResponseAuthorFromService> author = fetchAuthorsFromService(t.getAuthorId());
					
					return buildAlbum(t, albumDuration, tracksInAlbum, author);
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
				.exchangeToMono(e -> e.bodyToMono(ResponseAlbumFromService.class))
				.filter(t -> t.getId() != null);
	}
	
	private Mono<String> fetchAlbumDuration(Integer id){ 
		return builder
				.baseUrl("http://track-service/api/tracks/duration")
				.build()
				.get()
				.uri(u -> u.queryParam("albumId", id).build())
				.accept(MediaType.APPLICATION_JSON)
				.exchangeToMono(e -> e.bodyToMono(ResponseAlbumDuration.class))
				.map(ResponseAlbumDuration::getDuration);
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
			Mono<String> albumDuration,
			Mono<Integer> tracksInAlbum,
			Mono<ResponseAuthorFromService> author
		) {
		return Mono.zip(albumDuration, tracksInAlbum, author)
		.map(t -> ResponseAlbum.builder()
				.id(albumFromService.getId())
				.albumType(albumFromService.getAlbumType())
				.author(t.getT3())
				.numberOfTracks(t.getT2())
				.totalDuration(t.getT1())
				.imageUrl(albumFromService.getImageUrl())
				.name(albumFromService.getName())
				.releaseDate(albumFromService.getReleaseDate())
				.build());
		
	}

}
