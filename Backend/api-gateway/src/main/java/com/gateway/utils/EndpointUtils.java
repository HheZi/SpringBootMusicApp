package com.gateway.utils;

import com.gateway.model.Endpoint;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@UtilityClass
public class EndpointUtils {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final List<Endpoint> openEndpoints = List.of(
            new Endpoint("/api/users/", new HttpMethod[] { POST }),
            new Endpoint("/api/auth/*", new HttpMethod[] { POST }),
            new Endpoint("/api/files/**", new HttpMethod[] { GET }),
            new Endpoint("/api/tracks/**", new HttpMethod[] { GET }),
            new Endpoint("/api/albums/*", new HttpMethod[] { GET }),
            new Endpoint("/api/albums/symbol/*", new HttpMethod[] { GET }),
            new Endpoint("/api/authors/*", new HttpMethod[] { GET }),
            new Endpoint("/api/authors/symbol/*", new HttpMethod[] { GET }),
            new Endpoint("/api/playlists/*", new HttpMethod[] { GET }),
            new Endpoint("/api/playlists/symbol/*", new HttpMethod[] { GET }),
            new Endpoint("/api/playlists/tracks/*", new HttpMethod[] { GET }),
            new Endpoint("/albums/*", new HttpMethod[] { GET })
    );

    private final List<Endpoint> openClosedEndpoints = List.of(
            new Endpoint("/tracks/*", new HttpMethod[] { GET }),
            new Endpoint("/api/playlists/owner/*", new HttpMethod[] { GET })
    );

    public static boolean  isOpenEndpoint(ServerHttpRequest request){
        return isEndpointTheSame(request, openEndpoints);
    }

    public static boolean isOpenClosedEndpoints(ServerHttpRequest request){
        return isEndpointTheSame(request, openClosedEndpoints);
    }

    private boolean isEndpointTheSame(ServerHttpRequest request, List<Endpoint> endpoints) {
        return endpoints.stream()
                .anyMatch(endpoint ->
                        isPathTheSame(request, endpoint.uri()) &&
                                isHttpMethodTheSame(request, endpoint.httpMethods())
                );
    }

    private boolean isPathTheSame(ServerHttpRequest request, String uri) {
        return pathMatcher.match(uri, request.getURI().getPath());
    }

    private boolean isHttpMethodTheSame(ServerHttpRequest request, HttpMethod[] httpMethods) {
        return Stream.of(httpMethods).anyMatch(method -> method.equals(request.getMethod()));
    }

}
