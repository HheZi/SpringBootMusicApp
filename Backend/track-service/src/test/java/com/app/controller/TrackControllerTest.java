package com.app.controller;

import com.app.service.WebService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;

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
                .expectBody();
    }

    @Test
    public void test_count_tracks_by_album_id(){
        testClient.get()
                .uri("/api/tracks/count/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void test_duration_of_track(){
        testClient.get()
                .uri("/api/tracks/duration")
                .header("ids", "1", "2")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void test_create_track(){
        // ToDo Implement
    }

    @Test
    public void test_update_track(){
        // ToDo Implement
    }

    @Test
    public void test_delete_track(){
        // ToDo Implement
    }

    @Test
    public void test_delete_track_by_album_id(){
        // ToDo Implement
    }
}