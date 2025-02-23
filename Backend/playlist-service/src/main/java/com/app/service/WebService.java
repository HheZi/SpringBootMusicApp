package com.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WebService {

    private final WebClient.Builder builder;

    public Mono<ResponseEntity<Void>> savePlaylistImage(UUID name, File pathToFile) {
        if (name == null) Mono.empty();

        MultipartBodyBuilder multipartbuilder = new MultipartBodyBuilder();

        multipartbuilder.part("file", new FileSystemResource(pathToFile));
        multipartbuilder.part("name", name.toString());

        return builder.build()
                .post()
                .uri("http://image-service/api/images/")
                .body(BodyInserters.fromMultipartData(multipartbuilder.build()))
                .retrieve()
                .toBodilessEntity();

    }

}
