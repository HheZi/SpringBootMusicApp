package com.gateway.filter;

import com.gateway.model.UserJwtPayload;
import com.gateway.utils.AuthUtils;
import com.gateway.utils.EndpointUtils;
import com.gateway.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static java.util.Objects.isNull;
import static org.springframework.util.StringUtils.hasText;

@RefreshScope
@Component
public class AuthenticationGatewayFilter implements GatewayFilter {

	@Autowired
	private JwtUtil jwtUtil;

	private final static String HEADER_USER_ID = "User-Id";

	private final static String HEADER_USER_ROLE = "User-Role";

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();

		String token = AuthUtils.getTokenFromHeader(request)
				.orElse(null);
		
		boolean isJwtTokenNotPresent = isNull(token);
		
		boolean isOpenClosedEndpoint = EndpointUtils.isOpenClosedEndpoints(request);
		boolean isOpenEndpoint = EndpointUtils.isOpenEndpoint(request);

		if (isOpenEndpoint || (isOpenClosedEndpoint && isJwtTokenNotPresent)) {
			return chain.filter(exchange);	
		}
		
		if (isJwtTokenNotPresent || jwtUtil.isExpired(token)) {
			return Mono.error(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
		}


		UserJwtPayload payload = jwtUtil.getUserJwtPayload(token);

		updateRequest(exchange, payload);

		return chain.filter(exchange);
	}

	private void updateRequest(
			ServerWebExchange exchange,
			UserJwtPayload payload
	) {
		exchange
				.getRequest()
				.mutate()
				.header(HEADER_USER_ID, payload.getUserId().toString())
				.header(HEADER_USER_ROLE, payload.getUserRole())
				.build();
	}

}
