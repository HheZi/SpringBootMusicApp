package com.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gateway.payload.response.IsAdminResponse;
import com.gateway.utils.AuthUtils;
import com.gateway.utils.JwtUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RefreshScope
@Component
public class IsAdminFilter implements GatewayFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper mapper;

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();

        return Mono.justOrEmpty(AuthUtils.getTokenFromHeader(exchange.getRequest()))
                .flatMap(token -> {
                    if(!jwtUtil.isExpired(token))
                        return Mono.error(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
                    return Mono.just(token);
                })
                .map(token -> jwtUtil.getValueOfPayload(JwtUtil.USER_ROLE_KEY, token))
                .map(role -> Objects.equals(role, "ADMIN"))
                .switchIfEmpty(Mono.just(false))
                .map(IsAdminResponse::new)
                .flatMap(isAdminResponse -> Mono.fromCallable(
                            () -> mapper.writeValueAsBytes(isAdminResponse))
                        .map(response.bufferFactory()::wrap))
                .flatMap(dataBuffer -> response.writeWith(Mono.just(dataBuffer)));
    }
}
