package com.app.service;

import com.app.exception.FileValidationException;
import com.app.exception.model.BadFileValidation;
import com.app.model.Track;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;

@Service
@RequiredArgsConstructor
public class AudioClient {

    private final WebClient.Builder webClient;

    public Mono<Track> saveAudio(Track track, File pathToTempAudio) {
        if(track.getAudioName() == null || pathToTempAudio == null) return Mono.empty();

        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part("file", new FileSystemResource(pathToTempAudio));
        builder.part("name", track.getAudioName().toString());

        return webClient.build()
                .post().uri("http://file-service/api/files/audio")
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handleBadRequestError)
                .toBodilessEntity()
                .map(voidResponseEntity -> track);

    }

    private Mono<? extends Throwable> handleBadRequestError(ClientResponse clientResponse){
        return clientResponse.bodyToMono(BadFileValidation.class)
                .flatMap(e ->  Mono.error(() -> new FileValidationException(e.getReason())));
    }

}
