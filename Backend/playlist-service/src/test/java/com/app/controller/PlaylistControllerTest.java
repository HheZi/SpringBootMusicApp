package com.app.controller;

import com.app.service.WebService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@AutoConfigureWebTestClient
class PlaylistControllerTest {

    @Autowired
    private WebTestClient testClient;

    @MockitoBean
    private WebService service;

    @Test
    public void test_get_playlist() {
        testClient.get()
                .uri("/api/playlists/" + 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").value(is("test"));
    }

    @Test
    public void test_get_playlist_but_not_found() {
        testClient.get()
                .uri("/api/playlists/" + 100)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void test_get_tracks_of_playlist() {
        testClient.get()
                .uri("/api/playlists/tracks/" + 1)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Long.class)
                .contains(1L, 2L);
    }

    @Test
    public void test_get_playlist_by_name() {
        testClient.get()
                .uri("/api/playlists/symbol/te")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].name").value(is("test"));
    }

    @Test
    public void test_get_is_owner_of_playlist() {
        testClient.get()
                .uri("/api/playlists/owner/" + 1)
                .header("userId", "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .isEqualTo(true);
    }

    @Test
    public void test_get_is_owner_of_playlist_but_false_expected() {
        testClient.get()
                .uri("/api/playlists/owner/" + 1)
                .header("userId", "2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .isEqualTo(false);
    }

    @Test
    public void test_get_playlists_of_user() {
        testClient.get()
                .uri("/api/playlists/users/mine/")
                .header("userId", "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[*].name").value(contains("test", "2test"));
    }

    @Test
    public void test_get_playlists_of_user_but_not_found() {
        testClient.get()
                .uri("/api/playlists/mine/")
                .header("userId", "100")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void test_create_playlist(){
        Mockito.when(service.savePlaylistImage(Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(ResponseEntity.ok().build()));

        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part("name", "test with cover");
        builder.part("description", "test desc with cover");
        builder.part("cover", new ClassPathResource("testImage.jpeg"));

        testClient.post()
                .uri(t -> t.path("/api/playlists/").build())
                .header("userId", "2")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    public void test_create_playlist_without_file(){
        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part("name", "test without cover");
        builder.part("description", "test desc with cover");

        testClient.post()
                .uri(t -> t.path("/api/playlists/").build())
                .header("userId", "2")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    public void test_create_playlist_with_wrong_file(){
        Mockito.when(service.savePlaylistImage(Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(ResponseEntity.ok().build()));

        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part("name", "test with cover");
        builder.part("description", "test desc with cover");
        builder.part("cover", new ClassPathResource("testImage"));

        testClient.post()
                .uri(t -> t.path("/api/playlists/").build())
                .header("userId", "2")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void test_add_track_to_playlist(){
        testClient.patch()
                .uri("/api/playlists/1/7")
                .header("userId", "1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void test_add_track_to_playlist_but_already_exists(){
        testClient.patch()
                .uri("/api/playlists/1/1")
                .header("userId", "1")
                .exchange()
                .expectStatus().value(is(409));
    }

    @Test
    public void test_update_playlist(){
        Mockito.when(service.savePlaylistImage(Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(ResponseEntity.ok().build()));

        var builder = new MultipartBodyBuilder();

        builder.part("name", "Fifth");
        builder.part("description", "desc");
        builder.part("cover", new ClassPathResource("testImage.jpeg"));

        testClient.put()
                .uri(t -> t.path("/api/playlists/3").build())
                .header("userId", "2")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void test_update_playlist_with_wrong_file(){
        Mockito.when(service.savePlaylistImage(Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(ResponseEntity.ok().build()));

        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part("name", "test with cover");
        builder.part("description", "test desc with cover");
        builder.part("cover", new ClassPathResource("testImage"));

        testClient.put()
                .uri(t -> t.path("/api/playlists/3").build())
                .header("userId", "2")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void test_update_playlist_without_file(){

        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part("name", "test with cover");
        builder.part("description", "test desc with cover");

        testClient.put()
                .uri(t -> t.path("/api/playlists/3").build())
                .header("userId", "2")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void test_delete_track_from_playlist(){
        testClient.delete()
                .uri("/api/playlists/1/2")
                .header("userId", "1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void test_delete_cover_of_playlist(){
        testClient.delete()
                .uri("/api/playlists/cover/1")
                .header("userId", "1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void test_delete_playlist(){
        testClient.delete()
                .uri("/api/playlists/4")
                .header("userId", "3")
                .exchange()
                .expectStatus().isOk();
    }

}