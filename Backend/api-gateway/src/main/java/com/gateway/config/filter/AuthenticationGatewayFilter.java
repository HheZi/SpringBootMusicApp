package com.gateway.config.filter;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.gateway.utils.JwtUtil;

import reactor.core.publisher.Mono;

@RefreshScope
@Component
public class AuthenticationGatewayFilter implements GatewayFilter {

	private final AntPathMatcher pathMatcher = new AntPathMatcher();
	
	private final List<Endpoint> openEndpoints = List.of(
			new Endpoint("/api/auth/*", new HttpMethod[] { POST }),
			new Endpoint("/api/users/", new HttpMethod[] { POST }),
			new Endpoint("/api/audio/*", new HttpMethod[] { GET }),
			new Endpoint("/api/images/*", new HttpMethod[] { GET }),
			new Endpoint("/api/tracks/**", new HttpMethod[] { GET }),
			new Endpoint("/api/albums/**", new HttpMethod[] { GET }),
			new Endpoint("/api/authors/**", new HttpMethod[] { GET }),
			new Endpoint("/api/playlists/{[\\d+|\\bsymbol\\b|\\bowner\\b\\btracks\\b]}/**", 
					new HttpMethod[] { GET }),
			new Endpoint("/albums/*", new HttpMethod[] { GET })
		);

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();

		String token = getTokenFromHeader(request);
		
		boolean isJwtTokenNotPresent = isJwtTokenNotPresent(token);
		
		if ( (isPathOfTracks(request) && isJwtTokenNotPresent) || isEndpointNotSecured(request)) {
			return chain.filter(exchange);	
		}
		
		if (isJwtTokenNotPresent || isJwtExpired(token)) {
			return Mono.error(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
		}

		setUserIdHeader(exchange, jwtUtil.getValue("id", token));
		
		return chain.filter(exchange);
	}

	private void setUserIdHeader(ServerWebExchange exchange, String value) {
		exchange.getRequest()
		.mutate()
		.header("userId", value)
		.build();
	}
	
	private boolean isJwtExpired(String token) {
		return jwtUtil.isExpired(token);
	}
	
	private boolean isPathOfTracks(ServerHttpRequest request) {
		return isPathTheSame(request, "/tracks/*");
	}
	
	private boolean isJwtTokenNotPresent(String token) {
		return Objects.isNull(token);
	}

	private String getTokenFromHeader(ServerHttpRequest request) {
		try {
			List<String> vals = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
			return vals == null ? null : vals.get(0).split("\s+")[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	private boolean isEndpointNotSecured(ServerHttpRequest request) {
	    return openEndpoints.stream()
	            .anyMatch(endpoint -> isPathTheSame(request, endpoint.uri()) && isHttpMethodTheSame(request, endpoint.httpMethods()));
	}

	private boolean isPathTheSame(ServerHttpRequest request, String uri) {
		return pathMatcher.match(uri, request.getURI().getPath());
	}

	private boolean isHttpMethodTheSame(ServerHttpRequest request, HttpMethod[] httpMethods) {
		return Stream.of(httpMethods).anyMatch(method -> method.equals(request.getMethod()));
	}

}
