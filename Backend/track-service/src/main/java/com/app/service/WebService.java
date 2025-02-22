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

@Service
@RequiredArgsConstructor
public class WebService {

    private final WebClient.Builder webClient;

    public Mono<ResponseEntity<Void>> saveAudio(String name, File pathToTempAudio) {
        if(name  == null || pathToTempAudio == null) return Mono.empty();

        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part("file", new FileSystemResource(pathToTempAudio));
        builder.part("name", name);

        return webClient.build()
                .post().uri("http://audio-service/api/audio")
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .toBodilessEntity();

    }

}
