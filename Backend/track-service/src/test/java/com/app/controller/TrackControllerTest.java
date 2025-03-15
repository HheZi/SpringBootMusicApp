package com.app.controller;

import com.app.exception.FileValidationException;
import com.app.kafka.consumer.KafkaAlbumConsumer;
import com.app.kafka.producer.KafkaTrackProducer;
import com.app.model.Track;
import com.app.payload.UploadTrack;
import com.app.payload.request.UpdateTrackRequest;
import com.app.service.AudioClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.file.Paths;
import java.util.UUID;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@EnableAutoConfiguration(exclude = KafkaAutoConfiguration.class)
@AutoConfigureWebTestClient
public class TrackControllerTest {

    @Autowired
    private WebTestClient testClient;

    @MockBean
    private AudioClient service;

    @MockBean
    private KafkaTrackProducer kafkaTrackProducer;

    @MockBean
    private KafkaAlbumConsumer kafkaAlbumConsumer;

    @Test
    public void test_pagination_when_correct_body(){
        testClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/tracks/")
                                .build()
                ).accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content")
                .isArray()
                .jsonPath("$.size", 4);
    }

    @Test
    public void test_pagination_when_track_id_include(){
        testClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/tracks/")
                        .queryParam("id", "1", "2")
                        .build()
                ).accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content[*].title").value(contains("test", "2test2"));
    }

    @Test
    public void test_pagination_when_album_id_include(){
        testClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/tracks/")
                        .queryParam("albumId", "1")
                        .build()
                ).accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content[*].title").value(contains("test", "2test2"));
    }

    @Test
    public void test_pagination_when_title_include(){
        testClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/tracks/")
                        .queryParam("name", "te")
                        .build()
                ).accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content[0].title").value(is("test"))
                .jsonPath("$.size", 2);
    }

    @Test
    public void test_count_tracks_by_album_id(){
        testClient.get()
                .uri("/api/tracks/count/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .isEqualTo(2);
    }

    @Test
    public void test_duration_of_track(){
        testClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/tracks/duration")
                        .queryParam("ids","1", "2")
                        .build()
                )
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void test_create_track(){
        var builder = new MultipartBodyBuilder();

        builder.part("title", "test4");
        builder.part("albumId", "2");
        builder.part("audio", new ClassPathResource("file.mp3"));

        UploadTrack uploadTrack = new UploadTrack();
        uploadTrack.setName(UUID.randomUUID());
        uploadTrack.setPath(Paths.get("temp", "file.mp3"));

        Mockito.when(service.saveAudio(any()))
                .thenReturn(Mono.just(uploadTrack));

        testClient.post()
                .uri(t -> t.path("/api/tracks/").build())
                .header("User-Role", "ADMIN")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    public void test_create_track_with_wrong_file(){
        var builder = new MultipartBodyBuilder();

        builder.part("title", "test4");
        builder.part("albumId", "2");
        builder.part("audio", new ClassPathResource("file"));

        Track track = new Track();
        track.setAudioName(UUID.randomUUID());
//
        Mockito.when(service.saveAudio(any()))
                .thenReturn(Mono.error(() -> new FileValidationException("Wrong format of file. Can be only MP3")));

        testClient.post()
                .uri(t -> t.path("/api/tracks/").build())
                .header("User-Role", "ADMIN")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.[0]").value(is("Wrong format of file. Can be only MP3"));
    }

    @Test
    public void test_create_track_when_not_admin(){
        var builder = new MultipartBodyBuilder();

        builder.part("title", "test4");
        builder.part("albumId", "2");
        builder.part("audio", new ClassPathResource("file"));

        Track track = new Track();
        track.setAudioName(UUID.randomUUID());

        testClient.post()
                .uri(t -> t.path("/api/tracks/").build())
                .header("User-Role", "USER")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    public void test_create_track_without_file(){
        var builder = new MultipartBodyBuilder();

        builder.part("title", "test4");
        builder.part("albumId", "2");

        Mockito.when(service.saveAudio(any()))
                .thenReturn(Mono.error(() -> new FileValidationException("Wrong format of file. Can be only MP3")));

        testClient.post()
                .uri(t -> t.path("/api/tracks/").build())
                .header("User-Role", "ADMIN")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.[0]").value(is("Audio file is not specified"));
    }

    @Test
    public void test_create_track_with_incorrect_body(){
        var builder = new MultipartBodyBuilder();

        builder.part("title", " ");
        builder.part("albumId", "2");
        builder.part("audio", new ClassPathResource("file"));


        Mockito.when(service.saveAudio(any()))
                .thenReturn(Mono.error(() -> new FileValidationException("Wrong format of file. Can be only MP3")));

        testClient.post()
                .uri(t -> t.path("/api/tracks/").build())
                .header("User-Role", "ADMIN")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.[0]").value(is("Title can't be blank"));
    }



    @Test
    public void test_update_track_title(){
        UpdateTrackRequest testNew = new UpdateTrackRequest("testNew");

        testClient.patch()
                .uri("/api/tracks/3")
                .bodyValue(testNew)
                .header("User-Role", "ADMIN")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void test_delete_track(){
        testClient.delete()
                .uri("/api/tracks/3")
                .header("User-Role", "ADMIN")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void test_delete_track_by_album_id(){
        testClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/api/tracks/")
                        .queryParam("albumId", "3")
                        .build()
                )
                .header("userId", "2")
                .exchange()
                .expectStatus().isOk();
    }
}