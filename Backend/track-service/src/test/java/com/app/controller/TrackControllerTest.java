package com.app.controller;

import com.app.payload.request.UpdateTrackRequest;
import com.app.service.WebService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.io.File;

import static org.hamcrest.Matchers.*;

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@AutoConfigureWebTestClient
public class TrackControllerTest {

    @Autowired
    private WebTestClient testClient;

    @MockBean
    private WebService service;

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
                .jsonPath("$.size", 3);
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

        Mockito.when(service.saveAudio(Mockito.any(String.class), Mockito.any(File.class)))
                .thenReturn(Mono.just(ResponseEntity.noContent().build()));

        testClient.post()
                .uri(t -> t.path("/api/tracks/").build())
                .header("userId", "2")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    public void test_update_track_title(){
        UpdateTrackRequest testNew = new UpdateTrackRequest("testNew");

        testClient.patch()
                .uri("/api/tracks/3")
                .bodyValue(testNew)
                .header("userId", "2")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void test_delete_track(){
        testClient.delete()
                .uri("/api/tracks/3")
                .header("userId", "2")
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